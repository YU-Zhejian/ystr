package com.github.yu_zhejian.ystr.profilers;

import com.github.yu_zhejian.ystr.StrHash;
import com.github.yu_zhejian.ystr.checksum.CRC32;
import com.github.yu_zhejian.ystr.checksum.ChecksumInterface;
import com.github.yu_zhejian.ystr.hash.APHash;
import com.github.yu_zhejian.ystr.hash.BKDRHash;
import com.github.yu_zhejian.ystr.hash.BPHash;
import com.github.yu_zhejian.ystr.hash.BitwiseFNV1a32;
import com.github.yu_zhejian.ystr.hash.BitwiseFNV1a64;
import com.github.yu_zhejian.ystr.hash.DJBHash;
import com.github.yu_zhejian.ystr.hash.ELFHash;
import com.github.yu_zhejian.ystr.hash.HashInterface;
import com.github.yu_zhejian.ystr.hash.JSHash;
import com.github.yu_zhejian.ystr.hash.MultiplyFNV1a32;
import com.github.yu_zhejian.ystr.hash.MultiplyFNV1a64;
import com.github.yu_zhejian.ystr.hash.PJWHash;
import com.github.yu_zhejian.ystr.hash.RSHash;
import com.github.yu_zhejian.ystr.hash.SDBMHash;
import com.github.yu_zhejian.ystr.io.FastxIterator;
import com.github.yu_zhejian.ystr.test_utils.CSVUtils;
import com.github.yu_zhejian.ystr.test_utils.GitUtils;
import com.github.yu_zhejian.ystr.utils.Alphabets;
import com.github.yu_zhejian.ystr.utils.KmerGenerator;
import com.github.yu_zhejian.ystr.utils.LogUtils;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import net.openhft.hashing.LongHashFunction;

import org.apache.commons.csv.CSVPrinter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.ToLongFunction;
import java.util.zip.Adler32;
import java.util.zip.CRC32C;

public final class StringHashBenchmark {
    private static final Logger LH = LoggerFactory.getLogger(StringHashBenchmark.class);
    private static final ObjectArrayList<byte[]> TEST_VOCABULARY;
    private static final ObjectArrayList<byte[]> TEST_ESTS;
    private static final ObjectArrayList<byte[]> TEST_SWISSPROT;
    private static final Object2ObjectArrayMap<String, Iterable<byte[]>> COLLISION_TEST_CASES;
    private static final Object2ObjectArrayMap<String, Iterable<byte[]>> SPEED_TEST_CASES;
    private static final BitwiseFNV1a64 bitwiseFNV1a64 = new BitwiseFNV1a64();
    private static final CRC32 crc32 = new CRC32();
    private static final BitwiseFNV1a32 bitwiseFNV1a32 = new BitwiseFNV1a32();
    private static final MultiplyFNV1a32 multiplyFNV1a32 = new MultiplyFNV1a32();
    private static final MultiplyFNV1a64 multiplyFNV1a64 = new MultiplyFNV1a64();
    private static final APHash apHash = new APHash();
    private static final BKDRHash bkdrHash = new BKDRHash();
    private static final BPHash bpHash = new BPHash();
    private static final DJBHash djbHash = new DJBHash();
    private static final ELFHash elfHash = new ELFHash();
    private static final JSHash jsHash = new JSHash();
    private static final PJWHash pjwHash = new PJWHash();
    private static final RSHash rsHash = new RSHash();
    private static final SDBMHash sdbmHash = new SDBMHash();
    private static final java.util.zip.CRC32 julCrc32 = new java.util.zip.CRC32();
    private static final CRC32C julCrc32C = new CRC32C();
    private static final Adler32 julAlder32 = new Adler32();

    private static final CSVPrinter SPEED_CSVP;
    private static final CSVPrinter COLLISIONS_CSVP;

    static {
        var className = StringHashBenchmark.class.getSimpleName();
        try {
            SPEED_CSVP = CSVUtils.createCSVPrinter(new File(
                    Path.of(GitUtils.getGitRoot(), "benchmark_out", className + ".speed.tsv")
                            .toString()));
            SPEED_CSVP.printRecord("DATA", "ALGO", "TIME", "LEN");
            COLLISIONS_CSVP = CSVUtils.createCSVPrinter(new File(
                    Path.of(GitUtils.getGitRoot(), "benchmark_out", className + ".collisions.tsv")
                            .toString()));
            COLLISIONS_CSVP.printRecord("DATA", "ALGO", "COLLISIONS", "TOTAL");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        TEST_VOCABULARY = new ObjectArrayList<>();
        var tmpVocabulary = new ObjectOpenHashSet<byte[]>();
        try (var reader = Files.newBufferedReader(
                Path.of(GitUtils.getGitRoot(), "test", "literature", "wordlist.txt"))) {
            String l;
            while ((l = reader.readLine()) != null) {
                l = l.trim();
                if (l.isEmpty()) {
                    continue;
                }
                tmpVocabulary.add(l.getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        TEST_VOCABULARY.addAll(tmpVocabulary);
        LH.info("Test data WORD read with {} strings", LogUtils.lazy(TEST_VOCABULARY::size));

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

        TEST_SWISSPROT = new ObjectArrayList<>();
        try (var fxp =
                FastxIterator.read(Path.of(GitUtils.getGitRoot(), "test", "ref", "uniprot_sprot.fa")
                        .toFile())) {
            while (fxp.hasNext()) {
                TEST_SWISSPROT.add(fxp.next().seq());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        LH.info("Test data SWISSPROT read with {} strings", LogUtils.lazy(TEST_SWISSPROT::size));

        COLLISION_TEST_CASES = new Object2ObjectArrayMap<>();
        COLLISION_TEST_CASES.put("WORD", TEST_VOCABULARY);
        for (var i = 4; i <= 12; i++) {
            int finalI = i;
            COLLISION_TEST_CASES.put("DNA%dMER".formatted(i), new Iterable<>() {
                @NotNull
                @Override
                public Iterator<byte[]> iterator() {
                    return new KmerGenerator(Alphabets.DNA_ALPHABET, finalI);
                }
            });
        }
        for (var i = 2; i <= 6; i++) {
            int finalI = i;
            COLLISION_TEST_CASES.put("AA%dMER".formatted(i), new Iterable<>() {
                @NotNull
                @Override
                public Iterator<byte[]> iterator() {
                    return new KmerGenerator(Alphabets.AA_ALPHABET, finalI);
                }
            });
        }
        SPEED_TEST_CASES = new Object2ObjectArrayMap<>();
        SPEED_TEST_CASES.put("ESTS", TEST_ESTS);
        SPEED_TEST_CASES.put("SWISSPROT", TEST_SWISSPROT);
    }

    private static int getNumCollisions(@NotNull LongArrayList hashes) {
        hashes.sort(null);
        var numCollisions = 0;
        var prev = hashes.getLong(0);
        for (int i = 1; i < hashes.size(); i++) {
            var current = hashes.getLong(i);
            if (prev == current) {
                numCollisions += 1;
            }
            prev = current;
        }
        return numCollisions;
    }

    private static void testCollisions(ToLongFunction<byte[]> checksumFunc, String name)
            throws IOException {
        for (var entry : COLLISION_TEST_CASES.entrySet()) {
            LH.info("Benchmark collisions: {} on {}", name, entry.getKey());
            var hashes = new LongArrayList();
            for (var word : entry.getValue()) {
                hashes.add(checksumFunc.applyAsLong(word));
            }
            var collisions = getNumCollisions(hashes);
            COLLISIONS_CSVP.printRecord(entry.getKey(), name, collisions, hashes.size());
        }
    }

    private static void testSpeed(ToLongFunction<byte[]> checksumFunc, String name)
            throws IOException {
        long hash;
        long maxHash = 0;
        long minHash = Long.MAX_VALUE;
        long startNS;
        long endNS;
        for (var entry : SPEED_TEST_CASES.entrySet()) {
            LH.info("Benchmark speed: {} on {}", name, entry.getKey());
            for (var word : entry.getValue()) {
                startNS = System.nanoTime();
                hash = checksumFunc.applyAsLong(word);
                endNS = System.nanoTime();
                SPEED_CSVP.printRecord(entry.getKey(), name, endNS - startNS, word.length);
                maxHash =
                        Math.max(maxHash + Long.MIN_VALUE, hash + Long.MIN_VALUE) - Long.MIN_VALUE;
                minHash =
                        Math.min(minHash + Long.MIN_VALUE, hash + Long.MIN_VALUE) - Long.MIN_VALUE;
            }
            long finalMinHash = minHash;
            long finalMaxHash = maxHash;
            LH.info(
                    "Benchmark speed: {} on {}: 0x{} -> 0x{}",
                    name,
                    entry.getKey(),
                    LogUtils.lazy(() -> Long.toHexString(finalMinHash)),
                    LogUtils.lazy(() -> Long.toHexString(finalMaxHash)));
        }
    }

    public static void main(String[] args) throws IOException {
        testSpeed((word) -> ChecksumInterface.convenientChecksum(crc32, word), "crc32");
        testSpeed((word) -> ChecksumInterface.convenientChecksum(julCrc32C, word), "julCrc32C");
        testSpeed((word) -> ChecksumInterface.convenientChecksum(julCrc32, word), "julCrc32");
        testSpeed((word) -> ChecksumInterface.convenientChecksum(julAlder32, word), "julAlder32");
        testSpeed((word) -> HashInterface.fastHash(bitwiseFNV1a32, word), "bitwiseFNV1a32");
        testSpeed((word) -> HashInterface.fastHash(bitwiseFNV1a64, word), "bitwiseFNV1a64");
        testSpeed((word) -> HashInterface.fastHash(multiplyFNV1a32, word), "multiplyFNV1a32");
        testSpeed((word) -> HashInterface.fastHash(multiplyFNV1a64, word), "multiplyFNV1a64");
        testSpeed(StrHash::ntHash, "ntHash");
        testSpeed((word) -> (long) Arrays.hashCode(word), "javaHash");

        testSpeed((word) -> HashInterface.fastHash(apHash, word), "apHash");
        testSpeed((word) -> HashInterface.fastHash(bkdrHash, word), "bkdrHash");
        testSpeed((word) -> HashInterface.fastHash(bpHash, word), "bpHash");
        testSpeed((word) -> HashInterface.fastHash(djbHash, word), "djbHash");
        testSpeed((word) -> HashInterface.fastHash(elfHash, word), "elfHash");
        testSpeed((word) -> HashInterface.fastHash(jsHash, word), "jsHash");
        testSpeed((word) -> HashInterface.fastHash(pjwHash, word), "pjwHash");
        testSpeed((word) -> HashInterface.fastHash(rsHash, word), "rsHash");
        testSpeed((word) -> HashInterface.fastHash(sdbmHash, word), "sdbmHash");

        testSpeed((word) -> LongHashFunction.xx3().hashBytes(word), "xx3");
        testSpeed((word) -> LongHashFunction.xx().hashBytes(word), "xx");
        testSpeed((word) -> LongHashFunction.murmur_3().hashBytes(word), "murmur_3");
        testSpeed((word) -> LongHashFunction.city_1_1().hashBytes(word), "city_1_1");
        testSpeed((word) -> LongHashFunction.metro().hashBytes(word), "metro");
        testSpeed((word) -> LongHashFunction.wy_3().hashBytes(word), "wy_3");
        testSpeed((word) -> LongHashFunction.farmNa().hashBytes(word), "farmNa");
        testSpeed((word) -> LongHashFunction.farmUo().hashBytes(word), "farmUo");

        testCollisions((word) -> ChecksumInterface.convenientChecksum(crc32, word), "crc32");
        testCollisions((word) -> ChecksumInterface.convenientChecksum(julCrc32C, word), "julCrc32C");
        testCollisions((word) -> ChecksumInterface.convenientChecksum(julCrc32, word), "julCrc32");
        testCollisions((word) -> ChecksumInterface.convenientChecksum(julAlder32, word), "julAlder32");
        testCollisions((word) -> HashInterface.fastHash(bitwiseFNV1a32, word), "bitwiseFNV1a32");
        testCollisions((word) -> HashInterface.fastHash(bitwiseFNV1a64, word), "bitwiseFNV1a64");
        testCollisions((word) -> HashInterface.fastHash(multiplyFNV1a32, word), "multiplyFNV1a32");
        testCollisions((word) -> HashInterface.fastHash(multiplyFNV1a64, word), "multiplyFNV1a64");
        testCollisions(StrHash::ntHash, "ntHash");
        testCollisions((word) -> (long) Arrays.hashCode(word), "javaHash");

        testCollisions((word) -> HashInterface.fastHash(apHash, word), "apHash");
        testCollisions((word) -> HashInterface.fastHash(bkdrHash, word), "bkdrHash");
        testCollisions((word) -> HashInterface.fastHash(bpHash, word), "bpHash");
        testCollisions((word) -> HashInterface.fastHash(djbHash, word), "djbHash");
        testCollisions((word) -> HashInterface.fastHash(elfHash, word), "elfHash");
        testCollisions((word) -> HashInterface.fastHash(jsHash, word), "jsHash");
        testCollisions((word) -> HashInterface.fastHash(pjwHash, word), "pjwHash");
        testCollisions((word) -> HashInterface.fastHash(rsHash, word), "rsHash");
        testCollisions((word) -> HashInterface.fastHash(sdbmHash, word), "sdbmHash");

        testCollisions((word) -> LongHashFunction.xx3().hashBytes(word), "xx3");
        testCollisions((word) -> LongHashFunction.xx().hashBytes(word), "xx");
        testCollisions((word) -> LongHashFunction.murmur_3().hashBytes(word), "murmur_3");
        testCollisions((word) -> LongHashFunction.city_1_1().hashBytes(word), "city_1_1");
        testCollisions((word) -> LongHashFunction.metro().hashBytes(word), "metro");
        testCollisions((word) -> LongHashFunction.wy_3().hashBytes(word), "wy_3");
        testCollisions((word) -> LongHashFunction.farmNa().hashBytes(word), "farmNa");
        testCollisions((word) -> LongHashFunction.farmUo().hashBytes(word), "farmUo");

        SPEED_CSVP.flush();
        SPEED_CSVP.close();

        COLLISIONS_CSVP.flush();
        COLLISIONS_CSVP.close();
    }
}
