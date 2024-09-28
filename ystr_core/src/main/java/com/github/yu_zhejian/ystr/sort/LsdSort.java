package com.github.yu_zhejian.ystr.sort;

import com.github.yu_zhejian.ystr.utils.StrUtils;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public final class LsdSort implements SortInterface {

    @Override
    public void sort(@NotNull List<byte[]> strings) {
        if (strings.isEmpty()) {
            return;
        }
        final int keyLen = strings.get(0).length;
        for (int i = 1; i < strings.size(); i++) {
            if (strings.get(i).length != keyLen) {
                throw new IllegalArgumentException(
                        "The %dth string length != first string length; were: %d vs. %d"
                                .formatted(i, strings.get(i).length, keyLen));
            }
        }
        final int[] count = new int[StrUtils.ALPHABET_SIZE];
        final var aux = new ObjectArrayList<byte[]>();
        aux.ensureCapacity(strings.size());
        aux.addAll(strings);
        for (int d = keyLen - 1; d >= 0; d--) {
            Arrays.fill(count, 0);
            for (final byte[] bytes : strings) {
                count[(bytes[d] & StrUtils.BYTE_TO_UNSIGNED_MASK) + 1]++;
            }
            for (int k = 1; k < StrUtils.ALPHABET_SIZE; k++) {
                count[k] += count[k - 1];
            }
            for (final byte[] string : strings) {
                aux.set(count[(string[d] & StrUtils.BYTE_TO_UNSIGNED_MASK)]++, string);
            }
            for (int i = 0; i < strings.size(); i++) {
                strings.set(i, aux.get(i));
            }
        }
    }
}
