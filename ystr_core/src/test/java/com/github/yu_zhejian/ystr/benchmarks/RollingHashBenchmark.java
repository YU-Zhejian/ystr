package com.github.yu_zhejian.ystr.benchmarks;

import com.github.yu_zhejian.ystr.IterUtils;
import com.github.yu_zhejian.ystr.io.FastxIterator;
import com.github.yu_zhejian.ystr.rolling.NtHash;
import com.github.yu_zhejian.ystr.rolling.PolynomialRollingHash;
import com.github.yu_zhejian.ystr.rolling.PrecomputedNtHash;
import com.github.yu_zhejian.ystr.test_utils.GitUtils;

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
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
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
                "20" // Commonly used for assembly
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
                Path.of(GitUtils.getGitRoot(), "test", "sars_cov2.genomic.fna"))) {
            TEST_CHR = reader.next().seq();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    public void benchPrecomputedNtHash() {
        IterUtils.exhaust(new PrecomputedNtHash(TEST_CHR, k, 0));
    }

    @Benchmark
    public void benchNtHash() {
        IterUtils.exhaust(new NtHash(TEST_CHR, k, 0));
    }

    @Benchmark
    public void benchPolynomialRollingHash() {
        IterUtils.exhaust(new PolynomialRollingHash(TEST_CHR, k, 0));
    }
}
