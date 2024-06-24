package com.github.yu_zhejian.ystr;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

class StrSortTest {

    @Test
    void keyIndexedCounting() {
        assertArrayEquals(new int[] {}, StrSort.keyIndexedCounting("".getBytes()));
        assertArrayEquals(new int[] {0, 1, 2, 3}, StrSort.keyIndexedCounting("ACGT".getBytes()));
        assertArrayEquals(
                new int[] {0, 1, 2, 3, 4}, StrSort.keyIndexedCounting("ACCGT".getBytes()));
        assertArrayEquals(
                new int[] {0, 1, 3, 2, 4}, StrSort.keyIndexedCounting("ACGCT".getBytes()));
        assertArrayEquals(new int[] {3, 1, 2, 0}, StrSort.keyIndexedCounting("TCGA".getBytes()));
    }

    void isSorted(@NotNull List<byte[]> strings) {
        if (strings.isEmpty()) {
            return;
        }
        for (int i = 0; i < strings.size() - 1; i++) {
            if (StrLibc.strcmp(strings.get(i), strings.get(i + 1)) > 0) {
                fail("The array is not sorted between %d and %d!".formatted(i, i + 1));
            }
        }
    }

    @Test
    void lsdSort() {
        var kmers1 = new ArrayList<byte[]>();

        var kmers2 = Stream.of("AGCT", "ACGT", "TCGA", "AAAA", "TCGA", "ACCC")
                .map(it -> it.getBytes(StandardCharsets.UTF_8))
                .toList();

        var kmers3 = Stream.of("A", "AGCT", "ACGT", "TCGA", "AAAA", "TCGA", "ACCC")
                .map(it -> it.getBytes(StandardCharsets.UTF_8))
                .toList();

        isSorted(StrSort.lsdSort(kmers1));
        isSorted(StrSort.lsdSort(kmers2));
        assertThrows(IllegalArgumentException.class, () -> {
            StrSort.lsdSort(kmers3);
        });
    }
}
