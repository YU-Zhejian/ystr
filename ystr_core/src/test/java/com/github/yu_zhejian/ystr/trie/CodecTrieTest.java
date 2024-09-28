package com.github.yu_zhejian.ystr.trie;

import static org.junit.jupiter.api.Assertions.*;

import com.github.yu_zhejian.ystr.utils.Alphabets;
import com.github.yu_zhejian.ystr.utils.IterUtils;
import com.github.yu_zhejian.ystr.utils.RandomKmerGenerator;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

class CodecTrieTest {
    void testTrieHelper(@NotNull TrieInterface trie) {
        var th = new TrieMap<Integer>(trie);
        th.clear();
        assertNull(th.put("and".getBytes(StandardCharsets.US_ASCII), 1));
        assertEquals(1, th.put("and".getBytes(StandardCharsets.US_ASCII), 1));
        assertEquals(1, th.put("and".getBytes(StandardCharsets.US_ASCII), 2));
        assertNull(th.put("ant".getBytes(StandardCharsets.US_ASCII), 2));
        th.put(":sigma".getBytes(StandardCharsets.US_ASCII), 2);
        th.put("apache".getBytes(StandardCharsets.US_ASCII), null);
        assertEquals(2, th.get(":sigma".getBytes(StandardCharsets.US_ASCII)));
        assertNull(th.get(":sigma3".getBytes(StandardCharsets.US_ASCII)));
        assertNull(th.get("".getBytes(StandardCharsets.US_ASCII)));
        assertFalse(th.containsKey("".getBytes(StandardCharsets.US_ASCII)));
        assertTrue(th.containsKey("ant".getBytes(StandardCharsets.US_ASCII)));
        assertFalse(th.containsKey("ants".getBytes(StandardCharsets.US_ASCII)));
        assertIterableEquals(
                List.of(":sigma", "and", "ant", "apache"),
                trie.traverse().stream()
                        .map(i -> new String(i, StandardCharsets.US_ASCII))
                        .toList());
        assertIterableEquals(
                List.of(":sigma", "and", "ant", "apache"),
                th.keySet().stream()
                        .map(i -> new String(i, StandardCharsets.US_ASCII))
                        .toList());
        var values = new ArrayList<Integer>();
        values.add(2);
        values.add(2);
        values.add(2);
        values.add(null);
        assertIterableEquals(values, th.values());
    }

    void test(@NotNull TrieInterface trie) {
        trie.clear();
        for (final var s : List.of(
                "and",
                "ant",
                "annnnnnnt",
                "do",
                "geek",
                "dad",
                "ball",
                ":AGSTTCGSGTRCTSGCT\0\0XXXX",
                "ball",
                "dadaa")) {
            trie.add(s.getBytes(StandardCharsets.US_ASCII));
        }
        assertTrue(trie.contains("and".getBytes(StandardCharsets.US_ASCII)));
        assertTrue(trie.contains("ant".getBytes(StandardCharsets.US_ASCII)));
        assertTrue(trie.contains("do".getBytes(StandardCharsets.US_ASCII)));
        assertFalse(trie.contains("ann".getBytes(StandardCharsets.US_ASCII)));
        assertFalse(trie.contains("an".getBytes(StandardCharsets.US_ASCII)));
        assertFalse(trie.contains("z".getBytes(StandardCharsets.US_ASCII)));
        assertFalse(trie.contains("".getBytes(StandardCharsets.US_ASCII)));
        trie.add(new byte[] {});
        assertTrue(trie.contains("".getBytes(StandardCharsets.US_ASCII)));
        assertEquals(24, trie.treeHeight());
        assertEquals(10, trie.numWords());
        //        System.out.println(trie.traverse("".getBytes(StandardCharsets.US_ASCII)).stream()
        //                .map(i -> new String(i, StandardCharsets.US_ASCII))
        //                .toList());
        assertIterableEquals(
                List.of("and", "annnnnnnt", "ant"),
                trie.traverse("an".getBytes(StandardCharsets.US_ASCII)).stream()
                        .map(i -> new String(i, StandardCharsets.US_ASCII))
                        .toList());
        assertIterableEquals(
                List.of("ball"),
                trie.traverse("b".getBytes(StandardCharsets.US_ASCII)).stream()
                        .map(i -> new String(i, StandardCharsets.US_ASCII))
                        .toList());
        assertIterableEquals(
                List.of(),
                trie.traverse("X".getBytes(StandardCharsets.US_ASCII)).stream()
                        .map(i -> new String(i, StandardCharsets.US_ASCII))
                        .toList());
        assertIterableEquals(
                List.of(
                        "",
                        ":AGSTTCGSGTRCTSGCT\0\0XXXX",
                        "and",
                        "annnnnnnt",
                        "ant",
                        "ball",
                        "dad",
                        "dadaa",
                        "do",
                        "geek"),
                trie.traverse().stream()
                        .map(i -> new String(i, StandardCharsets.US_ASCII))
                        .toList());
        System.out.println("Trie test result: " + trie);
    }

    void testRandomKmers(@NotNull TrieInterface trie) {
        for (int k = 3; k < 6; k++) {
            trie.clear();
            final var set = new ObjectOpenHashSet<String>();
            final var kmers =
                    IterUtils.head(new RandomKmerGenerator(Alphabets.DNA5_ALPHABET, k), 50);
            while (kmers.hasNext()) {
                final var kmer = kmers.next();
                trie.add(kmer);
                set.add(new String(kmer, StandardCharsets.US_ASCII));
            }
            for (var kmer : set) {
                assertTrue(trie.contains(kmer.getBytes(StandardCharsets.US_ASCII)));
            }
            final var kmers2 =
                    IterUtils.head(new RandomKmerGenerator(Alphabets.DNA5_ALPHABET, k), 500);
            while (kmers2.hasNext()) {
                final var kmer = kmers2.next();
                assertEquals(
                        set.contains(new String(kmer, StandardCharsets.US_ASCII)),
                        trie.contains(kmer));
            }
        }
    }

    @Test
    void testTrie() {
        var trie = new CodecTrie();
        test(trie);
        testTrieHelper(trie);
        testRandomKmers(trie);
    }

    @Test
    void testMapBasedTrie() {
        var trie = new MapBasedTrie();
        test(trie);
        testTrieHelper(trie);
        testRandomKmers(trie);
    }

    @Test
    void testTernarySearchTrie() {
        var trie = new TernarySearchTrie();
        test(trie);
        testTrieHelper(trie);
        testRandomKmers(trie);
    }
}
