package com.github.yu_zhejian.ystr.rolling_hash;

import io.vavr.Tuple;
import io.vavr.Tuple2;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * A ring-buffer of minimizers.
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
 * Notice, if hash collision occurs, will use the one with smaller position.
 */
final class MinimizerRingBuffer {
    public static final long MAX_ULONG = 0xffffffffffffffffL;
    private final int capacity;
    private final long[] contents;
    private final int[] pos;
    private int curPos;

    /**
     * Default constructor.
     *
     * @param capacity As described.
     * @param start See {@link MinimizerCalculator}.
     */
    public MinimizerRingBuffer(int capacity, int start) {
        this.capacity = capacity;
        contents = new long[capacity];
        pos = new int[capacity];
        Arrays.fill(contents, MAX_ULONG);
        Arrays.fill(pos, start - 1);
        curPos = 0;
    }

    /**
     * As described.
     *
     * @param hash As described.
     */
    public void add(long hash) {
        contents[curPos] = hash;
        pos[curPos] = pos[(curPos + capacity - 1) % capacity] + 1;
        curPos++;
        curPos %= capacity;
    }

    /**
     * As described.
     *
     * <p>Note, the current implementation is brute-force. There should have been better.
     *
     * @return As described.
     */
    public @NotNull Tuple2<Long, Integer> getCurrentMinimizer() {
        long minimizer = MAX_ULONG;
        int minPos = Integer.MAX_VALUE;
        for (int i = 0; i < capacity; i++) {
            var cmp = Long.compareUnsigned(contents[i], minimizer);
            if (cmp < 0 || (cmp == 0 && pos[i] < minPos)) {
                minimizer = contents[i];
                minPos = pos[i];
            }
        }
        return Tuple.of(minimizer, minPos);
    }

    public @NotNull String toString() {
        var sb = new StringBuilder();
        for (int i = 0; i < capacity; i++) {
            if (i == curPos) {
                sb.append('(');
            }
            sb.append(contents[i] == MAX_ULONG ? "M" : Long.toHexString(contents[i]));
            if (i == curPos) {
                sb.append(')');
            }
            sb.append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        return "MinimizerRingBuffer[" + sb + "]";
    }
}

/**
 * Calculate minimizers and their offsets. That is, the minimum of every {@link #windowSize} hashes.
 *
 * <p><b>References</b>
 *
 * <ul>
 *   <li>Roberts, W. Hayes, B. R. Hunt, S. M. Mount, and J. A. Yorke, “Reducing storage requirements
 *       for biological sequence comparison,” Bioinformatics, vol. 20, no. 18, pp. 3363–3369, Dec.
 *       2004 <a href="https://doi.org/10.1093/bioinformatics/bth408">DOI</a>
 * </ul>
 */
public final class MinimizerCalculator implements Iterator<Tuple2<Long, Integer>> {
    /** Result of a rolling hashing function. */
    private final Iterator<Long> hashIterable;
    /** Calculate minimizer per {@code windowSize} hashes. */
    private final int windowSize;

    /**
     * Whether to calculate endHash as-is specified in the article. Calculating endHash enables
     * "overlapping matches".
     */
    private final boolean endHash;

    /** End hash as-is described at the paper. */
    private final LinkedList<Tuple2<Long, Integer>> leadingEndHash;

    /** End hash as-is described at the paper. */
    private final LinkedList<Tuple2<Long, Integer>> terminatingEndHash;

    private final MinimizerRingBuffer minimizerRingBuffer;

    /**
     * Helper variable that indicates the current minimizer was started from where. Inclusive
     * 0-based regardless of {@code start}.
     */
    private int hashFrom;
    /**
     * Helper variable that indicates the current minimizer was ended at where. Exclusive 0-based
     * regardless of {@code start}.
     */
    private int hashTo;
    /** Whether {@link #terminatingEndHash} were calculated. */
    private boolean terminateHashIsCalculated;

    /**
     * Default constructor.
     *
     * @param hashIterable As described.
     * @param windowSize As described.
     * @param start Start offset of the {@link #hashIterable}.
     * @param endHash As described.
     */
    public MinimizerCalculator(
            Iterator<Long> hashIterable, int windowSize, int start, boolean endHash) {
        this.hashIterable = hashIterable;
        this.windowSize = windowSize;
        this.endHash = endHash;

        terminateHashIsCalculated = false;
        hashFrom = 0;
        hashTo = 1;
        minimizerRingBuffer = new MinimizerRingBuffer(windowSize, start);

        // Populating the rolling buffer
        leadingEndHash = new LinkedList<>();
        terminatingEndHash = new LinkedList<>();

        // System.out.println("---pre---");
        while (hashTo < windowSize && hashIterable.hasNext()) {
            // System.out.println(hashFrom + " " + hashTo);
            var currentHash = hashIterable.next();
            minimizerRingBuffer.add(currentHash);
            if (endHash) {
                leadingEndHash.add(minimizerRingBuffer.getCurrentMinimizer());
            }
            hashTo++;
        }

        if (!hashIterable.hasNext()) {
            // Remove the first hash from the ring buffer, otherwise there will be duplicates.
            hashFrom++;
            minimizerRingBuffer.add(MinimizerRingBuffer.MAX_ULONG);
            calculateTerminatingEndHash();
        }
        // System.out.println("---begin---");
    }

    @Override
    public boolean hasNext() {
        if (!terminateHashIsCalculated) {
            return true;
        }
        return !terminatingEndHash.isEmpty();
    }

    private void calculateTerminatingEndHash() {
        // System.out.println("---end---");
        terminateHashIsCalculated = true;
        // Since hashTo is always increasing, it should now exceed the boundary.
        hashTo--;
        while (hashFrom < hashTo) {
            // System.out.println(hashFrom + " " + hashTo);
            minimizerRingBuffer.add(MinimizerRingBuffer.MAX_ULONG);
            if (endHash) {
                terminatingEndHash.add(minimizerRingBuffer.getCurrentMinimizer());
            }
            // System.out.println(minimizerRingBuffer);
            // System.out.println(minimizerRingBuffer.getCurrentMinimizer());
            hashFrom++;
        }
    }

    @Override
    public @Nullable Tuple2<Long, Integer> next() {
        // Exhaustion of leading end hash.
        if (!leadingEndHash.isEmpty()) {
            return leadingEndHash.pollFirst();
        }

        // Normal cases.
        if (hashIterable.hasNext()) {
            // System.out.println(hashFrom + " " + hashTo);
            minimizerRingBuffer.add(hashIterable.next());
            var retv = minimizerRingBuffer.getCurrentMinimizer();
            hashTo++;
            if (hashTo != windowSize) {
                hashFrom++;
            }
            if (!hashIterable.hasNext()) {
                calculateTerminatingEndHash();
            }
            return retv;
        }

        // Exhaustion of terminating end hash.
        if (!terminatingEndHash.isEmpty()) {
            return terminatingEndHash.pollFirst();
        }
        throw new NoSuchElementException();
    }
}
