package com.github.yu_zhejian.ystr.unsorted;

import static org.junit.jupiter.api.Assertions.*;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

class TrieTest {
    void testTrieHelper(@NotNull TrieInterface trie) {
        var th = new TrieHelper<Integer>(trie);
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
        assertEquals(9, trie.numWords());
        assertIterableEquals(
                List.of("and", "ant"),
                trie.traverse("an".getBytes(StandardCharsets.US_ASCII)).stream()
                        .map(i -> new String(i, StandardCharsets.US_ASCII))
                        .toList());
        assertIterableEquals(
                List.of(
                        "",
                        ":AGSTTCGSGTRCTSGCT\0\0XXXX",
                        "and",
                        "ant",
                        "ball",
                        "dad",
                        "dadaa",
                        "do",
                        "geek"),
                trie.traverse().stream()
                        .map(i -> new String(i, StandardCharsets.US_ASCII))
                        .toList());
    }

    @Test
    void testTrie() {
        var trie = new Trie();
        test(trie);
        testTrieHelper(trie);
    }

    @Test
    void testMapBasedTrie() {
        var trie = new MapBasedTrie();
        test(trie);
        testTrieHelper(trie);
    }
}
