package com.github.yu_zhejian.ystr.benchmarks;

import com.github.yu_zhejian.ystr.checksum.CRC32;
import com.github.yu_zhejian.ystr.checksum.ChecksumInterface;
import com.github.yu_zhejian.ystr.hash.BitwiseFNV1a32;
import com.github.yu_zhejian.ystr.hash.BitwiseFNV1a64;
import com.github.yu_zhejian.ystr.hash.HashInterface;
import com.github.yu_zhejian.ystr.hash.MultiplyFNV1a32;
import com.github.yu_zhejian.ystr.hash.MultiplyFNV1a64;
import com.github.yu_zhejian.ystr.test_utils.GitUtils;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 10, time = 1)
@Fork(1)
public class StringHashBenchmark {

    ObjectArrayList<byte[]> TEST_VOCABULARY;

    public static void main(String[] args) throws RunnerException, FileNotFoundException {
        var className = StringHashBenchmark.class.getSimpleName();
        var options = new OptionsBuilder()
                .include("%s.*".formatted(className))
                .result(Path.of(GitUtils.getGitRoot(), "benchmark_out", className + ".json")
                        .toString())
                .resultFormat(ResultFormatType.JSON)
                .build();
        new Runner(options).run();
    }

    @Setup
    public void setup() {
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
    }

    @Benchmark
    public void benchBitwiseFNV1a64(Blackhole blackhole) {
        for (var word : TEST_VOCABULARY) {
            blackhole.consume(HashInterface.fastHash(BitwiseFNV1a64::new, word));
        }
    }

    @Benchmark
    public void benchMultiplyFNV1a64(Blackhole blackhole) {
        for (var word : TEST_VOCABULARY) {
            blackhole.consume(HashInterface.fastHash(MultiplyFNV1a64::new, word));
        }
    }

    @Benchmark
    public void benchMultiplyFNV1a32(Blackhole blackhole) {
        for (var word : TEST_VOCABULARY) {
            blackhole.consume(HashInterface.fastHash(MultiplyFNV1a32::new, word));
        }
    }

    @Benchmark
    public void benchBitwiseFNV1a32(Blackhole blackhole) {
        for (var word : TEST_VOCABULARY) {
            blackhole.consume(HashInterface.fastHash(BitwiseFNV1a32::new, word));
        }
    }

    @Benchmark
    public void benchCRC32(Blackhole blackhole) {
        for (var word : TEST_VOCABULARY) {
            blackhole.consume(ChecksumInterface.fastChecksum(CRC32::new, word));
        }
    }

    @Benchmark
    public void benchJULZipCRC32(Blackhole blackhole) {
        for (var word : TEST_VOCABULARY) {
            blackhole.consume(ChecksumInterface.fastJULZipChecksum(java.util.zip.CRC32::new, word));
        }
    }

    @Benchmark
    public void benchJULZipCRC32C(Blackhole blackhole) {
        for (var word : TEST_VOCABULARY) {
            blackhole.consume(
                    ChecksumInterface.fastJULZipChecksum(java.util.zip.CRC32C::new, word));
        }
    }

    @Benchmark
    public void benchJULZipAdler32(Blackhole blackhole) {
        for (var word : TEST_VOCABULARY) {
            blackhole.consume(
                    ChecksumInterface.fastJULZipChecksum(java.util.zip.Adler32::new, word));
        }
    }
}
