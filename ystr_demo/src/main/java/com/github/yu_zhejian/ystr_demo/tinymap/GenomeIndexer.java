package com.github.yu_zhejian.ystr_demo.tinymap;

import com.github.yu_zhejian.ystr.IterUtils;
import com.github.yu_zhejian.ystr.io.FastxIterator;
import com.github.yu_zhejian.ystr.io.FastxRecord;
import com.github.yu_zhejian.ystr.io.LongEncoder;
import com.github.yu_zhejian.ystr.rolling.NtHashBase;
import com.github.yu_zhejian.ystr.rolling.NtShannonEntropy;
import com.github.yu_zhejian.ystr.rolling.PrecomputedBidirectionalNtHash;
import com.github.yu_zhejian.ystr.utils.MinimizerCalculator;
import com.github.yu_zhejian.ystr_demo.FrontendUtils;
import com.github.yu_zhejian.ystr_demo.LogUtils;

import htsjdk.samtools.reference.FastaSequenceIndex;
import htsjdk.samtools.reference.ReferenceSequenceFileFactory;

import io.vavr.Tuple;
import io.vavr.Tuple2;

import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongBigArrayBigList;
import it.unimi.dsi.fastutil.objects.ObjectBigArrayBigList;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public final class GenomeIndexer {
    private final GenomeIndexerConfig config;
    private static final Logger LH = LoggerFactory.getLogger(GenomeIndexer.class.getSimpleName());

    /** 512 Mbp per segment for {@link IndexType#CHR_SPLIT_IDX}. */
    private static final int CHR_SPLIT_SEG_LEN = (1 << 28);
    /** Number of sequences to process per {@link IndexType#UNIFIED_SPLIT_IDX}. */
    private static final int UNIFIED_SPLIT_SEG_LEN = (1 << 14);
    /** Length of encoded positions. */
    private static final int ENCODED_POSITION_SIZE = 2 * Long.BYTES;

    private final Path fnaPath;
    private LongBigArrayBigList contigLens;
    private BigList<String> contigNames;

    private final AtomicLong numMinimizers;
    private final AtomicLong numAllKmers;
    private final AtomicLong numProcessedKmers;
    private final AtomicLong fimalIndexSize;
    private final SummaryStatistics numPositionsPerMinimizer;
    private final SummaryStatistics minimizerDistances;
    private final SummaryStatistics shannonEntropy;
    private final AtomicLong minimizerSingletonNumber;

    public GenomeIndexer(Path fnaPath, GenomeIndexerConfig config) {
        this.fnaPath = fnaPath;
        this.config = config;
        numMinimizers = new AtomicLong(0);
        numPositionsPerMinimizer = new SummaryStatistics();
        minimizerSingletonNumber = new AtomicLong(0);
        minimizerDistances = new SummaryStatistics();
        shannonEntropy = new SummaryStatistics();
        fimalIndexSize = new AtomicLong(0);
        numAllKmers = new AtomicLong(0);
        numProcessedKmers = new AtomicLong(0);
    }

    public long appendToOutput(
            @NotNull OutputStream w, long hash, @NotNull ByteArrayList encodedPositions)
            throws IOException {
        var wlen = 0L;
        var numPositions = encodedPositions.size() / ENCODED_POSITION_SIZE;
        numPositionsPerMinimizer.addValue(numPositions);
        if (numPositions == 1) {
            minimizerSingletonNumber.getAndIncrement();
        }
        var hashAndLen = LongEncoder.encodeLong(hash, encodedPositions.size()).toByteArray();
        w.write(hashAndLen);
        wlen += hashAndLen.length;
        var arr = encodedPositions.toByteArray();
        w.write(arr);
        wlen += arr.length;
        return wlen;
    }

    private static @NotNull DoubleArrayList calcShannonEntropy(
            byte[] string, @NotNull GenomeIndexerConfig config) {
        var thisShannonEntropy = new DoubleArrayList();
        var nts = new NtShannonEntropy(string, config.kmerSize(), 0);
        while (nts.hasNext()) {
            thisShannonEntropy.add(nts.nextDouble());
        }
        return thisShannonEntropy;
    }

    private static @NotNull Tuple2<LongArrayList, LongArrayList> calcMinimizers(
            @NotNull Tuple2<LongArrayList, LongArrayList> hashes,
            @NotNull IntArrayList passingIdx,
            long offsetOfFirst,
            GenomeIndexerConfig config) {
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
        for (int i : IterUtils.dedup((IntArrayList) minimizerIndices)) {
            minimizers.add(passedNtHashes.getLong(i));
            encodedPositionsOfMinimizers.add(encodedPositionsOfPassedNtHashes.getLong(i));
        }
        return Tuple.of(encodedPositionsOfMinimizers, minimizers);
    }

    /**
     * Method to hash an entire string.
     *
     * @param string As described.
     * @param config As described.
     * @return Mapping of hashes and their encoded positions.
     */
    public static @NotNull Long2ObjectOpenHashMap<LongArrayList> constructMinimizerMap(
            byte @NotNull [] string, @NotNull GenomeIndexerConfig config) {
        var thisShannonEntropy = calcShannonEntropy(string, config);

        var passingIdx =
                IterUtils.where(thisShannonEntropy, (i) -> i > config.ntShannonEntropyCutoff());
        var hashes = NtHashBase.getAllBothHash(
                new PrecomputedBidirectionalNtHash(string, config.kmerSize(), 0), string.length);

        var minimizerSpec = calcMinimizers(hashes, passingIdx, 0, config);

        var positions = minimizerSpec._1();
        var minimizers = minimizerSpec._2();
        var hashOffsetDict = new Long2ObjectOpenHashMap<LongArrayList>();

        for (var i = 0; i < minimizers.size(); i++) {
            long hashValue = minimizers.getLong(i);
            hashOffsetDict.computeIfAbsent(hashValue, k -> new LongArrayList());
            hashOffsetDict.get(hashValue).add(positions.getLong(i));
        }
        return hashOffsetDict;
    }

    private @NotNull Long2ObjectOpenHashMap<ByteArrayList> constructMinimizerMap(
            byte @NotNull [] string, long contigID, long offsetOfFirst) {
        var thisShannonEntropy = calcShannonEntropy(string, config);
        for (int i = 0; i < thisShannonEntropy.size(); i++) {
            shannonEntropy.addValue(thisShannonEntropy.getDouble(i));
        }
        numAllKmers.addAndGet(thisShannonEntropy.size());

        var passingIdx =
                IterUtils.where(thisShannonEntropy, i -> i > config.ntShannonEntropyCutoff());
        numProcessedKmers.addAndGet(passingIdx.size());

        var hashes = NtHashBase.getAllBothHash(
                new PrecomputedBidirectionalNtHash(string, config.kmerSize(), 0), string.length);

        var minimizerSpec = calcMinimizers(hashes, passingIdx, offsetOfFirst, config);

        var positions = minimizerSpec._1();
        var minimizers = minimizerSpec._2();

        var encodedHashOffsetDict = new Long2ObjectOpenHashMap<ByteArrayList>();

        var lastPos = offsetOfFirst;
        for (var i = 0; i < minimizers.size(); i++) {
            var encodedPos = positions.getLong(i);
            var realPos = Math.abs(encodedPos);
            minimizerDistances.addValue(realPos - lastPos);
            lastPos = realPos;

            long hashValue = minimizers.getLong(i);
            encodedHashOffsetDict.computeIfAbsent(hashValue, k -> new ByteArrayList());
            encodedHashOffsetDict
                    .get(hashValue)
                    .addAll(LongEncoder.encodeLong(contigID, encodedPos));
        }

        return encodedHashOffsetDict;
    }

    public void fmtLogChrSplit(
            int strLen, long contigID, long offsetOfFirst, int nthPart, String trailing) {
        LH.info(
                "PROCESS {}:{} {} -> {} {}",
                contigNames.get(contigID),
                nthPart,
                LogUtils.lazy(() -> FrontendUtils.toHumanReadable(offsetOfFirst, "bp")),
                LogUtils.lazy(() -> FrontendUtils.toHumanReadable(offsetOfFirst + strLen, "bp")),
                trailing);
    }

    public void fmtLogUnifiedSplit(List<FastxRecord> records, long batchID, String trailing) {
        LH.info(
                "PROCESS batch {} {} -> {} {}",
                batchID,
                LogUtils.lazy(() -> records.get(0).seqid()),
                LogUtils.lazy(() -> records.get(records.size() - 1).seqid()),
                trailing);
    }

    public void generateHashesChrSplit(
            byte @NotNull [] string, long contigID, long offsetOfFirst, int nthPart)
            throws IOException {
        var contigName = contigNames.get(contigID);
        fmtLogChrSplit(string.length, contigID, offsetOfFirst, nthPart, "Calculating minimizers");
        var encodedHOffsetDict = constructMinimizerMap(string, contigID, offsetOfFirst);

        fmtLogChrSplit(string.length, contigID, offsetOfFirst, nthPart, "Writing minimizers");

        var out = Path.of("%s.%d.%s.idx.bin".formatted(fnaPath.toString(), nthPart, contigName))
                .toFile();
        try (var w = new FileOutputStream(out)) {
            numMinimizers.addAndGet(encodedHOffsetDict.size());

            for (var entry : encodedHOffsetDict.long2ObjectEntrySet()) {
                fimalIndexSize.addAndGet(appendToOutput(w, entry.getLongKey(), entry.getValue()));
            }
        }
        fmtLogChrSplit(string.length, contigID, offsetOfFirst, nthPart, "Finished");
    }

    /** Generating indices using {@link IndexType#UNIFIED_SPLIT_IDX}. */
    public void generateHashesUnifiedSplit(@NotNull List<FastxRecord> records, long batchID)
            throws IOException {
        fmtLogUnifiedSplit(records, batchID, "Started");
        var hashEncodedOffsetDict = new Long2ObjectOpenHashMap<ByteArrayList>();
        for (var record : records) {
            var contigID = contigNames.size64();
            contigNames.add(record.seqid());
            contigLens.add(record.seq().length);
            if (record.seq().length < config.kmerSize()) {
                LH.warn("SEQ {} too short; Skipped", record.seqid());
            }
            for (var entry :
                    constructMinimizerMap(record.seq(), contigID, 0).long2ObjectEntrySet()) {
                var minimizer = entry.getLongKey();
                var encodedPositions = entry.getValue();
                hashEncodedOffsetDict.computeIfAbsent(
                        minimizer,
                        k -> new ByteArrayList(entry.getValue().size() * ENCODED_POSITION_SIZE));
                hashEncodedOffsetDict.get(minimizer).addAll(encodedPositions);
            }
        }
        fmtLogUnifiedSplit(records, batchID, "Writing");
        var out =
                Path.of("%s.%d.idx.bin".formatted(fnaPath.toString(), batchID)).toFile();
        numMinimizers.addAndGet(hashEncodedOffsetDict.size());
        try (var w = new FileOutputStream(out)) {
            for (var entry : hashEncodedOffsetDict.long2ObjectEntrySet()) {
                fimalIndexSize.addAndGet(appendToOutput(w, entry.getLongKey(), entry.getValue()));
            }
        }

        fmtLogUnifiedSplit(records, batchID, "Finished");
    }

    /** Generating indices using {@link IndexType#CHR_SPLIT_IDX}. */
    private void indexChrSplit() {
        var refi = new FastaSequenceIndex(new File(fnaPath + ".fai"));
        contigLens = new LongBigArrayBigList();
        contigNames = new ObjectBigArrayBigList<>();
        for (var refSpec : refi) {
            contigLens.add(refSpec.getSize());
            contigNames.add(refSpec.getContig());
        }
        try (var ref = ReferenceSequenceFileFactory.getReferenceSequenceFile(fnaPath.toFile())) {
            for (var i = 0L; i < contigNames.size64(); i++) {
                var fromfwd = 1L;
                var tofwd = fromfwd + CHR_SPLIT_SEG_LEN + config.kmerSize();
                var nthPart = 0;
                do {
                    final long finalFromfwd = fromfwd - 1; // Due to 1-based indexing
                    final long finalTofwd = tofwd;
                    final int finalNthPart = nthPart;
                    final byte[] finalSeq = ref.getSubsequenceAt(
                                    contigNames.get(i),
                                    finalFromfwd,
                                    Long.min(finalTofwd, contigLens.getLong(i)))
                            .getBases();
                    generateHashesChrSplit(finalSeq, i, finalFromfwd, finalNthPart);
                    fromfwd += CHR_SPLIT_SEG_LEN;
                    tofwd += CHR_SPLIT_SEG_LEN;
                    nthPart++;
                } while (tofwd <= contigLens.getLong(i));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void indexUnifiedSplit() {
        var batchID = 0L;
        contigLens = new LongBigArrayBigList();
        contigNames = new ObjectBigArrayBigList<>();
        try (var ref = FastxIterator.read(this.fnaPath)) {
            var batches = IterUtils.window(ref, UNIFIED_SPLIT_SEG_LEN);
            while (batches.hasNext()) {
                generateHashesUnifiedSplit(batches.next(), batchID);
                batchID++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void index() {
        if (IndexType.CHR_SPLIT_IDX.equals(config.indexType())) {
            indexChrSplit();
        } else {
            indexUnifiedSplit();
        }
        printStatistics();
    }

    private void printStatistics() {
        var contigLensStatistics = new SummaryStatistics();
        contigLens.forEach(contigLensStatistics::addValue);
        LH.info(
                "FINAL contig number: {}, sizes: {}",
                LogUtils.lazy(() -> FrontendUtils.toHumanReadable(contigLens.size64(), "")),
                LogUtils.lazy(
                        LogUtils.summarizeStatisticsToHumanReadable(contigLensStatistics, "bp")));
        LH.info(
                "FINAL k-mers: all {} ; processed {} ({})",
                FrontendUtils.toHumanReadable(numAllKmers, ""),
                FrontendUtils.toHumanReadable(numProcessedKmers, ""),
                LogUtils.lazy(LogUtils.calcPctLazy(numProcessedKmers, numAllKmers)));
        LH.info("FINAL index size: {}", FrontendUtils.toHumanReadable(fimalIndexSize, "B"));
        LH.info(
                "FINAL NT entropy: {}",
                LogUtils.lazy(LogUtils.summarizeStatisticsWithFormatStr(shannonEntropy, "%.4f")));
        LH.info(
                "FINAL positions per minimizer: {}",
                LogUtils.lazy(
                        LogUtils.summarizeStatisticsToHumanReadable(numPositionsPerMinimizer, "")));
        LH.info(
                "FINAL minimizer distances: {}",
                LogUtils.lazy(
                        LogUtils.summarizeStatisticsToHumanReadable(minimizerDistances, "bp")));
        LH.info(
                "FINAL distinct minimizers: {}, singletons: {} ({})",
                FrontendUtils.toHumanReadable(numMinimizers, ""),
                FrontendUtils.toHumanReadable(minimizerSingletonNumber, ""),
                LogUtils.lazy(LogUtils.calcPctLazy(minimizerSingletonNumber, numMinimizers)));
    }

    static void main1() {
        var basePath = "F:\\home\\Documents\\ystr\\test";
        var fnaPath = Path.of(basePath, "c_elegans_ests.fa");
        var conf = GenomeIndexerConfig.blast();
        var gi = new GenomeIndexer(fnaPath, conf);
        gi.index();
    }

    static void main2() {
        var basePath = "F:\\home\\Documents\\ystr\\test";
        var fnaPath = Path.of(basePath, "ce11.genomic.fna");
        var conf = GenomeIndexerConfig.minimap2();
        var gi = new GenomeIndexer(fnaPath, conf);
        gi.index();
    }

    public static void main(String[] args) {
        main1();
        main2();
    }
}
