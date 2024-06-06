package com.github.yu_zhejian.ystr_demo;

import com.github.yu_zhejian.ystr.IterUtils;
import com.github.yu_zhejian.ystr.rolling.MinimizerCalculator;
import com.github.yu_zhejian.ystr.rolling.NtHashBase;
import com.github.yu_zhejian.ystr.rolling.PrecomputedNtHash;

import htsjdk.samtools.reference.FastaSequenceIndex;
import htsjdk.samtools.reference.FastaSequenceIndexEntry;
import htsjdk.samtools.reference.ReferenceSequenceFileFactory;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class GenomeIndexer {
    // 512 MiB
    private static final long SEG_LEN = (1 << 28);
    public final Path fnaPath;
    private final Map<String, Long> contigLen;
    private final Map<String, Long> rawIdxLen;

    public record GenomeIndexerConfig(int kmerSize, int numKmerPerMinimizer) {
        @Contract(" -> new")
        @NotNull
        public static GenomeIndexerConfig minimap2() {
            return new GenomeIndexerConfig(14, 10);
        }
    }

    public void generateHashes(
            byte[] string,
            String contigName,
            long offsetOfFirst,
            int nthPart,
            @NotNull GenomeIndexerConfig config)
            throws IOException {
        var posOut = Path.of("%s.%d.%s.%s.idx.bin"
                        .formatted(fnaPath.toString(), nthPart, contigName, "pos"))
                .toFile();
        var negOut = Path.of("%s.%d.%s.%s.idx.bin"
                        .formatted(fnaPath.toString(), nthPart, contigName, "neg"))
                .toFile();

        var hashes = NtHashBase.getAllBothHash(new PrecomputedNtHash(string, config.kmerSize(), 0));
        var posHash = hashes._1();
        var negHash = hashes._2();
        var minPos = MinimizerCalculator.getMinimizerPositions(
                posHash, config.numKmerPerMinimizer(), true);
        var minNeg = MinimizerCalculator.getMinimizerPositions(
                negHash, config.numKmerPerMinimizer(), true);

        var bb = ByteBuffer.allocateDirect(Long.BYTES * 2);
        long wlen = 0;

        try (var posW = new FileOutputStream(posOut);
                var negW = new FileOutputStream(negOut)) {
            var posWC = posW.getChannel();
            var negWC = negW.getChannel();
            for (var i : IterUtils.iterable(IterUtils.dedup(minPos.iterator()))) {
                bb.clear();
                bb.putLong(posHash.get(i));
                bb.putLong(i + offsetOfFirst);
                bb.rewind();
                wlen += posWC.write(bb);
            }
            for (var i : IterUtils.iterable(IterUtils.dedup(minNeg.iterator()))) {
                bb.clear();
                bb.putLong(negHash.get(i));
                bb.putLong(i + offsetOfFirst);
                bb.rewind();
                wlen += negWC.write(bb);
            }
        }

        var finalWlen = wlen;
        rawIdxLen.compute(contigName, (k, v) -> v + finalWlen);
    }

    public GenomeIndexer(Path fnaPath) {
        this.fnaPath = fnaPath;
        var refi = new FastaSequenceIndex(new File(fnaPath + ".fai"));
        contigLen = StreamSupport.stream(IterUtils.iterable(refi.iterator()).spliterator(), false)
                .collect(Collectors.toMap(
                        FastaSequenceIndexEntry::getContig, FastaSequenceIndexEntry::getSize));
        rawIdxLen = new HashMap<>();
    }

    public void index(GenomeIndexerConfig config) {
        try (var ref = ReferenceSequenceFileFactory.getReferenceSequenceFile(fnaPath.toFile())) {
            for (var contigName : contigLen.keySet()) {
                rawIdxLen.putIfAbsent(contigName, 0L);
                var fromPos = 1L;
                var toPos = fromPos + SEG_LEN + config.kmerSize();
                var nthPart = 0;
                do {
                    this.generateHashes(
                            ref.getSubsequenceAt(
                                            contigName,
                                            fromPos,
                                            Long.min(toPos, contigLen.get(contigName)))
                                    .getBases(),
                            contigName,
                            fromPos,
                            nthPart,
                            config);
                    fromPos += SEG_LEN;
                    toPos += SEG_LEN;
                    nthPart++;
                } while (toPos <= contigLen.get(contigName));
                System.out.printf(
                        "W: %s -- %s -> %s (%d)%n",
                        contigName,
                        FrontendUtils.toHumanReadable(contigLen.get(contigName), "bp"),
                        FrontendUtils.toHumanReadable(rawIdxLen.get(contigName)),
                        nthPart);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        var basePath = "D:\\Work\\ystr\\test";
        var fnaPath = Path.of(basePath, "e_coli.genomic.fna");
        var gi = new GenomeIndexer(fnaPath);
        gi.index(GenomeIndexerConfig.minimap2());
    }
}
