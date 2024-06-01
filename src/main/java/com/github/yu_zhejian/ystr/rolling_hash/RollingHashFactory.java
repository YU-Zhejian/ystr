package com.github.yu_zhejian.ystr.rolling_hash;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public final class RollingHashFactory {

    @Contract("_, _, _, _, _ -> new")
    public static <T extends RollingHashInterface<?>> @NotNull
            RollingHashInterface<?> newRollingHash(
                    @NotNull Class<T> claz,
                    final byte @NotNull [] string,
                    int k,
                    int start,
                    Object... params) {
        if (claz == NtHash.class) {
            return new NtHash(string, k, start);
        } else if (claz.equals(PolynomialRollingHash.class)) {
            if (params.length == 2
                    && params[0] instanceof Integer
                    && params[1] instanceof Integer) {
                return new PolynomialRollingHash(
                        string, k, start, (Integer) params[0], (Integer) params[1]);
            } else if (params.length == 0) {
                return new PolynomialRollingHash(string, k, start);
            } else {
                throw new IllegalArgumentException("Can't handle PolynomialRollingHash parameters: "
                        + Arrays.toString(params));
            }
        } else {
            throw new IllegalArgumentException("Unknown class type: " + claz.getCanonicalName());
        }
    }
}
