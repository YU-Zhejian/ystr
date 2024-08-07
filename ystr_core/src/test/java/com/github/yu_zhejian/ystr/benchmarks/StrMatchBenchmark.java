package com.github.yu_zhejian.ystr.benchmarks;

import com.github.yu_zhejian.ystr.io.FastxIterator;
import com.github.yu_zhejian.ystr.match.BoyerMooreBCMatch;
import com.github.yu_zhejian.ystr.match.BruteForceMatch;
import com.github.yu_zhejian.ystr.match.KnuthMorrisPrattMatch;
import com.github.yu_zhejian.ystr.match.NaiveMatch;
import com.github.yu_zhejian.ystr.match.RabinKarpMatch;
import com.github.yu_zhejian.ystr.match.ShiftOrMatch;
import com.github.yu_zhejian.ystr.match.StrMatchInterface;
import com.github.yu_zhejian.ystr.rolling.PrecomputedNtHash;
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
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
public class StrMatchBenchmark {

    byte[] TEST_CHR;

    @Param({"TATA", "GGAGG", "GATAAGGCGT", "CGCCGCATCCGGCA"})
    String needle;

    public static void main(String[] args) throws RunnerException, FileNotFoundException {
        var className = StrMatchBenchmark.class.getSimpleName();
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

    @Benchmark
    public void benchBruteForce(@NotNull Blackhole blackhole) {
        blackhole.consume(StrMatchInterface.convenientApply(
                BruteForceMatch::new,
                TEST_CHR,
                needle.getBytes(StandardCharsets.UTF_8),
                0,
                TEST_CHR.length));
    }

    @Benchmark
    public void benchNaive(@NotNull Blackhole blackhole) {
        blackhole.consume(StrMatchInterface.convenientApply(
                NaiveMatch::new,
                TEST_CHR,
                needle.getBytes(StandardCharsets.UTF_8),
                0,
                TEST_CHR.length));
    }

    @Benchmark
    public void benchRabinKarp(@NotNull Blackhole blackhole) {
        blackhole.consume(StrMatchInterface.convenientApply(
                RabinKarpMatch::new,
                TEST_CHR,
                needle.getBytes(StandardCharsets.UTF_8),
                0,
                TEST_CHR.length));
    }

    @Benchmark
    public void benchRabinKarpNtHash(@NotNull Blackhole blackhole) {
        blackhole.consume(StrMatchInterface.convenientApply(
                () -> new RabinKarpMatch(new PrecomputedNtHash()),
                TEST_CHR,
                needle.getBytes(StandardCharsets.UTF_8),
                0,
                TEST_CHR.length));
    }

    @Benchmark
    public void benchKnuthMorrisPratt(@NotNull Blackhole blackhole) {
        blackhole.consume(StrMatchInterface.convenientApply(
                KnuthMorrisPrattMatch::new,
                TEST_CHR,
                needle.getBytes(StandardCharsets.UTF_8),
                0,
                TEST_CHR.length));
    }

    @Benchmark
    public void benchShiftOr(@NotNull Blackhole blackhole) {
        blackhole.consume(StrMatchInterface.convenientApply(
                ShiftOrMatch::new,
                TEST_CHR,
                needle.getBytes(StandardCharsets.UTF_8),
                0,
                TEST_CHR.length));
    }

    @Benchmark
    public void benchBoyerMooreBadCharactersOnly(@NotNull Blackhole blackhole) {
        blackhole.consume(StrMatchInterface.convenientApply(
                BoyerMooreBCMatch::new,
                TEST_CHR,
                needle.getBytes(StandardCharsets.UTF_8),
                0,
                TEST_CHR.length));
    }
}
