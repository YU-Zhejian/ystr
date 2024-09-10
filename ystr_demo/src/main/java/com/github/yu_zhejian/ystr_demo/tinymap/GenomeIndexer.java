package com.github.yu_zhejian.ystr_demo.tinymap;

import com.github.yu_zhejian.ystr.io.LongEncoder;
import com.github.yu_zhejian.ystr.io.RuntimeIOException;
import com.github.yu_zhejian.ystr.minimizer.MinimizerCalculator;
import com.github.yu_zhejian.ystr.rolling.NtHashBase;
import com.github.yu_zhejian.ystr.rolling.NtShannonEntropy;
import com.github.yu_zhejian.ystr.rolling.PrecomputedBidirectionalNtHash;
import com.github.yu_zhejian.ystr.utils.FrontendUtils;
import com.github.yu_zhejian.ystr.utils.IterUtils;
import com.github.yu_zhejian.ystr.utils.LogUtils;

import htsjdk.samtools.reference.FastaSequenceIndex;
import htsjdk.samtools.reference.ReferenceSequenceFileFactory;

import io.vavr.Tuple;
import io.vavr.Tuple2;

import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongBigArrayBigList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.objects.ObjectBigArrayBigList;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class GenomeIndexer {
    private static final Logger LH = LoggerFactory.getLogger(GenomeIndexer.class.getSimpleName());

    /** 512 Mbp per segment for large contigs. */
    private static final int CHR_SPLIT_SEG_LEN = (1 << 28);

    /** Length of encoded positions. */
    private static final int ENCODED_POSITION_SIZE = 2 * Long.BYTES;

    private final Path outDirectory;
    /** @see GenomeIndex * */
    private final GenomeIndexerConfig config;
    /** @see GenomeIndex * */
    private final Path fnaPath;
    /** @see GenomeIndex * */
    private final LongBigArrayBigList contigLens = new LongBigArrayBigList();
    /** @see GenomeIndex * */
    private final BigList<String> contigNames = new ObjectBigArrayBigList<>();
    /** @see GenomeIndex * */
    private final BigList<BigList<String>> contigIndexPaths = new ObjectBigArrayBigList<>();

    /** Lock that helps {@link #contigIndexPaths} * */
    private final Lock lock = new ReentrantLock();
    /** Statistics of the index and indexing process. * */
    private final GenomeIndexStatistics gis;

    public GenomeIndexer(
            final Path fnaPath,
            final Path outDirectory,
            final GenomeIndexerConfig config,
            final boolean recordStatistics) {
        this.outDirectory = outDirectory;
        this.fnaPath = fnaPath;
        this.config = config;
        gis = recordStatistics
                ? GenomeIndexStatistics.create()
                : GenomeIndexStatistics.createDumb();
    }

    public long appendToOutput(
            final @NotNull OutputStream w,
            final long hash,
            final @NotNull ByteArrayList encodedPositions)
            throws IOException {
        var wlen = 0L;
        var numPositions = encodedPositions.size() / ENCODED_POSITION_SIZE;
        gis.numPositionsPerMinimizer().addValue(numPositions);
        if (numPositions == 1) {
            gis.minimizerSingletonNumber().getAndIncrement();
        }
        var hashAndLen = LongEncoder.encodeLong(hash, encodedPositions.size()).toByteArray();
        w.write(hashAndLen);
        wlen += hashAndLen.length;
        var arr = encodedPositions.toByteArray();
        w.write(arr);
        wlen += arr.length;
        return wlen;
    }

    private static @NotNull DoubleList calcShannonEntropy(
            final byte @NotNull [] string, final @NotNull GenomeIndexerConfig config) {
        var nts = new NtShannonEntropy();
        nts.attach(string, config.kmerSize());
        var retv = IterUtils.collect(nts);
        nts.detach();
        return retv;
    }

    private static @NotNull Tuple2<LongArrayList, LongArrayList> calcMinimizers(
            final @NotNull Tuple2<LongList, LongList> hashes,
            final @NotNull IntList passingIdx,
            final long offsetOfFirst,
            final GenomeIndexerConfig config) {
        var fwdHashes = hashes._1();
        var revHashes = hashes._2();
        var passedNtHashes = new LongArrayList(passingIdx.size());
        var encodedPositionsOfPassedNtHashes = new LongArrayList(passingIdx.size());

        // Filtering and encoding of positions.
        for (int i = 0; i < passingIdx.size(); i++) {
            final int pos = passingIdx.getInt(i);
            long fwdHash = fwdHashes.getLong(pos);
            long revHash = revHashes.getLong(pos);
            if (fwdHash == revHash) {
                continue;
            }
            if (fwdHash + Long.MIN_VALUE < revHash + Long.MIN_VALUE) {
                passedNtHashes.add(fwdHash);
                encodedPositionsOfPassedNtHashes.add(i + offsetOfFirst);
            } else {
                passedNtHashes.add(revHash);
                encodedPositionsOfPassedNtHashes.add(-i - offsetOfFirst);
            }
        }

        // Calculating minimizers
        var minimizerIndices = MinimizerCalculator.getMinimizerPositions(
                passedNtHashes, config.numKmerPerMinimizer(), false);

        // Adding minimizers
        var minimizers = new LongArrayList(minimizerIndices.size());
        var encodedPositionsOfMinimizers = new LongArrayList(minimizerIndices.size());
        for (int i : IterUtils.dedup(minimizerIndices)) {
            minimizers.add(passedNtHashes.getLong(i));
            encodedPositionsOfMinimizers.add(encodedPositionsOfPassedNtHashes.getLong(i));
        }
        return Tuple.of(encodedPositionsOfMinimizers, minimizers);
    }

    public static @NotNull Long2ObjectOpenHashMap<ByteArrayList> constructMinimizerMap(
            final byte @NotNull [] string,
            final long contigID,
            final long offsetOfFirst,
            final @NotNull GenomeIndexerConfig config,
            final GenomeIndexStatistics gis) {
        var thisShannonEntropy = calcShannonEntropy(string, config);
        for (int i = 0; i < thisShannonEntropy.size(); i++) {
            gis.shannonEntropy().addValue(thisShannonEntropy.getDouble(i));
        }
        gis.numAllKmers().addAndGet(thisShannonEntropy.size());

        var passingIdx =
                IterUtils.where(thisShannonEntropy, i -> i > config.ntShannonEntropyCutoff());
        gis.numProcessedKmers().addAndGet(passingIdx.size());

        var hashes = NtHashBase.hashOnBothDirections(
                new PrecomputedBidirectionalNtHash(), string.length, string, config.kmerSize(), 0);

        var minimizerSpec = calcMinimizers(hashes, passingIdx, offsetOfFirst, config);

        var positions = minimizerSpec._1();
        var minimizers = minimizerSpec._2();

        var encodedHashOffsetDict = new Long2ObjectOpenHashMap<ByteArrayList>();

        var lastPos = offsetOfFirst;
        for (var i = 0; i < minimizers.size(); i++) {
            final long encodedPos = positions.getLong(i);
            final long realPos = Math.abs(encodedPos);
            gis.minimizerDistances().addValue(realPos - lastPos);
            lastPos = realPos;

            final long hashValue = minimizers.getLong(i);
            encodedHashOffsetDict.computeIfAbsent(hashValue, k -> new ByteArrayList());
            encodedHashOffsetDict
                    .get(hashValue)
                    .addAll(LongEncoder.encodeLong(contigID, encodedPos));
        }

        return encodedHashOffsetDict;
    }

    public void fmtLogChrSplit(
            final int strLen,
            final long contigID,
            final long offsetOfFirst,
            final int nthPart,
            final String trailing) {
        LH.info(
                "PROCESS {}:{} {} -> {} {}",
                LogUtils.lazy(() -> contigNames.get(contigID)),
                nthPart,
                LogUtils.lazy(() -> FrontendUtils.toHumanReadable(offsetOfFirst, "bp")),
                LogUtils.lazy(() -> FrontendUtils.toHumanReadable(offsetOfFirst + strLen, "bp")),
                trailing);
    }

    public void generateHashesChrSplit(
            final byte @NotNull [] string,
            final long contigID,
            final long offsetOfFirst,
            final int nthPart)
            throws IOException {
        var contigName = contigNames.get(contigID);
        fmtLogChrSplit(string.length, contigID, offsetOfFirst, nthPart, "Calculating minimizers");
        var encodedHOffsetDict =
                constructMinimizerMap(string, contigID, offsetOfFirst, config, gis);

        fmtLogChrSplit(string.length, contigID, offsetOfFirst, nthPart, "Writing minimizers");

        var out = Path.of(outDirectory.toString(), "%s.%d.idx.bin".formatted(contigName, nthPart))
                .toFile();
        lock.lock();
        contigIndexPaths.get(contigID).add(out.getAbsolutePath());
        lock.unlock();

        try (var w = new FileOutputStream(out)) {
            gis.numMinimizers().addAndGet(encodedHOffsetDict.size());

            for (var entry : encodedHOffsetDict.long2ObjectEntrySet()) {
                gis.fimalIndexSize()
                        .addAndGet(appendToOutput(w, entry.getLongKey(), entry.getValue()));
            }
        }
        fmtLogChrSplit(string.length, contigID, offsetOfFirst, nthPart, "Finished");
    }

    private void indexChrSplit() {
        var refi = new FastaSequenceIndex(new File(fnaPath + ".fai"));
        for (var refSpec : refi) {
            contigLens.add(refSpec.getSize());
            contigNames.add(refSpec.getContig());
            contigIndexPaths.add(new ObjectBigArrayBigList<>());
            gis.contigLens().addValue(refSpec.getSize());
        }
        try (var ref = ReferenceSequenceFileFactory.getReferenceSequenceFile(fnaPath.toFile())) {
            for (var i = 0L; i < contigNames.size64(); i++) {
                var fromfwd = 1L;
                var tofwd = fromfwd + CHR_SPLIT_SEG_LEN + config.kmerSize();
                var nthPart = 0;
                do {
                    final byte[] finalSeq = ref.getSubsequenceAt(
                                    contigNames.get(i),
                                    fromfwd - 1, // Due to 1-based indexing
                                    Long.min(tofwd, contigLens.getLong(i)))
                            .getBases();
                    generateHashesChrSplit(finalSeq, i, tofwd, nthPart);
                    fromfwd += CHR_SPLIT_SEG_LEN;
                    tofwd += CHR_SPLIT_SEG_LEN;
                    nthPart++;
                } while (tofwd <= contigLens.getLong(i));
            }
        } catch (IOException e) {
            throw new RuntimeIOException("", e);
        }
    }

    public void index() throws IOException {
        Files.createDirectories(outDirectory);
        indexChrSplit();
        gis.printStatistics();
        (new GenomeIndex(
                        config,
                        fnaPath.toAbsolutePath().toString(),
                        contigLens,
                        contigNames,
                        contigIndexPaths))
                .toDisk(Path.of(outDirectory.toString(), "index.json"));
    }

    /**
     * @deprecated Test method that should be removed before publishing.
     * @param args As described.
     * @throws IOException As described.
     */
    @Deprecated(forRemoval = true)
    public static void main(String[] args) throws IOException {
        var basePath = "D:\\Work\\ystr\\test\\ref";
        var fnaPath = Path.of(basePath, "ce11.genomic.fna");
        var conf = GenomeIndexerConfig.minimap2();
        var gi = new GenomeIndexer(fnaPath, Path.of(fnaPath + ".d"), conf, false);
        gi.index();
    }
}
