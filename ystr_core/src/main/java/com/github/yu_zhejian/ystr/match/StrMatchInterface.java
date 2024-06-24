package com.github.yu_zhejian.ystr.match;

import it.unimi.dsi.fastutil.ints.IntArrayList;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public interface StrMatchInterface {

    IntArrayList apply(
            final byte @NotNull [] haystack,
            final byte @NotNull [] needle,
            final int start,
            final int end);

    default IntArrayList apply(final byte @NotNull [] haystack, final byte @NotNull [] needle) {
        return apply(haystack, needle, 0, haystack.length);
    }

    static IntArrayList fastApply(
            final @NotNull Supplier<StrMatchInterface> supplier,
            final byte @NotNull [] haystack,
            final byte @NotNull [] needle,
            final int start,
            final int end) {
        return supplier.get().apply(haystack, needle, start, end);
    }

    static IntArrayList fastApply(
            final @NotNull Supplier<StrMatchInterface> supplier,
            final byte @NotNull [] haystack,
            final byte @NotNull [] needle) {
        return fastApply(supplier, haystack, needle, 0, haystack.length);
    }
}
