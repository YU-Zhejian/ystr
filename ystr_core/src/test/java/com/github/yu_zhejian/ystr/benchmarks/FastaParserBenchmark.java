package com.github.yu_zhejian.ystr.benchmarks;

import com.github.yu_zhejian.ystr.io.TwoBitParser;
import com.github.yu_zhejian.ystr.test_utils.GitUtils;

import htsjdk.samtools.reference.ReferenceSequenceFile;
import htsjdk.samtools.reference.ReferenceSequenceFileFactory;

import io.vavr.Tuple;
import io.vavr.Tuple3;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import org.jetbrains.annotations.NotNull;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 3, time = 5)
@Measurement(iterations = 5, time = 5)
@Fork(1)
public class FastaParserBenchmark {
    private static final Logger LH = LoggerFactory.getLogger(FastaParserBenchmark.class);
    private ReferenceSequenceFile htsJdkRef;
    private ReferenceSequenceFile compressedHtsJdkRef;
    private TwoBitParser twoBitParser;
    private List<Tuple3<String, Integer, Integer>> coordinates;
    private Map<String, Integer> seqNameIDMap;
    private static final int NUM_RDN_INTERVALS = 1;

    public static void main(String[] args) throws RunnerException, FileNotFoundException {
        var className = FastaParserBenchmark.class.getSimpleName();
        var options = new OptionsBuilder()
                .include("%s.*".formatted(className))
                .result(Path.of(GitUtils.getGitRoot(), "benchmark_out", className + ".json")
                        .toString())
                .resultFormat(ResultFormatType.JSON)
                .build();
        new Runner(options).run();
    }

    @Setup
    public void setup() throws IOException {
        htsJdkRef = ReferenceSequenceFileFactory.getReferenceSequenceFile(
                Path.of(GitUtils.getGitRoot(), "test", "ce11.genomic.fna").toFile());
        compressedHtsJdkRef = ReferenceSequenceFileFactory.getReferenceSequenceFile(
                Path.of(GitUtils.getGitRoot(), "test", "ce11.genomic.fna.gz").toFile());
        twoBitParser = new TwoBitParser(
                Path.of(GitUtils.getGitRoot(), "test", "ce11.2bit").toFile());
        coordinates = new ObjectArrayList<>();
        var seqNames = twoBitParser.getSeqNames();
        var seqLengths = twoBitParser.getSeqLengths();

        seqNameIDMap = new HashMap<>();
        for (var i = 0; i < seqNames.size(); i++) {
            seqNameIDMap.put(seqNames.get(i), i);
        }
        var rng = new Random();
        for (var i = 0; i < NUM_RDN_INTERVALS; i++) {
            var seqID = rng.nextInt(seqNames.size());
            var selectedChrom = seqNames.get(seqID);
            var selectedTerm1 = rng.nextInt(0, seqLengths.getInt(seqID));
            var selectedTerm2 = rng.nextInt(0, seqLengths.getInt(seqID));
            coordinates.add(Tuple.of(
                    selectedChrom,
                    Math.min(selectedTerm1, selectedTerm2),
                    Math.max(selectedTerm1, selectedTerm2)));
        }
    }

    @Benchmark
    public void benchTwoBit(@NotNull Blackhole blackhole) throws IOException {
        for (var coordinate : coordinates) {
            blackhole.consume(twoBitParser.getSequence(
                    seqNameIDMap.get(coordinate._1()), coordinate._2(), coordinate._3(), true));
        }
    }

    @Benchmark
    public void benchTwoBitUnmasked(@NotNull Blackhole blackhole) throws IOException {
        for (var coordinate : coordinates) {
            blackhole.consume(twoBitParser.getSequence(
                    seqNameIDMap.get(coordinate._1()), coordinate._2(), coordinate._3(), true));
        }
    }

    @Benchmark
    public void benchHtsJdk(@NotNull Blackhole blackhole) {
        for (var coordinate : coordinates) {
            blackhole.consume(htsJdkRef.getSubsequenceAt(
                    coordinate._1(), 1 + coordinate._2(), coordinate._3()));
        }
    }

    @Benchmark
    public void benchCompressedHtsJdk(@NotNull Blackhole blackhole) {
        for (var coordinate : coordinates) {
            blackhole.consume(compressedHtsJdkRef.getSubsequenceAt(
                    coordinate._1(), 1 + coordinate._2(), coordinate._3()));
        }
    }
}
