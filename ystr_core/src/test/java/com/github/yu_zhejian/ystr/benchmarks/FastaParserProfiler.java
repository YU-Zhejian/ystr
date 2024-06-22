package com.github.yu_zhejian.ystr.benchmarks;

import com.github.yu_zhejian.ystr.io.TwoBitParser;
import com.github.yu_zhejian.ystr.test_utils.GitUtils;

import io.vavr.Tuple;
import io.vavr.Tuple3;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class FastaParserProfiler {
    private TwoBitParser twoBitParser;
    private List<Tuple3<String, Integer, Integer>> coordinates;
    private Map<String, Integer> seqNameIDMap;
    private static final int NUM_RDN_INTERVALS = 200;
    private static final int NUM_BENCH_ROUNDS = 100;

    public static void main(String[] args) throws IOException {
        var fastaParserProfiler = new FastaParserProfiler();
        fastaParserProfiler.setup();
        for (var i = 0; i < NUM_BENCH_ROUNDS; i++) {
            fastaParserProfiler.benchTwoBit();
        }
        fastaParserProfiler.tearDown();
    }

    public void tearDown() throws IOException {
        twoBitParser.close();
    }

    public void setup() throws IOException {
        twoBitParser =
                new TwoBitParser(Path.of(GitUtils.getGitRoot(), "test", "ref", "ce11.genomic.2bit")
                        .toFile());
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

    public void benchTwoBit() throws IOException {
        for (var coordinate : coordinates) {
            var seq = twoBitParser.getSequence(
                    seqNameIDMap.get(coordinate._1()), coordinate._2(), coordinate._3(), true);
            assert seq.length > 0;
        }
    }
}
