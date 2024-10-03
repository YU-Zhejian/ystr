package com.github.yu_zhejian.ystr.match;

import it.unimi.dsi.fastutil.ints.IntList;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/** Interface for online exact string matching. */
public interface StrMatchInterface {

    /**
     * As described.
     *
     * @param haystack As described.
     * @param needle As described.
     * @param start As described.
     * @param end As described.
     * @param limitTo As described.
     * @return As described.
     * @see #apply(byte[], byte[], int, int, int)
     */
    IntList applyUnchecked(
            final byte @NotNull [] haystack,
            final byte @NotNull [] needle,
            final int start,
            final int end,
            final int limitTo);

    /**
     * As described.
     *
     * @param haystack As described.
     * @param needle As described.
     * @param start As described.
     * @param end As described.
     * @return As described.
     * @see #apply(byte[], byte[], int, int, int)
     */
    default IntList applyUnchecked(
            final byte @NotNull [] haystack,
            final byte @NotNull [] needle,
            final int start,
            final int end) {
        return applyUnchecked(haystack, needle, start, end, Integer.MAX_VALUE);
    }

    /**
     * As described.
     *
     * @param haystack As described.
     * @param needle As described.
     * @return As described.
     * @see #apply(byte[], byte[], int, int, int)
     */
    default IntList applyUnchecked(final byte @NotNull [] haystack, final byte @NotNull [] needle) {
        return applyUnchecked(haystack, needle, 0, needle.length, Integer.MAX_VALUE);
    }

    /**
     * As described.
     *
     * @param haystack As described.
     * @param needle As described.
     * @param limitTo As described.
     * @return As described.
     * @see #apply(byte[], byte[], int, int, int)
     */
    default IntList applyUnchecked(
            final byte @NotNull [] haystack, final byte @NotNull [] needle, final int limitTo) {
        return applyUnchecked(haystack, needle, 0, needle.length, limitTo);
    }

    /**
     * Ensure that the input is valid.
     *
     * @param haystack As described.
     * @param needle As described.
     * @param start As described.
     * @param end As described.
     * @param limitTo As described.
     * @throws IllegalArgumentException See {@link StrMatchUtils#ensureParametersValid}. Also thrown
     *     when the {@code needle} is empty or the {@code limitTo} is not positive.
     */
    default void assertInputIsValid(
            final byte @NotNull [] haystack,
            final byte @NotNull [] needle,
            final int start,
            final int end,
            final int limitTo) {
        if (needle.length == 0) {
            throw new IllegalArgumentException("Needle is empty");
        }
        if (limitTo <= 0) {
            throw new IllegalArgumentException("limitTo (%d) is not positive".formatted(limitTo));
        }
        StrMatchUtils.ensureParametersValid(haystack, needle, start, end);
    }

    /**
     * As described.
     *
     * @param haystack As described.
     * @param needle As described.
     * @param start As described.
     * @param end As described.
     * @return As described.
     * @throws IllegalArgumentException As described.
     * @see #apply(byte[], byte[], int, int, int)
     */
    default IntList apply(
            final byte @NotNull [] haystack,
            final byte @NotNull [] needle,
            final int start,
            final int end) {
        return apply(haystack, needle, start, end, Integer.MAX_VALUE);
    }

    /**
     * Apply the online exact match algorithm.
     *
     * @param haystack The text to search from.
     * @param needle The pattern.
     * @param start Desired start offset of the text, 0-based inclusive.
     * @param end Desired end offset of the text, 1-based exclusive.
     * @param limitTo Stop when found this number of occurrences.
     * @return List of offsets of pattern hits among the text.
     * @throws IllegalArgumentException See {@link #assertInputIsValid(byte[], byte[], int, int,
     *     int)}
     */
    default IntList apply(
            final byte @NotNull [] haystack,
            final byte @NotNull [] needle,
            final int start,
            final int end,
            final int limitTo) {
        assertInputIsValid(haystack, needle, start, end, limitTo);
        return applyUnchecked(haystack, needle, start, end, limitTo);
    }

    /**
     * As described.
     *
     * @param haystack As described.
     * @param needle As described.
     * @return As described.
     * @throws IllegalArgumentException As described.
     * @see #apply(byte[], byte[], int, int, int)
     */
    default IntList apply(final byte @NotNull [] haystack, final byte @NotNull [] needle) {
        return apply(haystack, needle, 0, haystack.length, Integer.MAX_VALUE);
    }

    /**
     * As described.
     *
     * @param haystack As described.
     * @param needle As described.
     * @param limitTo As described.
     * @return As described.
     * @throws IllegalArgumentException As described.
     * @see #apply(byte[], byte[], int, int, int)
     */
    default IntList apply(
            final byte @NotNull [] haystack, final byte @NotNull [] needle, final int limitTo) {
        return apply(haystack, needle, 0, haystack.length, limitTo);
    }

    /**
     * Supplier variant of {@link #apply(byte[], byte[], int, int, int)}.
     *
     * @param supplier As described.
     * @param haystack As described.
     * @param needle As described.
     * @param start As described.
     * @param end As described.
     * @param limitTo As described.
     * @return As described.
     * @throws IllegalArgumentException As described.
     */
    static IntList convenientApply(
            final @NotNull Supplier<StrMatchInterface> supplier,
            final byte @NotNull [] haystack,
            final byte @NotNull [] needle,
            final int start,
            final int end,
            final int limitTo) {
        return supplier.get().apply(haystack, needle, start, end, limitTo);
    }

    /**
     * Supplier variant of {@link #apply(byte[], byte[], int, int, int)}.
     *
     * @param supplier As described.
     * @param haystack As described.
     * @param needle As described.
     * @param limitTo As described.
     * @return As described.
     * @throws IllegalArgumentException As described.
     */
    static IntList convenientApply(
            final @NotNull Supplier<StrMatchInterface> supplier,
            final byte @NotNull [] haystack,
            final byte @NotNull [] needle,
            final int limitTo) {
        return supplier.get().apply(haystack, needle, 0, needle.length, limitTo);
    }

    /**
     * Supplier variant of {@link #apply(byte[], byte[], int, int, int)}.
     *
     * @param supplier As described.
     * @param haystack As described.
     * @param needle As described.
     * @param start As described.
     * @param end As described.
     * @return As described.
     * @throws IllegalArgumentException As described.
     */
    static IntList convenientApply(
            final @NotNull Supplier<StrMatchInterface> supplier,
            final byte @NotNull [] haystack,
            final byte @NotNull [] needle,
            final int start,
            final int end) {
        return supplier.get().apply(haystack, needle, start, end, Integer.MAX_VALUE);
    }

    /**
     * Supplier variant of {@link #apply(byte[], byte[], int, int, int)}.
     *
     * @param supplier As described.
     * @param haystack As described.
     * @param needle As described.
     * @return As described.
     * @throws IllegalArgumentException As described.
     */
    static IntList convenientApply(
            final @NotNull Supplier<StrMatchInterface> supplier,
            final byte @NotNull [] haystack,
            final byte @NotNull [] needle) {
        return supplier.get().apply(haystack, needle, 0, needle.length, Integer.MAX_VALUE);
    }

    /**
     * Supplier variant of {@link #apply(byte[], byte[], int, int, int)}.
     *
     * @param supplier As described.
     * @param haystack As described.
     * @param needle As described.
     * @param start As described.
     * @param end As described.
     * @param limitTo As described.
     * @return As described.
     * @throws IllegalArgumentException As described.
     */
    static IntList convenientApplyUnchecked(
            final @NotNull Supplier<StrMatchInterface> supplier,
            final byte @NotNull [] haystack,
            final byte @NotNull [] needle,
            final int start,
            final int end,
            final int limitTo) {
        return supplier.get().applyUnchecked(haystack, needle, start, end, limitTo);
    }

    /**
     * Supplier variant of {@link #apply(byte[], byte[], int, int, int)}.
     *
     * @param supplier As described.
     * @param haystack As described.
     * @param needle As described.
     * @param limitTo As described.
     * @return As described.
     * @throws IllegalArgumentException As described.
     */
    static IntList convenientApplyUnchecked(
            final @NotNull Supplier<StrMatchInterface> supplier,
            final byte @NotNull [] haystack,
            final byte @NotNull [] needle,
            final int limitTo) {
        return supplier.get().applyUnchecked(haystack, needle, 0, needle.length, limitTo);
    }

    /**
     * Supplier variant of {@link #apply(byte[], byte[], int, int, int)}.
     *
     * @param supplier As described.
     * @param haystack As described.
     * @param needle As described.
     * @param start As described.
     * @param end As described.
     * @return As described.
     * @throws IllegalArgumentException As described.
     */
    static IntList convenientApplyUnchecked(
            final @NotNull Supplier<StrMatchInterface> supplier,
            final byte @NotNull [] haystack,
            final byte @NotNull [] needle,
            final int start,
            final int end) {
        return supplier.get().applyUnchecked(haystack, needle, start, end, Integer.MAX_VALUE);
    }

    /**
     * Supplier variant of {@link #apply(byte[], byte[], int, int, int)}.
     *
     * @param supplier As described.
     * @param haystack As described.
     * @param needle As described.
     * @return As described.
     * @throws IllegalArgumentException As described.
     */
    static IntList convenientApplyUnchecked(
            final @NotNull Supplier<StrMatchInterface> supplier,
            final byte @NotNull [] haystack,
            final byte @NotNull [] needle) {
        return supplier.get().applyUnchecked(haystack, needle, 0, needle.length, Integer.MAX_VALUE);
    }
}
