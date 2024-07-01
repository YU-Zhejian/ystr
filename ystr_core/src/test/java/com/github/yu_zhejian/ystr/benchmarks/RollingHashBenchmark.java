package com.github.yu_zhejian.ystr.benchmarks;

import com.github.yu_zhejian.ystr.hash.HashInterface;
import com.github.yu_zhejian.ystr.io.FastxIterator;
import com.github.yu_zhejian.ystr.rolling.NtHash;
import com.github.yu_zhejian.ystr.rolling.PolynomialRollingHash;
import com.github.yu_zhejian.ystr.rolling.PrecomputedNtHash;
import com.github.yu_zhejian.ystr.rolling.RollingHashAdaptor;
import com.github.yu_zhejian.ystr.rolling.RollingHashInterface;
import com.github.yu_zhejian.ystr.test_utils.GitUtils;

import org.jetbrains.annotations.NotNull;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
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
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
public class RollingHashBenchmark {

    byte[] TEST_CHR;

    @Param(
            value = {
                "6", // Commonly used for BLAST
                "12", // Commonly used for LR alignment
                "20", // Commonly used for assembly
                "256", // Some unrealistic value
                "1024" // Some unrealistic value
            })
    private int k;

    public static void main(String[] args) throws RunnerException, FileNotFoundException {
        var className = RollingHashBenchmark.class.getSimpleName();
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
        try (var reader = FastxIterator.read(
                Path.of(GitUtils.getGitRoot(), "test", "ref", "e_coli.genomic.fna"))) {
            TEST_CHR = reader.next().seq();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void bench(@NotNull Blackhole blackhole, @NotNull RollingHashInterface hasher) {
        hasher.attach(TEST_CHR, k);
        while (hasher.hasNext()) {
            blackhole.consume(hasher.nextLong());
        }
        hasher.detach();
    }

    @Benchmark
    public void benchPrecomputedNtHash(@NotNull Blackhole blackhole) {
        bench(blackhole, new PrecomputedNtHash());
    }

    @Benchmark
    public void benchNtHash(@NotNull Blackhole blackhole) {
        bench(blackhole, new NtHash());
    }

    @Benchmark
    public void benchPolynomialRollingHash(@NotNull Blackhole blackhole) {
        bench(blackhole, new PolynomialRollingHash());
    }

    @Benchmark
    public void benchRollingHashAdaptor(@NotNull Blackhole blackhole) {
        bench(blackhole, new RollingHashAdaptor(HashInterface.JUL_CRC32_CHECKSUM));
    }
}
