package com.github.yu_zhejian.ystr;

import com.github.yu_zhejian.ystr.test_utils.CSVUtils;
import com.github.yu_zhejian.ystr.test_utils.GitUtils;

import io.vavr.Function4;

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
    private static final byte[] KJV = readLC("kjv_gutenberg.txt");
    private static final byte[] SHAKESPEARE = readLC("shakespeare_gutenberg.txt");
    private static final List<byte[]> OXFORD_3K;
    private final CSVPrinter csvp;
    private final Logger LH = LoggerFactory.getLogger(StrMatchBenchmarkUsingLiteratures.class);

    static {
        try (var reader = new BufferedReader(new BufferedReader(new FileReader(
                Path.of(GitUtils.getGitRoot(), "test", "oxford_3k.txt").toFile())))) {
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

    public static void main(String[] args) throws IOException {
        var test = new StrMatchBenchmarkUsingLiteratures();
        test.test();
    }

    void testSS(
            @NotNull Function4<byte[], byte[], Integer, Integer, List<Integer>> function,
            String name,
            int strLenLimit)
            throws IOException {
        LH.info("Testing {}... Warming up JVM", name);
        var rng = new Random();
        for (int j = 0; j < 20; j++) {
            var word = OXFORD_3K.get(rng.nextInt(OXFORD_3K.size()) - 1);
            IterUtils.exhaust(
                    function.apply(SHAKESPEARE, word, 0, SHAKESPEARE.length).iterator());
            IterUtils.exhaust(function.apply(KJV, word, 0, KJV.length).iterator());
        }

        LH.info("Testing {}... Started", name);
        var i = 0;
        for (var word : OXFORD_3K) {
            if (word.length > strLenLimit) {
                continue;
            }
            long startNS;
            long endNS;
            int occurrence;
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
        testSS(StrMatch::bruteForceMatch, "bruteForceMatch", Integer.MAX_VALUE);
        testSS(
                StrMatch::naiveMatch,
                "naiveMatch",
                Integer.MAX_VALUE); // FIXME: This algorithm is having errors!
        testSS(StrMatch::rabinKarpMatch, "rabinKarpMatch", Integer.MAX_VALUE);
        testSS(StrMatch::knuthMorrisPrattMatch, "knuthMorrisPrattMatch", Integer.MAX_VALUE);
        testSS(StrMatch::shiftOrMatch, "shiftOrMatch", 64);
    }

    private static byte @NotNull [] readLC(String fileName) {
        try (var reader = new BufferedReader(
                new FileReader(Path.of(GitUtils.getGitRoot(), "test", fileName).toFile()))) {
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
}
