package com.github.yu_zhejian.ystr.io;

import static org.junit.jupiter.api.Assertions.*;

import com.github.yu_zhejian.ystr.test_utils.GitUtils;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

class TwoBitParserTest {
    Object2ObjectArrayMap<String, byte[]> seqs;

    TwoBitParserTest() throws IOException {
        seqs = new Object2ObjectArrayMap<>();
        try (var parser = FastxIterator.read(
                Path.of(GitUtils.getGitRoot(), "test", "small", "test_2bit", "simple.fa")
                        .toFile())) {
            while (parser.hasNext()) {
                var n = parser.next();
                seqs.put(n.seqid(), n.seq());
            }
        }
    }

    @Test
    void testLong() throws IOException {
        try (var parser = new TwoBitParser(
                Path.of(GitUtils.getGitRoot(), "test", "small", "test_2bit", "simple_l.2bit")
                        .toFile())) {
            assertTrue(parser.isLittleEndian());
            assertTrue(parser.isSupportsLongSequences());
            test(parser);
        }
    }

    void test(@NotNull TwoBitParser parser) throws IOException {
        assertEquals(8, parser.size());
        assertIterableEquals(seqs.keySet(), parser.getSeqNames());
        var parsedSeqLen = parser.getSeqNameLengthMap();
        for (var k : seqs.keySet()) {
            assertEquals(seqs.get(k).length, parsedSeqLen.getInt(k));
        }
        var chr1str = "AAAAAGGGGGCCCCCTTTTT";
        for (var i = 0; i < chr1str.length() - 1; i++) {
            for (var j = i; j < chr1str.length(); j++) {
                var expected = chr1str.substring(i, j).getBytes(StandardCharsets.US_ASCII);
                var actual = parser.getSequence(0, i, j, true);
                assertArrayEquals(
                        expected,
                        actual,
                        "Error at pos %d-%d! %s vs. %s"
                                .formatted(
                                        i,
                                        j,
                                        new String(expected, StandardCharsets.US_ASCII),
                                        new String(actual, StandardCharsets.US_ASCII)));
            }
        }
        for (var i = 0; i < seqs.size(); i++) {
            var seqName = parser.getSeqName(i);
            var expected = seqs.get(seqName);
            var actual = parser.getSequence(i, 0, parser.getSeqLength(i), true);
            assertArrayEquals(
                    expected,
                    actual,
                    "Error at chr %s! %s vs. %s"
                            .formatted(
                                    seqName,
                                    new String(expected, StandardCharsets.US_ASCII),
                                    new String(actual, StandardCharsets.US_ASCII)));
        }
    }

    @Test
    void testShort() throws IOException {
        try (var parser = new TwoBitParser(
                Path.of(GitUtils.getGitRoot(), "test", "small", "test_2bit", "simple.2bit")
                        .toFile())) {
            assertTrue(parser.isLittleEndian());
            assertFalse(parser.isSupportsLongSequences());
            test(parser);
        }
    }

    @Test
    void testCe11() throws IOException {
        var ce11seqs = new Object2ObjectArrayMap<String, byte[]>();
        try (var parser =
                FastxIterator.read(Path.of(GitUtils.getGitRoot(), "test", "ref", "ce11.genomic.fna")
                        .toFile())) {
            while (parser.hasNext()) {
                var n = parser.next();
                ce11seqs.put(n.seqid(), n.seq());
            }
        }
        try (var parser =
                new TwoBitParser(Path.of(GitUtils.getGitRoot(), "test", "ref", "ce11.genomic.2bit")
                        .toFile())) {
            assertTrue(parser.isLittleEndian());
            assertFalse(parser.isSupportsLongSequences());
            assertEquals(7, parser.size());
            assertIterableEquals(ce11seqs.keySet(), parser.getSeqNames());
            var parsedSeqLen = parser.getSeqNameLengthMap();
            for (var k : ce11seqs.keySet()) {
                assertEquals(ce11seqs.get(k).length, parsedSeqLen.getInt(k));
            }
            for (var i = 0; i < ce11seqs.size(); i++) {
                var seqName = parser.getSeqName(i);
                var expected = ce11seqs.get(seqName);
                var actual = parser.getSequence(i, 0, parser.getSeqLength(i), true);
                assertArrayEquals(expected, actual, "Error at chr %s!".formatted(seqName));
            }
        }
    }

    @Test
    void decodePrecomputed() {
        for (var i = 0; i < 256; i++) {
            assertArrayEquals(
                    TwoBitParser.decode((byte) i), TwoBitParser.decodePrecomputed((byte) i));
        }
    }
}
