package com.github.yu_zhejian.ystr.profilers;

import com.github.yu_zhejian.ystr.StrHash;
import com.github.yu_zhejian.ystr.checksum.CRC32;
import com.github.yu_zhejian.ystr.checksum.ChecksumInterface;
import com.github.yu_zhejian.ystr.hash.BitwiseFNV1a32;
import com.github.yu_zhejian.ystr.hash.BitwiseFNV1a64;
import com.github.yu_zhejian.ystr.hash.HashInterface;
import com.github.yu_zhejian.ystr.hash.MultiplyFNV1a32;
import com.github.yu_zhejian.ystr.hash.MultiplyFNV1a64;
import com.github.yu_zhejian.ystr.io.FastxIterator;
import com.github.yu_zhejian.ystr.test_utils.CSVUtils;
import com.github.yu_zhejian.ystr.test_utils.GitUtils;
import com.github.yu_zhejian.ystr.test_utils.LogUtils;
import com.github.yu_zhejian.ystr.utils.KmerGenerator;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

public final class StringHashBenchmark {

    private static final Logger LH = LoggerFactory.getLogger(StringHashBenchmark.class);
    private static final ObjectArrayList<byte[]> TEST_VOCABULARY;
    private static final ObjectArrayList<byte[]> TEST_12MER;
    private static final ObjectArrayList<byte[]> TEST_ESTS;

    private static final Map<String, ObjectArrayList<byte[]>> TEST_CASES;

    private static final BitwiseFNV1a64 bitwiseFNV1a64 = new BitwiseFNV1a64();
    private static final CRC32 crc32 = new CRC32();
    private static final BitwiseFNV1a32 bitwiseFNV1a32 = new BitwiseFNV1a32();
    private static final MultiplyFNV1a32 multiplyFNV1a32 = new MultiplyFNV1a32();
    private static final MultiplyFNV1a64 multiplyFNV1a64 = new MultiplyFNV1a64();
    private static final java.util.zip.CRC32 julCrc32 = new java.util.zip.CRC32();
    private static final java.util.zip.CRC32C julCrc32C = new java.util.zip.CRC32C();
    private static final java.util.zip.Adler32 julAlder32 = new java.util.zip.Adler32();
    private static final CSVPrinter SPEED_CSVP;

    static {
        var className = StringHashBenchmark.class.getSimpleName();
        try {
            SPEED_CSVP = CSVUtils.createCSVPrinter(new File(
                    Path.of(GitUtils.getGitRoot(), "benchmark_out", className + ".speed.tsv")
                            .toString()));
            SPEED_CSVP.printRecord("DATA", "ALGO", "TIME", "LEN");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        TEST_VOCABULARY = new ObjectArrayList<>();
        try (var reader = Files.newBufferedReader(
                Path.of(GitUtils.getGitRoot(), "test", "literature", "wordlist.txt"))) {
            String l;
            while ((l = reader.readLine()) != null) {
                TEST_VOCABULARY.add(l.getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        LH.info("Test data WORD read with {} strings", LogUtils.lazy(TEST_VOCABULARY::size));

        TEST_12MER = new ObjectArrayList<>();
        var kmerGenerator = new KmerGenerator(KmerGenerator.DNA_ALPHABET, 12);
        while (kmerGenerator.hasNext()) {
            TEST_12MER.add(kmerGenerator.next());
        }
        LH.info("Test data 12MER read with {} strings", LogUtils.lazy(TEST_12MER::size));

        TEST_ESTS = new ObjectArrayList<>();
        try (var fxp = FastxIterator.read(
                Path.of(GitUtils.getGitRoot(), "test", "ref", "c_elegans_ests.fa")
                        .toFile())) {
            while (fxp.hasNext()) {
                TEST_ESTS.add(fxp.next().seq());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        LH.info("Test data ESTS read with {} strings", LogUtils.lazy(TEST_ESTS::size));

        TEST_CASES = Map.of(
                "12MER", TEST_12MER,
                "WORD", TEST_VOCABULARY);
    }

    private static void testSpeed(Function<byte[], Long> checksumFunc, String name)
            throws IOException {
        long hash;
        long maxHash = 0;
        long minHash = Long.MAX_VALUE;
        long startNS;
        long endNS;
        LH.info("Benchmark {} on {}", name, "ESTS");
        for (var word : TEST_ESTS) {
            startNS = System.nanoTime();
            hash = checksumFunc.apply(word);
            endNS = System.nanoTime();
            SPEED_CSVP.printRecord("ESTS", name, endNS - startNS, word.length);
            maxHash = Math.max(maxHash + Long.MIN_VALUE, hash + Long.MIN_VALUE) - Long.MIN_VALUE;
            minHash = Math.min(minHash + Long.MIN_VALUE, hash + Long.MIN_VALUE) - Long.MIN_VALUE;
        }
        long finalMinHash = minHash;
        long finalMaxHash = maxHash;
        LH.info(
                "Benchmark {} on ESTS: 0x{} -> 0x{}",
                name,
                LogUtils.lazy(() -> Long.toHexString(finalMinHash)),
                LogUtils.lazy(() -> Long.toHexString(finalMaxHash)));
    }

    public static void main(String[] args) throws IOException {
        testSpeed((word) -> ChecksumInterface.fastChecksum(crc32, word), "crc32");
        testSpeed((word) -> ChecksumInterface.fastChecksum(julCrc32C, word), "julCrc32C");
        testSpeed((word) -> ChecksumInterface.fastChecksum(julCrc32, word), "julCrc32");
        testSpeed((word) -> ChecksumInterface.fastChecksum(julAlder32, word), "julAlder32");
        testSpeed((word) -> HashInterface.fastHash(bitwiseFNV1a32, word), "bitwiseFNV1a32");
        testSpeed((word) -> HashInterface.fastHash(bitwiseFNV1a64, word), "bitwiseFNV1a64");
        testSpeed((word) -> HashInterface.fastHash(multiplyFNV1a32, word), "multiplyFNV1a32");
        testSpeed((word) -> HashInterface.fastHash(multiplyFNV1a64, word), "multiplyFNV1a64");
        testSpeed(StrHash::ntHash, "ntHash");
        testSpeed((word) -> (long) Arrays.hashCode(word), "javaHash");
    }
}
