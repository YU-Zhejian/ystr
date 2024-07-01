package com.github.yu_zhejian.ystr.match;

import it.unimi.dsi.fastutil.ints.IntList;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/** Interface for online exact string matching. */
public interface StrMatchInterface {

    /**
     * Unchecked variant of {@link #apply(byte[], byte[], int, int)}.
     *
     * @param haystack As described.
     * @param needle As described.
     * @param start As described.
     * @param end As described.
     * @return As described.
     */
    IntList applyUnchecked(
            final byte @NotNull [] haystack,
            final byte @NotNull [] needle,
            final int start,
            final int end);

    /**
     * Apply the online exact match algorithm.
     *
     * @param haystack As described.
     * @param needle As described.
     * @param start As described.
     * @param end As described.
     * @return As described.
     * @throws IllegalArgumentException See {@link StrMatchUtils#ensureParametersValid(byte[],
     *     byte[], int, int)}.
     */
    default IntList apply(
            final byte @NotNull [] haystack,
            final byte @NotNull [] needle,
            final int start,
            final int end) {
        StrMatchUtils.ensureParametersValid(haystack, needle, start, end);
        return applyUnchecked(haystack, needle, start, end);
    }

    /**
     * Full-length variant of {@link #apply(byte[], byte[], int, int)}.
     *
     * @param haystack As described.
     * @param needle As described.
     * @return As described.
     */
    default IntList apply(final byte @NotNull [] haystack, final byte @NotNull [] needle) {
        return applyUnchecked(haystack, needle, 0, haystack.length);
    }

    /**
     * Supplier variant of {@link #apply(byte[], byte[], int, int)}.
     *
     * @param supplier As described.
     * @param haystack As described.
     * @param needle As described.
     * @param start As described.
     * @param end As described.
     * @return As described.
     * @throws IllegalArgumentException See {@link StrMatchUtils#ensureParametersValid(byte[],
     *     byte[], int, int)}.
     */
    static IntList convenientApply(
            final @NotNull Supplier<StrMatchInterface> supplier,
            final byte @NotNull [] haystack,
            final byte @NotNull [] needle,
            final int start,
            final int end) {
        return supplier.get().apply(haystack, needle, start, end);
    }

    /**
     * Supplier unchecked variant of {@link #apply(byte[], byte[], int, int)}.
     *
     * @param supplier As described.
     * @param haystack As described.
     * @param needle As described.
     * @param start As described.
     * @param end As described.
     * @return As described.
     * @throws IllegalArgumentException See {@link StrMatchUtils#ensureParametersValid(byte[],
     *     byte[], int, int)}.
     */
    static IntList convenientApplyUnchecked(
            final @NotNull Supplier<StrMatchInterface> supplier,
            final byte @NotNull [] haystack,
            final byte @NotNull [] needle,
            final int start,
            final int end) {
        return supplier.get().applyUnchecked(haystack, needle, start, end);
    }

    /**
     * Supplier full-length variant of {@link #apply(byte[], byte[], int, int)}.
     *
     * @param supplier As described.
     * @param haystack As described.
     * @param needle As described.
     * @return As described.
     */
    static IntList convenientApply(
            final @NotNull Supplier<StrMatchInterface> supplier,
            final byte @NotNull [] haystack,
            final byte @NotNull [] needle) {
        return convenientApply(supplier, haystack, needle, 0, haystack.length);
    }
}
