package com.github.yu_zhejian.ystr.match;

import com.github.yu_zhejian.ystr.rolling.PolynomialRollingHash;
import com.github.yu_zhejian.ystr.rolling.RollingHashInterface;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import org.jetbrains.annotations.NotNull;

/** Rabin-Karp string matching. */
public final class RabinKarpMatch implements StrMatchInterface {
    /** Rolling hash algorithm. */
    private final RollingHashInterface rollingHasher;

    /**
     * Default constructor.
     *
     * @param rollingHasher As described.
     */
    public RabinKarpMatch(RollingHashInterface rollingHasher) {
        this.rollingHasher = rollingHasher;
    }

    /** Default constructor with default rolling hash implementation. */
    public RabinKarpMatch() {
        this(new PolynomialRollingHash());
    }

    @Override
    public @NotNull IntList applyUnchecked(
            final byte @NotNull [] haystack,
            final byte @NotNull [] needle,
            final int start,
            final int end) {
        // Create initial hash
        var haystackPos = start;

        final var needleLen = needle.length;

        rollingHasher.attach(needle, needleLen, start);
        final var needleHash = rollingHasher.nextLong();
        rollingHasher.detach();

        rollingHasher.attach(haystack, needleLen, start);
        final var retl = new IntArrayList();
        while (haystackPos + needleLen <= end) {
            final var nextHash = rollingHasher.nextLong();
            if (nextHash == needleHash && StrMatchUtils.isMatch(haystack, needle, haystackPos)) {
                retl.add(haystackPos);
            }
            haystackPos++;
        }
        rollingHasher.detach();

        return retl;
    }
}
