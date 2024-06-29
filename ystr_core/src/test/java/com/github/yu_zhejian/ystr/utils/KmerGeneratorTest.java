package com.github.yu_zhejian.ystr.utils;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

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
        assertIterableEquals(
                List.of("AA", "AB", "BA", "BB"),
                convert(new KmerGenerator(new byte[] {'A', 'B'}, 2)));
        assertIterableEquals(List.of("AA"), convert(new KmerGenerator(new byte[] {'A'}, 2)));
        assertIterableEquals(
                List.of("AA", "AG", "AC", "GA", "GG", "GC", "CA", "CG", "CC"),
                convert(new KmerGenerator(new byte[] {'A', 'G', 'C'}, 2)));
        assertIterableEquals(
                List.of("AAA", "AAG", "AGA", "AGG", "GAA", "GAG", "GGA", "GGG"),
                convert(new KmerGenerator(new byte[] {'A', 'G'}, 3)));
    }
}
