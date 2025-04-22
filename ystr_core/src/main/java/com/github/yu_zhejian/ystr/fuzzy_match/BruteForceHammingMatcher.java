package com.github.yu_zhejian.ystr.fuzzy_match;

import com.github.yu_zhejian.ystr.distance.HammingDistance;
import com.github.yu_zhejian.ystr.match.StrMatchInterface;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import org.jetbrains.annotations.NotNull;

public final class BruteForceHammingMatcher implements StrMatchInterface {
    private final int maxDistance;
    private final HammingDistance hammingDistance = new HammingDistance();

    public BruteForceHammingMatcher(final int maxDistance) {
        if (maxDistance <= 0) {
            throw new IllegalArgumentException(
                    "maxDistance must be greater than 0. Actual: %d".formatted(maxDistance));
        }
        this.maxDistance = maxDistance;
    }

    @Override
    public @NotNull IntList applyUnchecked(
            byte @NotNull [] haystack, byte @NotNull [] needle, int start, int end, int limitTo) {
        var haystackPos = start;
        int numMatch = 0;
        final var retl = new IntArrayList();
        while (haystackPos + needle.length < end) {
            if (hammingDistance.applyUnchecked(
                            haystack,
                            needle,
                            haystackPos,
                            haystackPos + needle.length,
                            0,
                            needle.length)
                    <= maxDistance) {
                retl.add(haystackPos);
                numMatch++;
                if (numMatch == limitTo) {
                    return retl;
                }
            }
            haystackPos++;
        }
        return retl;
    }
}
