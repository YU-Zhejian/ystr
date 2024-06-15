package com.github.yu_zhejian.ystr.io;

import static org.junit.jupiter.api.Assertions.*;

import com.github.yu_zhejian.ystr.IterUtils;

import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.List;

class FastxIteratorTest {

    @Test
    void testFasta() {
        assertIterableEquals(
                List.of(FastxRecord.ofStrings("chr1", "AAAA")),
                IterUtils.collect(new FastxIterator(new StringReader(">chr1\nAAAA"))));
        assertIterableEquals(List.of(), IterUtils.collect(new FastxIterator(new StringReader(""))));
        assertIterableEquals(
                List.of(), IterUtils.collect(new FastxIterator(new StringReader("\n\n"))));
        assertIterableEquals(
                List.of(
                        FastxRecord.ofStrings("chr1", "AAAAGGGG"),
                        FastxRecord.ofStrings("chr2", "CCCCTTTT"),
                        FastxRecord.ofStrings("chr3", "")),
                IterUtils.collect(new FastxIterator(
                        new StringReader(">chr1\nAAAA\nGGGG\n>chr2\nCCCC\nTTTT\n>chr3"))));
        assertIterableEquals(
                List.of(
                        FastxRecord.ofStrings("chr1", "AAAAGGGG"),
                        FastxRecord.ofStrings("chr2", "CCCCTTTT"),
                        FastxRecord.ofStrings("chr3", "")),
                IterUtils.collect(new FastxIterator(
                        new StringReader(">chr1\nAAAA\nGGGG\n\n\n>chr2\nCCCC\nTTTT\n>chr3"))));
        assertIterableEquals(
                List.of(
                        FastxRecord.ofStrings("chr1", "AAAA"),
                        FastxRecord.ofStrings("chr2", "CCCC")),
                IterUtils.collect(new FastxIterator(new StringReader(">chr1\nAAAA\n>chr2\nCCCC"))));
        assertIterableEquals(
                List.of(
                        FastxRecord.ofStrings("chr1", "AAAA"),
                        FastxRecord.ofStrings("chr2", "CCCC")),
                IterUtils.collect(
                        new FastxIterator(new StringReader(">chr1\nAAAA\n>chr2\nCCCC\n\n"))));
    }

    @Test
    void testFastq(){
        assertIterableEquals(
                List.of(FastxRecord.ofStrings("A", "AAAA", "CCCC")),
                IterUtils.collect(new FastxIterator(new StringReader("@A\nAAAA\n+\nCCCC\n"))));
        assertIterableEquals(
                List.of(FastxRecord.ofStrings("A", "AAAA", "CCCC"), FastxRecord.ofStrings("B", "AAAAT", "CCCCT")),
                IterUtils.collect(new FastxIterator(new StringReader("@A\nAAAA\n+\nCCCC\n@B\nAAAAT\n+\nCCCCT\n"))));
        assertIterableEquals(
                List.of(),
                IterUtils.collect(new FastxIterator(new StringReader(""))));
        assertIterableEquals(
                List.of(),
                IterUtils.collect(new FastxIterator(new StringReader("\n\n"))));
    }
}
