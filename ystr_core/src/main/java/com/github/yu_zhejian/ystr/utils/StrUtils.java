package com.github.yu_zhejian.ystr.utils;

/** Various utility functions concerning strings to support other classes. */
public final class StrUtils {
    /** Defunct constructor */
    private StrUtils() {}

    /**
     * Convert a signed byte to unsigned int.
     *
     * @param b As described.
     * @return As described.
     */
    public static int byteToUnsigned(final byte b) {
        return b & 0xFF;
    }

    /**
     * Integer version of {@link #byteToUnsigned(byte)}.
     *
     * @param b As described.
     * @return As described.
     */
    public static int byteToUnsigned(final int b) {
        return b & 0xFF;
    }

    /**
     * Ensure start and end are valid for some open-close interval.
     *
     * @param start As described.
     * @param end As described.
     */
    public static void ensureStartEndValid(final int start, final int end) {
        if (start > end) {
            throw new IllegalArgumentException(
                    "start must be less than end. Actual: %d vs %d".formatted(start, end));
        }
        if (start < 0) {
            throw new IllegalArgumentException(
                    "start must be greater than or equal to zero. Actual: %d".formatted(start));
        }
    }

    /**
     * Ensure start and end is valid for some open-close interval within a string.
     *
     * @param start As described.
     * @param end As described.
     * @param strLen As described.
     */
    public static void ensureStartEndValid(final int start, final int end, final int strLen) {
        ensureStartEndValid(start, end);
        if (end > strLen) {
            throw new IllegalArgumentException(
                    "end must be less than strLen. Actual: %d vs %d".formatted(end, strLen));
        }
    }

    /**
     * Ensure start and length are valid for some open-close interval within a string.
     *
     * @param start As described.
     * @param numBytesToRead As described.
     * @param strLen As described.
     */
    public static void ensureStartLengthValid(
            final int start, final int numBytesToRead, final int strLen) {
        ensureStartEndValid(start, start + numBytesToRead, strLen);
    }
}
