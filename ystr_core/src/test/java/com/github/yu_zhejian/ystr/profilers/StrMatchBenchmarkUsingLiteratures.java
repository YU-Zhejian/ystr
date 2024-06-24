package com.github.yu_zhejian.ystr.profilers;

import com.github.yu_zhejian.ystr.match.BoyerMooreBadCharactersOnlyMatch;
import com.github.yu_zhejian.ystr.match.BruteForceMatch;
import com.github.yu_zhejian.ystr.match.KnuthMorrisPrattMatch;
import com.github.yu_zhejian.ystr.match.NaiveMatch;
import com.github.yu_zhejian.ystr.match.RabinKarpMatch;
import com.github.yu_zhejian.ystr.match.ShiftOrMatch;
import com.github.yu_zhejian.ystr.match.StrMatchInterface;
import com.github.yu_zhejian.ystr.match.StrMatchUtils;
import com.github.yu_zhejian.ystr.test_utils.CSVUtils;
import com.github.yu_zhejian.ystr.test_utils.GitUtils;

import org.apache.commons.csv.CSVPrinter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public final class StrMatchBenchmarkUsingLiteratures {
    private static final int WARMUP_ITERATIONS = 20;
    private static final byte[] KJV = readLC("kjv_gutenberg.txt");
    private static final byte[] SHAKESPEARE = readLC("shakespeare_gutenberg.txt");
    private static final List<byte[]> OXFORD_3K;
    private final CSVPrinter csvp;
    private final Logger LH = LoggerFactory.getLogger(StrMatchBenchmarkUsingLiteratures.class);

    static {
        try (var reader = new BufferedReader(new BufferedReader(
                new FileReader(Path.of(GitUtils.getGitRoot(), "test", "literature", "oxford_3k.txt")
                        .toFile())))) {
            OXFORD_3K = reader.lines()
                    .map(s -> s.trim().getBytes(StandardCharsets.UTF_8))
                    .filter(s -> s.length != 0)
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public StrMatchBenchmarkUsingLiteratures() throws IOException {
        csvp = CSVUtils.createCSVPrinter(Path.of(
                        GitUtils.getGitRoot(),
                        "benchmark_out",
                        StrMatchBenchmarkUsingLiteratures.class.getSimpleName() + ".tsv")
                .toFile());
        csvp.printRecord("Text", "Algorithm", "NeedleLen", "Occurrence", "TimeNS");
    }

    void testSS(@NotNull StrMatchInterface function, int strLenLimit) throws IOException {
        var name = function.getClass().getSimpleName();
        LH.info("Testing {}... Warming up JVM", name);
        var rng = new Random();
        int occurrence = 0;
        for (int j = 0; j < WARMUP_ITERATIONS; j++) {
            var thisOccurrence = 0;
            var word = OXFORD_3K.get(rng.nextInt(OXFORD_3K.size()) - 1);
            thisOccurrence +=
                    function.apply(SHAKESPEARE, word, 0, SHAKESPEARE.length).size();
            thisOccurrence += function.apply(KJV, word, 0, KJV.length).size();
            if (occurrence < thisOccurrence) {
                occurrence = thisOccurrence;
            }
        }
        LH.info("Testing {}... Detected max {} occurrences in warming up", name, occurrence);

        LH.info("Testing {}... Started", name);
        var i = 0;
        for (var word : OXFORD_3K) {
            if (word.length > strLenLimit) {
                continue;
            }
            long startNS;
            long endNS;
            startNS = System.nanoTime();
            occurrence = function.apply(KJV, word, 0, KJV.length).size();
            endNS = System.nanoTime();
            csvp.printRecord("KJV", name, word.length, occurrence, endNS - startNS);

            startNS = System.nanoTime();
            occurrence =
                    function.apply(SHAKESPEARE, word, 0, SHAKESPEARE.length).size();
            endNS = System.nanoTime();
            csvp.printRecord("SHAKESPEARE", name, word.length, occurrence, endNS - startNS);
            i += 1;
            if (i % 300 == 0) {
                LH.info("Testing {}... {}%", name, i / 30);
            }
        }
        LH.info("Testing {}... Finished", name);
    }

    void test() throws IOException {
        testSS(new BruteForceMatch(), Integer.MAX_VALUE);
        testSS(new NaiveMatch(), Integer.MAX_VALUE);
        testSS(new RabinKarpMatch(), Integer.MAX_VALUE);
        testSS(new KnuthMorrisPrattMatch(), Integer.MAX_VALUE);
        testSS(new ShiftOrMatch(), StrMatchUtils.LONG_SIZE);
        testSS(new BoyerMooreBadCharactersOnlyMatch(), StrMatchUtils.LONG_SIZE);
    }

    private static byte @NotNull [] readLC(String fileName) {
        try (var reader = new BufferedReader(new FileReader(
                Path.of(GitUtils.getGitRoot(), "test", "literature", fileName).toFile()))) {
            String line;
            StringBuilder content = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                content.append(line.trim().toLowerCase(Locale.ENGLISH));
            }
            return content.toString().getBytes(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        var test = new StrMatchBenchmarkUsingLiteratures();
        test.test();
    }
}
