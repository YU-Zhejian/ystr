package com.github.yu_zhejian.ystr.alphabet;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class KmerGeneratorTest {

    List<String> convert(@NotNull Iterator<byte[]> it) {
        List<String> list = new ArrayList<>();
        while (it.hasNext()) {
            byte[] b = it.next();
            list.add(new String(b));
        }
        return list;
    }

    @Test
    void testKmers() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new KmerGenerator(AlphabetConstants.DNA_ALPHABET, -2));
        assertThrows(
                IllegalArgumentException.class,
                () -> new KmerGenerator(AlphabetConstants.DNA_ALPHABET, 0));
        assertIterableEquals(
                List.of("AA", "AB", "BA", "BB"),
                convert(new KmerGenerator(new Alphabet(new byte[] {'A', 'B'}), 2)));
        assertIterableEquals(
                List.of("AA"), convert(new KmerGenerator(new Alphabet(new byte[] {'A'}), 2)));
        assertIterableEquals(
                List.of("AA", "AC", "AG", "CA", "CC", "CG", "GA", "GC", "GG"),
                convert(new KmerGenerator(new Alphabet(new byte[] {'A', 'C', 'G'}), 2)));
        assertIterableEquals(
                List.of("AAA", "AAG", "AGA", "AGG", "GAA", "GAG", "GGA", "GGG"),
                convert(new KmerGenerator(new Alphabet(new byte[] {'A', 'G'}), 3)));
    }
}
