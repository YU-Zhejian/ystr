package org.labw.libinterval.itree;

import it.unimi.dsi.fastutil.objects.ObjectList;

import org.labw.libinterval.IntervalInterface;

/**
 * A general-purposed representation of an interval tree. It supports adding intervals and indexing
 * to ensure added intervals could be queried.
 *
 * @implNote All implementations should keep a constructor that takes no arguments. Recommended to
 *     add another constructor that takes a list of intervals.
 * @param <T> Type of the interval stored inside this tree.
 */
public interface IntervalTreeInterface<T extends IntervalInterface> {
    /**
     * Add a new interval to the current tree.
     *
     * <p>If this tree requires explicit indexing, this operation breaks the existing index and a
     * new index build will be required. Use {@link #index} to do so.
     *
     * <p>If this tree indexes itself while adding elements, no explicit indexing will be required.
     */
    void add(T interval);

    /**
     * (Re-)build the index.
     *
     * <p>If this tree indexes itself while adding elements, this function does nothing.
     */
    void index();

    /** Get number of intervals inside. */
    int getSize();

    /** Query for overlapping intervals. */
    ObjectList<T> overlap(IntervalInterface query);

    /** Get all indexed intervals. */
    ObjectList<T> getIntervals();
}
