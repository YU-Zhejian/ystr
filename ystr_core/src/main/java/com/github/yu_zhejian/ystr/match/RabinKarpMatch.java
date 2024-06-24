package com.github.yu_zhejian.ystr.match;

import com.github.yu_zhejian.ystr.rolling.PolynomialRollingHash;
import com.github.yu_zhejian.ystr.rolling.RollingHashInterface;

import io.vavr.Function3;

import it.unimi.dsi.fastutil.ints.IntArrayList;

import org.jetbrains.annotations.NotNull;

public class RabinKarpMatch implements StrMatchInterface {
    private final Function3<byte[], Integer, Integer, RollingHashInterface> supplier;

    public RabinKarpMatch(Function3<byte[], Integer, Integer, RollingHashInterface> supplier) {
        this.supplier = supplier;
    }

    public RabinKarpMatch() {
        this(PolynomialRollingHash::new);
    }

    @Override
    public IntArrayList apply(
            final byte @NotNull [] haystack,
            final byte @NotNull [] needle,
            final int start,
            final int end) {
        StrMatchUtils.ensureParametersValid(haystack, needle, start, end);
        if (haystack.length == 0 || needle.length == 0) {
            return new IntArrayList();
        }
        // Create initial hash
        var haystackPos = start;
        final var needleLen = needle.length;
        final var rhHaystack = supplier.apply(haystack, needleLen, start);
        final var needleHash = supplier.apply(needle, needleLen, start).nextLong();

        final var retl = new IntArrayList();
        while (haystackPos + needleLen <= end) {
            final var nextHash = rhHaystack.nextLong();
            if (nextHash == needleHash && StrMatchUtils.isMatch(haystack, needle, haystackPos)) {
                retl.add(haystackPos);
            }
            haystackPos++;
        }
        return retl;
    }
}
