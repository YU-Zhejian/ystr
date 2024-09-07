package com.github.yu_zhejian.ystr.unsorted;

import static org.junit.jupiter.api.Assertions.*;

import com.github.yu_zhejian.ystr.utils.AlphabetCodec;
import com.github.yu_zhejian.ystr.utils.Alphabets;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

class TrieTest {
    void test(TrieInterface trie) {
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
        var trie = new Trie(new AlphabetCodec(Alphabets.FULL_ALPHABET, 0));
        test(trie);
    }

    @Test
    void testMapBasedTrie() {
        var trie = new MapBasedTrie();
        test(trie);
    }
}
