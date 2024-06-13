package com.github.yu_zhejian.ystr.rolling;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/** TODO: This is sh*t. Have it refactored. */
public final class RollingHashFactory {

    /**
     * TODO
     *
     * @param claz TODO
     * @param string TODO
     * @param k TODO
     * @param skipFirst TODO
     * @param params TODO
     * @param <T> TODO
     * @return TODO
     */
    @Contract("_, _, _, _, _ -> new")
    public static <T extends RollingHashInterface> @NotNull RollingHashInterface newRollingHash(
            final @NotNull Class<T> claz,
            final byte @NotNull [] string,
            final int k,
            final int skipFirst,
            final Object... params) {

        if (claz == NtHash.class) {
            return new NtHash(string, k, skipFirst);
        } else if (claz == PrecomputedNtHash.class) {
            return new PrecomputedNtHash(string, k, skipFirst);
        } else if (claz.equals(PolynomialRollingHash.class)) {
            if (params.length == 2
                    && params[0] instanceof Long lp0
                    && params[1] instanceof Long lp1) {
                return new PolynomialRollingHash(string, k, skipFirst, lp0, lp1);
            } else if (params.length == 0) {
                return new PolynomialRollingHash(string, k, skipFirst);
            } else {
                throw new IllegalArgumentException("Can't handle PolynomialRollingHash parameters: "
                        + Arrays.toString(params));
            }
        } else {
            throw new IllegalArgumentException("Unknown class type: " + claz.getCanonicalName());
        }
    }
}
