package com.github.yu_zhejian.ystr.utils;

/** mathematics-related utilities that extends {@link Math}. */
public final class MathUtils {
    private MathUtils() {}

    /**
     * Integer power mimicking {@link Math#pow(double, double)}.
     *
     * <p><b>Implementation Limitations</b>
     *
     * <ul>
     *   <li>This method does not perform overflow detection.
     *   <li>This supports non-negative {@code p} and {@code q} only.
     * </ul>
     *
     * @param p The base.
     * @param q The exponent.
     * @return As described.
     * @throws IllegalArgumentException If {@code p} or {@code q} is negative.
     * @see Math#pow(double, double)
     */
    public static int pow(final int p, final int q) {
        if (p < 0 || q < 0) {
            throw new IllegalArgumentException(
                    "p and q should be non-negative. Actual: %d, %d".formatted(p, q));
        }
        int retv = 1;
        for (int i = q; i > 0; i--) {
            retv = retv * p;
        }
        return retv;
    }

    /**
     * {@link Long} version of {@link #pow(int, int)}
     *
     * @param p As described.
     * @param q As described.
     * @return As described.
     * @throws IllegalArgumentException If {@code p} or {@code q} is negative.
     * @see #pow(int, int)
     */
    public static long pow(final long p, final int q) {
        if (p < 0 || q < 0) {
            throw new IllegalArgumentException(
                    "p and q should be non-negative. Actual: %d, %d".formatted(p, q));
        }
        long retv = 1;
        for (int i = q; i > 0; i--) {
            retv = retv * p;
        }
        return retv;
    }
}
