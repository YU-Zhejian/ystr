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

import com.github.yu_zhejian.ystr_demo.tinymap.ds.EncodedPositionsWithHashes;
import com.github.yu_zhejian.ystr_demo.tinymap.ds.IndexedPositions;
import htsjdk.samtools.reference.FastaSequenceIndex;
import htsjdk.samtools.reference.ReferenceSequenceFileFactory;

import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongBigArrayBigList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.objects.ObjectBigArrayBigList;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class GenomeIndexer {
    /** Logger. */
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
    /** Whether detailed statistics will be recorded. **/
    private final boolean recordStatistics;

    /**
     * Create a new genome indexer.
     *
     * @param fnaPath Path to input FASTA
     * @param outDirectory Output directory.
     * @param config The indexer configuration.
     * @param recordStatistics Whether detailed statistics will be recorded.
     *                         Will slow down the process.
     */
    public GenomeIndexer(
            final Path fnaPath,
            final Path outDirectory,
            final GenomeIndexerConfig config,
            final boolean recordStatistics) {
        this.outDirectory = outDirectory;
        this.fnaPath = fnaPath;
        this.config = config;
        this.recordStatistics = recordStatistics;
        gis = this.recordStatistics
                ? GenomeIndexStatistics.create()
                : GenomeIndexStatistics.createDumb();
    }

    public long appendToOutput(
            final @NotNull OutputStream w,
            final long hash,
            final @NotNull ByteArrayList encodedPositions)
            throws IOException {
        var wlen = 0L;

        var hashAndLen = LongEncoder.encodeLongs(hash, encodedPositions.size()).array();
        w.write(hashAndLen);
        wlen += hashAndLen.length;

        final var arr = encodedPositions.toByteArray();
        w.write(arr);
        wlen += arr.length;

        if(recordStatistics) {
            final var numPositions = encodedPositions.size() / ENCODED_POSITION_SIZE;
            gis.numPositionsPerMinimizer().addValue(numPositions);
            if (numPositions == 1) {
                gis.minimizerSingletonNumber().getAndIncrement();
            }
        }
        return wlen;
    }

    /**
     * Calculate Shannon entropy for all k-mers inside the entire chunk.
     *
     * @param string As described.
     * @param config As described.
     * @return As described.
     */
    private static @NotNull DoubleList calcShannonEntropy(
            final byte @NotNull [] string, final @NotNull GenomeIndexerConfig config) {
        var nts = new NtShannonEntropy();
        nts.attach(string, config.kmerSize());
        var retv = IterUtils.collect(nts);
        nts.detach();
        return retv;
    }

    /**
     * Filter hashes and shannon entropies.
     *
     * @param fwdHashes Forward hashes.
     * @param revHashes Reverse hashes.
     * @param shannonEntropies Shannon entropies.
     * @param offsetOfFirst As described.
     * @param config As described.
     * @return As described.
     */
    @Contract("_, _, _, _, _ -> new")
    private static @NotNull EncodedPositionsWithHashes filterHashesAndShannonEntropies(
        final @NotNull LongList fwdHashes,
        final LongList revHashes,
        final DoubleList shannonEntropies,
        final long offsetOfFirst,
        final GenomeIndexerConfig config) {
        var encodedPositions = new LongArrayList ();
        var hashes = new LongArrayList ();
        
        // Filtering and encoding of positions.
        for (int posOnChunk = 0; posOnChunk < fwdHashes.size(); posOnChunk++) {
            final long fwdHash = fwdHashes.getLong(posOnChunk);
            final long revHash = revHashes.getLong(posOnChunk);
            if (fwdHash == revHash || shannonEntropies.getDouble(posOnChunk) < config.ntShannonEntropyCutoff()) {
                continue;
            }
            if (fwdHash + Long.MIN_VALUE < revHash + Long.MIN_VALUE) {
                hashes.add(fwdHash);
                encodedPositions.add(posOnChunk + offsetOfFirst);
            } else {
                hashes.add(revHash);
                encodedPositions.add(-posOnChunk - offsetOfFirst);
            }
        }
        return new EncodedPositionsWithHashes(encodedPositions, hashes);
    }

        /**
         * Filter the EPH using minimizer algorithm.
         *
         * @param eph As described.
         * @param config As described.
         * @return As described.
         */
    @Contract("_, _ -> new")
    private static @NotNull EncodedPositionsWithHashes calcMinimizers(
        @NotNull EncodedPositionsWithHashes eph,
        final @NotNull GenomeIndexerConfig config) {

        // Calculating minimizers
        var minimizerIndices = MinimizerCalculator.getMinimizerPositions(
            eph.hashes(), config.numKmerPerMinimizer(), false);

        // Adding minimizers
        final var minimizers = new LongArrayList(minimizerIndices.size());
        final var encodedPositions = new LongArrayList(minimizerIndices.size());
        for (final int i : IterUtils.dedup(minimizerIndices)) {
            minimizers.add(eph.hashes().getLong(i));
            encodedPositions.add(eph.encodedPositions().getLong(i));
        }
        return new EncodedPositionsWithHashes(encodedPositions, minimizers);
    }

    private @NotNull Long2ObjectMap<IndexedPositions> constructMinimizerMap(
            final byte @NotNull [] string,
            final long contigID,
            final long offsetOfFirst,
            final @NotNull GenomeIndexerConfig config) {
        final var thisShannonEntropies = calcShannonEntropy(string, config);
        final var hashes = NtHashBase.hashOnBothDirections(
                new PrecomputedBidirectionalNtHash(), string.length, string, config.kmerSize(), 0);
        final var entropyFilteredEph = filterHashesAndShannonEntropies(hashes._1(), hashes._2(), thisShannonEntropies, offsetOfFirst, config);

        final var minimizerEph = calcMinimizers(entropyFilteredEph, config);

        if (recordStatistics){
            for (double entropy: thisShannonEntropies) {
                gis.shannonEntropy().addValue(entropy);
            }
            gis.numAllKmers().addAndGet(thisShannonEntropies.size());
            gis.numProcessedKmers().addAndGet(entropyFilteredEph.encodedPositions().size());
            var lastPos = offsetOfFirst;
            for (var i = 0; i < minimizerEph.hashes().size(); i++) {
                final long encodedPos = minimizerEph.encodedPositions().getLong(i);

                    final long realPos = Math.abs(encodedPos);
                    gis.minimizerDistances().addValue(realPos - lastPos);
                    lastPos = realPos;
                }
        }

        return minimizerEph.toIndexedPositionsMap(contigID);
    }

    /**
     * Prints out the progress of processing a chromosome.
     *
     * @param strLen The length of the chunk being processed.
     * @param contigID As described.
     * @param offsetOfFirst As described.
     * @param nthPart The index of the chunk being processed.
     * @param trailing Other information to print.
     */
    private void fmtLogChrSplit(
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

    /**
     * Generates hashes for a chromosome chunk.
     *
     * @param string The chromosome chunk to process.
     * @param contigID As described.
     * @param offsetOfFirst 0-based inclusive offset of the first base of the chunk on the chromosome.
     * @param nthPart As described.
     * @throws IOException As described.
     */
    public void generateHashesChrSplit(
            final byte @NotNull [] string,
            final long contigID,
            final long offsetOfFirst,
            final int nthPart)
            throws IOException {
        fmtLogChrSplit(string.length, contigID, offsetOfFirst, nthPart, "Calculating minimizers");
        var encodedHOffsetDict =
                constructMinimizerMap(string, contigID, offsetOfFirst, config);
        gis.numMinimizers().addAndGet(encodedHOffsetDict.size());

        fmtLogChrSplit(string.length, contigID, offsetOfFirst, nthPart, "Writing minimizers");

        final var outFileName = "%d.%d.idx.bin".formatted(contigID, nthPart);
        final var outFile = Path.of(outDirectory.toString(), outFileName)
                .toFile();
        lock.lock();
        contigIndexPaths.get(contigID).add(outFileName);
        lock.unlock();

        var chunkIndexSize = 0L;
        try (final var w = new FileOutputStream(outFile)) {
            for (var entry : encodedHOffsetDict.long2ObjectEntrySet()) {
                chunkIndexSize += appendToOutput(w, entry.getLongKey(), entry.getValue());
            }
        }
        gis.finalIndexSize().addAndGet(chunkIndexSize);
        fmtLogChrSplit(string.length, contigID, offsetOfFirst, nthPart, "Finished");
    }

    private void indexChrSplit() {
        final var refi = new FastaSequenceIndex(new File(fnaPath + ".fai"));

        for (final var refSpec : refi) {
            contigLens.add(refSpec.getSize());
            contigNames.add(refSpec.getContig());
            contigIndexPaths.add(new ObjectBigArrayBigList<>());
            gis.contigLens().addValue(refSpec.getSize());
        }
        try (var ref = ReferenceSequenceFileFactory.getReferenceSequenceFile(fnaPath.toFile())) {
            for (var contigID = 0L; contigID < contigNames.size64(); contigID++) {
                var offsetOfFirst = 0L;
                var offsetOfLast = 0L;
                var nthPart = 0;
                final var contigLen = contigLens.getLong(contigID);

                do {
                    offsetOfLast = Long.min(
                        offsetOfFirst + CHR_SPLIT_SEG_LEN + config.kmerSize() + 1,
                        contigLen
                    );
                    final byte[] finalSeq = ref.getSubsequenceAt(
                                    contigNames.get(contigID),
                            offsetOfFirst,
                            offsetOfLast)
                            .getBases();
                    generateHashesChrSplit(finalSeq, contigID, offsetOfFirst, nthPart);
                    offsetOfFirst += CHR_SPLIT_SEG_LEN;
                    nthPart++;
                } while (offsetOfFirst < contigLen);
            }
        } catch (IOException e) {
            throw new RuntimeIOException("", e);
        }
    }

    /**
     * Construct the index and write the index to disk.
     *
     * @throws IOException As described.
     * @return The index.
     */
    public @NotNull GenomeIndex index() throws IOException {
        Files.createDirectories(outDirectory);
        indexChrSplit();
        gis.printStatistics();
        final var gi = new GenomeIndex(
            config,
            fnaPath.toAbsolutePath().toString(),
            contigLens,
            contigNames,
            contigIndexPaths);
        gi.toDisk(Path.of(outDirectory.toString(), "index.json"));
        return gi;
    }

    /**
     * @deprecated Test method that should be removed before publishing.
     * @param args As described.
     * @throws IOException As described.
     */
    @Deprecated(forRemoval = true)
    public static void main(String[] args) throws IOException {
        var basePath = "F:\\home\\Documents\\ystr\\test\\ref";
        var fnaPath = Path.of(basePath, "ce11.genomic.fna");
        var conf = GenomeIndexerConfig.minimap2();
        var gi = new GenomeIndexer(fnaPath, Path.of(fnaPath + ".d"), conf, false);
        gi.index();
    }
}
