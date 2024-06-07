package com.github.yu_zhejian.ystr;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;

public final class StrSort {
    private static final int BYTE_MAX = 128;

    private StrSort() {}

    /**
     * Key-Indexed counting for strings.
     *
     * <p>This is a sorting algorithm from Robert Sedgewick's Implementation.
     *
     * @param keys Bytes to be sorted.
     * @return Thr sorted index of the keys where order of the equal keys are preserved.
     */
    @Contract(pure = true)
    public static int @NotNull [] keyIndexedCounting(final byte @NotNull [] keys) {
        final var counts = new int[BYTE_MAX + 1];
        for (final var key : keys) {
            counts[key + 1]++;
        }
        for (int i = 0; i < BYTE_MAX; i++) {
            counts[i + 1] += counts[i];
        }
        final var retl = new int[keys.length];
        for (int i = 0; i < keys.length; i++) {
            retl[i] = counts[keys[i]]++;
        }
        return retl;
    }

    /**
     * Least Significant Digit sorting, for strings of equal lengths.
     *
     * @param strings As described.
     * @return As described.
     */
    public static @Unmodifiable List<byte[]> lsdSort(
            final @NotNull List<byte[]> strings, final boolean trustStringLength) {
        if (strings.isEmpty()) {
            return List.of();
        }
        final var numStrs = strings.size();
        final var strLen = strings.get(0).length;
        if (!trustStringLength) {
            for (int i = 0; i < numStrs; i++) {
                if (strings.get(i).length != strLen) {
                    throw new IllegalArgumentException(
                            "String at index %d have different length than at index 0. Actual: %d vs. %d"
                                    .formatted(i, strings.get(i).length, strLen));
                }
            }
        }
        final var keys = new byte[numStrs];
        var prevRoundSorted = new ArrayList<>(strings);
        final var thisRoundSorted = new ArrayList<>(prevRoundSorted);
        for (int i = strLen - 1; i >= 0; i--) {
            for (var j = 0; j < numStrs; j++) {
                keys[j] = strings.get(j)[i];
            }
            final var keyIndex = keyIndexedCounting(keys);
            for (var j = 0; j < numStrs; j++) {
                // TODO: Is it possible for optimization to make it stores indices only?
                thisRoundSorted.set(keyIndex[j], prevRoundSorted.get(j));
            }
            prevRoundSorted = thisRoundSorted;
        }
        return thisRoundSorted;
    }

    /**
     * Least Significant Digit sorting, for strings of equal lengths.
     *
     * @param strings As described.
     * @return As described.
     */
    public static @Unmodifiable List<byte[]> lsdSort(final @NotNull List<byte[]> strings) {
        return lsdSort(strings, false);
    }
}
