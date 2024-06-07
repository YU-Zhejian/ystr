package com.github.yu_zhejian.ystr_demo;

import com.github.yu_zhejian.ystr.IterUtils;
import com.github.yu_zhejian.ystr.rolling.MinimizerCalculator;
import com.github.yu_zhejian.ystr.rolling.NtHashBase;
import com.github.yu_zhejian.ystr.rolling.NtShannonEntropy;
import com.github.yu_zhejian.ystr.rolling.PrecomputedBidirectionalNtHash;

import htsjdk.samtools.reference.FastaSequenceIndex;
import htsjdk.samtools.reference.FastaSequenceIndexEntry;
import htsjdk.samtools.reference.ReferenceSequenceFileFactory;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class GenomeIndexer {
    private final DescriptiveStatistics numPositionsPerMinimizer;
    private final DescriptiveStatistics minimizerDistances;
    private final DescriptiveStatistics shannonEntropy;
    private final AtomicLong minimizerSingletonNumber;
    private static final Logger LH = LoggerFactory.getLogger(GenomeIndexer.class);
    // 512 MiB
    private static final long SEG_LEN = (1 << 28);
    public final Path fnaPath;
    private final Map<String, Long> contigLenMap;
    private final Map<String, Long> rawIdxFileSizeMap;
    private final Map<String, Long> numMinimizersMap;

    public GenomeIndexer(Path fnaPath) {
        this.fnaPath = fnaPath;
        var refi = new FastaSequenceIndex(new File(fnaPath + ".fai"));
        contigLenMap = StreamSupport.stream(
                        IterUtils.iterable(refi.iterator()).spliterator(), false)
                .collect(Collectors.toMap(
                        FastaSequenceIndexEntry::getContig, FastaSequenceIndexEntry::getSize));
        rawIdxFileSizeMap = new HashMap<>();
        numMinimizersMap = new HashMap<>();
        numPositionsPerMinimizer = new DescriptiveStatistics();
        minimizerSingletonNumber = new AtomicLong(0);
        minimizerDistances = new DescriptiveStatistics();
        shannonEntropy = new DescriptiveStatistics();
    }

    public record GenomeIndexerConfig(
            int kmerSize, int numKmerPerMinimizer, double ntShannonEntropyCutoff) {
        @Contract(" -> new")
        @NotNull
        public static GenomeIndexerConfig minimap2() {
            return new GenomeIndexerConfig(14, 10, 0.7);
        }
    }

    public void generateHashes(
            byte @NotNull [] string,
            String contigName,
            long offsetOfFirst,
            int nthPart,
            @NotNull GenomeIndexerConfig config)
            throws IOException {
        LH.info(
                "PROCESS {} {} -> {} Started",
                contigName,
                offsetOfFirst,
                offsetOfFirst + string.length);

        var out = Path.of("%s.%d.%s.idx.bin".formatted(fnaPath.toString(), nthPart, contigName))
                .toFile();

        LH.info(
                "PROCESS {} {} -> {} Calculating entropy",
                contigName,
                offsetOfFirst,
                offsetOfFirst + string.length);
        var thisShannonEntropy = new DoubleArrayList();
        for (var se : IterUtils.iterable(new NtShannonEntropy(string, config.kmerSize(), 0))) {
            shannonEntropy.addValue(se);
            thisShannonEntropy.add(se.doubleValue());
        }
        var passingIdx = IterUtils.where(
                thisShannonEntropy.iterator(), (i) -> i > config.ntShannonEntropyCutoff());

        LH.info(
                "PROCESS {} {} -> {} Calculating ntHash",
                contigName,
                offsetOfFirst,
                offsetOfFirst + string.length);
        var hashes = NtHashBase.getAllBothHash(
                new PrecomputedBidirectionalNtHash(string, config.kmerSize(), 0), string.length);

        LH.info(
                "PROCESS {} {} -> {} Calculating minimizers",
                contigName,
                offsetOfFirst,
                offsetOfFirst + string.length);

        var fwdHashes = hashes._1();
        var revHashes = hashes._2();

        var passedNtHashes = new LongArrayList(passingIdx.size());
        var positions = new LongArrayList(passingIdx.size());

        for (var i : passingIdx) {
            long fwdHash = fwdHashes.get(i);
            long revHash = revHashes.get(i);
            if (fwdHash == revHash) {
                continue;
            }
            if (fwdHash + Long.MIN_VALUE < revHash + Long.MIN_VALUE) {
                passedNtHashes.add(fwdHash);
                positions.add(i + offsetOfFirst);
            } else {
                passedNtHashes.add(revHash);
                positions.add(-i - offsetOfFirst);
            }
        }

        var minHashPositions = MinimizerCalculator.getMinimizerPositions(
                passedNtHashes, config.numKmerPerMinimizer(), false);

        LH.info(
                "PROCESS {} {} -> {} Sorting minimizers",
                contigName,
                offsetOfFirst,
                offsetOfFirst + string.length);
        var bb = ByteBuffer.allocateDirect(Long.BYTES * 2);
        var hashOffsetDict = new HashMap<Long, List<Long>>();

        var lastPos = offsetOfFirst;
        for (var i : IterUtils.iterable(IterUtils.dedup(minHashPositions.iterator()))) {
            var realPos = Math.abs(positions.getLong(i));
            minimizerDistances.addValue(realPos - lastPos);
            lastPos = realPos;
            var hashValue = passedNtHashes.getLong(i);
            hashOffsetDict.computeIfAbsent(hashValue, k -> new LongArrayList());
            hashOffsetDict.get(hashValue).add(positions.getLong(i));
        }

        LH.info(
                "PROCESS {} {} -> {} Writing minimizers",
                contigName,
                offsetOfFirst,
                offsetOfFirst + string.length);
        var wlen = 0;

        try (var w = new FileOutputStream(out)) {
            var wChannel = w.getChannel();
            numMinimizersMap.computeIfPresent(contigName, (k, v) -> v + hashOffsetDict.size());
            for (var hashKey : hashOffsetDict.keySet()) {
                var hashValues = hashOffsetDict.get(hashKey);
                var numPositions = hashValues.size();
                numPositionsPerMinimizer.addValue(numPositions);
                if (numPositions == 1) {
                    minimizerSingletonNumber.getAndIncrement();
                }
                //                                    bb.clear();
                //                                    bb.putLong(hashKey);
                //                                    bb.putLong(hashValues.size());
                //                                    bb.rewind();
                //                                    wlen += wChannel.write(bb);
                //                                    var largeBB =
                // ByteBuffer.allocate(Long.BYTES *
                //                 hashValues.size());
                //                                    largeBB.clear();
                //                                    for (var l: hashValues){
                //                                        largeBB.putLong(l);
                //                                    }
                //                                    largeBB.rewind();
                //                                    wlen += wChannel.write(largeBB);
            }
        }
        var finalWlen = wlen;
        rawIdxFileSizeMap.computeIfPresent(contigName, (k, v) -> v + finalWlen);
        LH.info(
                "PROCESS {} {} -> {} Finished",
                contigName,
                offsetOfFirst,
                offsetOfFirst + string.length);
    }

    public void index(GenomeIndexerConfig config) {
        try (var ref = ReferenceSequenceFileFactory.getReferenceSequenceFile(fnaPath.toFile())) {
            for (var contigName : contigLenMap.keySet()) {
                rawIdxFileSizeMap.put(contigName, 0L);
                numMinimizersMap.put(contigName, 0L);
                var fromfwd = 1L;
                var tofwd = fromfwd + SEG_LEN + config.kmerSize();
                var nthPart = 0;
                do {
                    final long finalFromfwd = fromfwd - 1; // Due to 1-based indexing
                    final long finalTofwd = tofwd;
                    final int finalNthPart = nthPart;
                    final byte[] finalSeq = ref.getSubsequenceAt(
                                    contigName,
                                    finalFromfwd,
                                    Long.min(finalTofwd, contigLenMap.get(contigName)))
                            .getBases();
                    try {
                        this.generateHashes(
                                finalSeq, contigName, finalFromfwd, finalNthPart, config);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    fromfwd += SEG_LEN;
                    tofwd += SEG_LEN;
                    nthPart++;
                } while (tofwd <= contigLenMap.get(contigName));
            }
            for (var contigName : contigLenMap.keySet()) {
                LH.info(
                        "FINAL {} -- {} -> {} ({} minimizers)",
                        contigName,
                        LogUtils.lazy(() ->
                                FrontendUtils.toHumanReadable(contigLenMap.get(contigName), "bp")),
                        LogUtils.lazy(() ->
                                FrontendUtils.toHumanReadable(rawIdxFileSizeMap.get(contigName))),
                        LogUtils.lazy(() -> FrontendUtils.toHumanReadable(
                                numMinimizersMap.get(contigName), "")));
            }
            LH.info(
                    "FINAL NT entropy: {}",
                    LogUtils.lazy(LogUtils.summarizeDescriptiveStatistics(shannonEntropy, "%.4f")));
            LH.info(
                    "FINAL positions per minimizer: {}",
                    LogUtils.lazy(LogUtils.summarizeDescriptiveStatisticsWithFEU(
                            numPositionsPerMinimizer, "")));
            LH.info(
                    "FINAL minimizer distances: {}",
                    LogUtils.lazy(LogUtils.summarizeDescriptiveStatisticsWithFEU(
                            minimizerDistances, "bp")));
            var numDistinctMinimizers =
                    numMinimizersMap.values().stream().reduce(Long::sum).orElse(1L);
            LH.info(
                    "FINAL distinct minimizers: {}, singletons: {} ({})",
                    FrontendUtils.toHumanReadable(numDistinctMinimizers, ""),
                    FrontendUtils.toHumanReadable(minimizerSingletonNumber.get(), ""),
                    LogUtils.lazy(
                            LogUtils.calcPctLazy(minimizerSingletonNumber, numDistinctMinimizers)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        var basePath = "F:\\home\\Documents\\ystr\\test";
        var fnaPath = Path.of(basePath, "ce11.genomic.fna");
        var gi = new GenomeIndexer(fnaPath);
        gi.index(GenomeIndexerConfig.minimap2());
    }
}
