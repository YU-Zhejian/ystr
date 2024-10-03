package com.github.yu_zhejian.ystr.utils;

import it.unimi.dsi.fastutil.ints.IntIntImmutablePair;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import it.unimi.dsi.fastutil.longs.LongLongImmutablePair;
import it.unimi.dsi.fastutil.longs.LongLongPair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;

import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;

/** Utilities for generating random intervals. */
public final class RngUtils {
    /** Defunct Constructor * */
    private RngUtils() {}

    /**
     * Generating random int intervals over a defined coordinate
     *
     * @param numCoordinates As described.
     * @param start As described.
     * @param end As described.
     * @return As described.
     * @throws IllegalArgumentException See {@link StrUtils#ensureStartEndValid(int, int)}.
     */
    public static @NotNull ObjectList<IntIntPair> generateRandomCoordinates(
            final int numCoordinates, final int start, final int end) {
        StrUtils.ensureStartEndValid(start, end);
        var coordinates = new ObjectArrayList<IntIntPair>();
        var rng = new SecureRandom();
        for (var i = 0; i < numCoordinates; i++) {
            var selectedTerm1 = rng.nextInt(start, end);
            var selectedTerm2 = rng.nextInt(start, end);
            coordinates.add(IntIntImmutablePair.of(
                    Math.min(selectedTerm1, selectedTerm2),
                    Math.max(selectedTerm1, selectedTerm2)));
        }
        return coordinates;
    }

    /**
     * Generating random int intervals over a defined coordinate, {@link Long} variant.
     *
     * @param numCoordinates As described.
     * @param start As described.
     * @param end As described.
     * @return As described.
     * @throws IllegalArgumentException See {@link StrUtils#ensureStartEndValid(long, long)}.
     * @see #generateRandomCoordinates(int, int, int)
     */
    public static @NotNull ObjectList<LongLongPair> generateRandomCoordinates(
            final int numCoordinates, final long start, final long end) {
        StrUtils.ensureStartEndValid(start, end);
        var coordinates = new ObjectArrayList<LongLongPair>();
        var rng = new SecureRandom();
        for (var i = 0; i < numCoordinates; i++) {
            var selectedTerm1 = rng.nextLong(start, end);
            var selectedTerm2 = rng.nextLong(start, end);
            coordinates.add(LongLongImmutablePair.of(
                    Math.min(selectedTerm1, selectedTerm2),
                    Math.max(selectedTerm1, selectedTerm2)));
        }
        return coordinates;
    }
}
