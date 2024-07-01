package com.github.yu_zhejian.ystr.match;

import com.github.yu_zhejian.ystr.utils.StrUtils;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * Shift-or string matching.
 *
 * <p>TODO: More docs needed.
 *
 * @see <a href="https://www-igm.univ-mlv.fr/~lecroq/string/node6.html">C source</a>
 */
public final class ShiftOrMatch implements StrMatchInterface {
    @Override
    public IntList apply(byte @NotNull [] haystack, byte @NotNull [] needle, int start, int end) {
        if (needle.length > StrUtils.LONG_SIZE) {
            throw new IllegalArgumentException(
                    "Needle length should not exceed 64! Actual: %d".formatted(needle.length));
        }
        return StrMatchInterface.super.apply(haystack, needle, start, end);
    }

    @Override
    public IntList apply(byte @NotNull [] haystack, byte @NotNull [] needle) {
        if (needle.length > StrUtils.LONG_SIZE) {
            throw new IllegalArgumentException(
                    "Needle length should not exceed 64! Actual: %d".formatted(needle.length));
        }
        return StrMatchInterface.super.apply(haystack, needle);
    }

    @Override
    public @NotNull IntList applyUnchecked(
            final byte @NotNull [] haystack,
            final byte @NotNull [] needle,
            final int start,
            final int end) {
        final var retl = new IntArrayList();

        // Pre-processing
        long mask = 0L;
        final var positionOfEachByteOnNeedle = new long[StrUtils.ALPHABET_SIZE];
        Arrays.fill(positionOfEachByteOnNeedle, ~0L);

        // The `shift` variable will be an unsigned long where only one position will be set 1.
        // The position of 1 is the current offset on the needle.
        long shift = 1L;
        for (final byte b : needle) {
            positionOfEachByteOnNeedle[StrUtils.byteToUnsigned(b)] &= ~shift;
            mask |= shift;
            shift <<= 1;
        }
        mask = ~(mask >> 1);

        // Matching
        long state = ~0L;
        for (var haystackPos = start; haystackPos < end; ++haystackPos) {
            state = (state << 1)
                    | positionOfEachByteOnNeedle[StrUtils.byteToUnsigned(haystack[haystackPos])];
            if (state < mask) { // TODO: Why?
                retl.add(haystackPos - needle.length + 1);
            }
        }
        return retl;
    }
}
