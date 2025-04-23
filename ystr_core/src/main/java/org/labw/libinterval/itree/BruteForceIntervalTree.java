package org.labw.libinterval.itree;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.labw.libinterval.IntervalAlgorithms;
import org.labw.libinterval.IntervalInterface;

/**
 * A simple implementation of old good brute-force that compares all intervals to the querying
 * interval using {@link IntervalAlgorithms#overlaps}.
 *
 * @param <T> As described.
 */
public final class BruteForceIntervalTree<T extends IntervalInterface>
        implements IntervalTreeInterface<T> {
    private final ObjectList<T> intervals;

    public BruteForceIntervalTree() {
        this.intervals = new ObjectArrayList<>();
    }

    @Override
    public void add(T interval) {
        intervals.add(interval);
    }

    @Override
    public void index() {
        // Does nothing!
    }

    @Override
    public int getSize() {
        return intervals.size();
    }

    @Override
    public @NotNull ObjectList<T> overlap(IntervalInterface query) {
        var retl = new ObjectArrayList<T>();
        for (T interval : intervals) {
            if (IntervalAlgorithms.overlaps(query, interval)) {
                retl.add(interval);
            }
        }
        return retl;
    }

    @Contract(value = " -> new", pure = true)
    @Override
    public @NotNull ObjectList<T> getIntervals() {
        return new ObjectArrayList<>(intervals);
    }
}
