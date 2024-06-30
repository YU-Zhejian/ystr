package com.github.yu_zhejian.ystr.match;

import com.github.yu_zhejian.ystr.rolling.PolynomialRollingHash;
import com.github.yu_zhejian.ystr.rolling.RollingHashInterface;

import io.vavr.Function3;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import org.jetbrains.annotations.NotNull;

/** Rabin-Karp string matching. */
public class RabinKarpMatch implements StrMatchInterface {
    /**
     * Rolling hash algorithm supplier.
     *
     * @see PolynomialRollingHash#supply(long, long)
     * @see RollingHashInterface
     */
    private final Function3<byte[], Integer, Integer, RollingHashInterface> supplier;

    /**
     * Default constructor.
     *
     * @param supplier As described.
     */
    public RabinKarpMatch(Function3<byte[], Integer, Integer, RollingHashInterface> supplier) {
        this.supplier = supplier;
    }

    /** Default constructor with default rolling hash implementation. */
    public RabinKarpMatch() {
        this(PolynomialRollingHash::new);
    }

    @Override
    public IntList applyUnchecked(
            final byte @NotNull [] haystack,
            final byte @NotNull [] needle,
            final int start,
            final int end) {
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
