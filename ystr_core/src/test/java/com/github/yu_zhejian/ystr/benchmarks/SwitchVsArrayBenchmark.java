package com.github.yu_zhejian.ystr.benchmarks;

import com.github.yu_zhejian.ystr.io.FastxIterator;
import com.github.yu_zhejian.ystr.test_utils.GitUtils;

import org.jetbrains.annotations.NotNull;
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
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

/**
 * Compare performance of switch-based methods and array-based methods in determining whether a base
 * may belong to Poly(A).
 */
@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
public class SwitchVsArrayBenchmark {
    private static final boolean[] PREDICATE = {
        false, false, false, false, false, false, false, false, // 0..7
        false, false, false, false, false, false, false, false, // 8..15
        false, false, false, false, false, false, false, false, // 16..23
        false, false, false, false, false, false, false, false, // 24..31
        false, false, false, false, false, false, false, false, // 32..39
        false, false, false, false, false, false, false, false, // 40..47
        false, false, false, false, false, false, false, false, // 48..55
        false, false, false, false, false, false, false, false, // 56..63
        false, true, false, false, false, false, false, true, // 64..71
        false, false, false, false, false, false, false, false, // 72..79
        false, false, false, false, true, true, false, false, // 80..87
        false, false, false, false, false, false, false, false, // 88..95
        false, true, false, false, false, false, false, false, // 96..103
        false, false, false, false, false, false, false, false, // 104..111
        false, false, false, false, true, true, false, false, // 112..119
        false, false, false, false, false, false, false, false // 120..127
    };
    byte[] TEST_CHR;

    public static void main(String[] args) throws RunnerException, FileNotFoundException {
        var className = SwitchVsArrayBenchmark.class.getSimpleName();
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
        try (var reader =
                FastxIterator.read(Path.of(GitUtils.getGitRoot(), "test", "e_coli.genomic.fna"))) {
            TEST_CHR = reader.next().seq();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    boolean switchMethod(byte i) {
        switch (i) {
            case 'A', 'T', 'U', 'a', 't', 'u' -> {
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    boolean arrayMethod(byte i) {
        return PREDICATE[i];
    }

    @Benchmark
    public void benchSwitchMethod(@NotNull Blackhole blackhole) {
        for (var i : TEST_CHR) {
            blackhole.consume(switchMethod(i));
        }
    }

    @Benchmark
    public void benchArrayMethod(@NotNull Blackhole blackhole) {
        for (var i : TEST_CHR) {
            blackhole.consume(arrayMethod(i));
        }
    }
}
