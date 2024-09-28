package com.github.yu_zhejian.ystr.minimizer;

import it.unimi.dsi.fastutil.longs.LongArrayPriorityQueue;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * A ring-buffer of minimizers and its position.
 *
 * <p><b>Implementation</b>
 *
 * <ol>
 *   <li>The ring buffer will be initially filled with {@link #MAX_ULONG}.
 *   <li>If a value were provided, the value stored at {@link #curPos} will be set to the new value,
 *       with {@link #curPos} moving to the next place.
 *   <li>{@link #curPos} will be moved to 0 when it reaches {@link #capacity}.
 * </ol>
 *
 * <p>Notice, if hash collision occurs, will use the one with smaller position.
 *
 * @see LongArrayPriorityQueue
 */
public final class MinimizerRingBuffer {
    /** Placeholder for leat possible minimizer. */
    public static final long MAX_ULONG = 0xffffffffffffffffL;

    /** Length of {@link #contents} and {@link #pos}. */
    private final int capacity;
    /** The ring buffer itself. */
    private final long[] contents;
    /** Real position of each element inside {@link #contents}. */
    private final int[] pos;
    /** Where we are inside the buffer. */
    private int curPos;
    /** Current minimizer value. */
    private long curMin;
    /** Current minimizer position. */
    private int curMinPos;

    /**
     * Default constructor.
     *
     * @param capacity As described.
     */
    public MinimizerRingBuffer(final int capacity) {
        this.capacity = capacity;
        contents = new long[capacity];
        pos = new int[capacity];
        Arrays.fill(contents, MAX_ULONG);
        Arrays.fill(pos, -1);
        curPos = 0;
    }

    /**
     * {@link #add(long, int)} with automatic calculation of positions.
     *
     * <p>Note, This method is extremely slow and should not be used in production.
     *
     * @param hash As described.
     */
    public void add(final long hash) {
        add(hash, pos[(curPos + capacity - 1) % capacity] + 1);
    }

    /**
     * Add a new hash while updating {@link #curMin}.
     *
     * @param hash As described.
     * @param newPos As described.
     */
    public void add(final long hash, final int newPos) {
        contents[curPos] = hash;
        pos[curPos] = newPos;
        if (hash + Long.MIN_VALUE < curMin + Long.MIN_VALUE) {
            curMin = hash;
            curMinPos = newPos;
        } else if (curMinPos + capacity == newPos) {
            // The current minimum is leaving!
            resetMinAndPos();
        }
        curPos++;
        // Reduce time used on mod operation.
        if (curPos == capacity) {
            curPos = 0;
        }
    }

    /** Force reset of {@link #curMin} and {@link #curMinPos}. */
    public void resetMinAndPos() {
        curMin = contents[0];
        curMinPos = pos[0];
        for (int i = 1; i < capacity; i++) {
            if ((contents[i] == curMin && pos[i] < curMinPos)
                    || (contents[i] + Long.MIN_VALUE < curMin + Long.MIN_VALUE)) {
                curMin = contents[i];
                curMinPos = pos[i];
            }
        }
    }

    /**
     * The current minimizer position.
     *
     * @return As described.
     */
    public int getCurrentMinimizer() {
        return curMinPos;
    }

    @Override
    public @NotNull String toString() {
        final var sb = new StringBuilder();
        for (int i = 0; i < capacity; i++) {
            if (i == curPos) {
                sb.append('(');
            }
            sb.append(contents[i] == MAX_ULONG ? "M" : Long.toHexString(contents[i]))
                    .append('/')
                    .append(pos[i]);
            if (i == curPos) {
                sb.append(')');
            }
            sb.append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        return "MinimizerRingBuffer[%s] (%d)".formatted(sb, curMinPos);
    }
}
