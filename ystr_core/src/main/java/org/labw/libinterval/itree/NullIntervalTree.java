package org.labw.libinterval.itree;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.labw.libinterval.IntervalInterface;

/** A placeholder that throws away anything that was added. Use this as a placeholder. */
public final class NullIntervalTree<T extends IntervalInterface>
        implements IntervalTreeInterface<T> {
    public NullIntervalTree() {
        // Does nothing!
    }

    @Override
    public void add(T interval) {
        // which does nothing
    }

    @Override
    public void index() {
        // which does nothing
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Contract(value = "_ -> new", pure = true)
    @Override
    public @NotNull ObjectList<T> overlap(IntervalInterface query) {
        return new ObjectArrayList<>() {};
    }

    @Contract(value = " -> new", pure = true)
    @Override
    public @NotNull ObjectList<T> getIntervals() {
        return new ObjectArrayList<>();
    }
}
