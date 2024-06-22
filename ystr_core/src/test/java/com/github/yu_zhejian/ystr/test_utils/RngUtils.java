package com.github.yu_zhejian.ystr.test_utils;

import io.vavr.Tuple;
import io.vavr.Tuple2;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.util.List;

public class RngUtils {

    public static @NotNull List<Tuple2<Integer, Integer>> generateRandomCoordinates(
            int numCoordinates, int start, int end) {
        var coordinates = new ObjectArrayList<Tuple2<Integer, Integer>>();
        var rng = new SecureRandom();
        for (var i = 0; i < numCoordinates; i++) {
            var selectedTerm1 = rng.nextInt(start, end);
            var selectedTerm2 = rng.nextInt(start, end);
            coordinates.add(Tuple.of(
                    Math.min(selectedTerm1, selectedTerm2),
                    Math.max(selectedTerm1, selectedTerm2)));
        }
        return coordinates;
    }
}
