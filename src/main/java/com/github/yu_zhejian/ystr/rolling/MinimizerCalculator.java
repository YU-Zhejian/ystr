package com.github.yu_zhejian.ystr.rolling;

import it.unimi.dsi.fastutil.ints.IntArrayList;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

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
 * Notice, if hash collision occurs, will use the one with smaller position.
 */
final class MinimizerRingBuffer {
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
     * As described.
     *
     * @param hash As described.
     */
    public void add(final long hash) {
        contents[curPos] = hash;
        pos[curPos] = pos[(curPos + capacity - 1) % capacity] + 1;
        curPos++;
        curPos %= capacity;
    }

    /**
     * The current minimizer position.
     *
     * <p>Note, the current implementation is brute-force. There should have been better.
     *
     * @return As described.
     */
    public int getCurrentMinimizer() {
        long minimizer = MAX_ULONG;
        int minPos = Integer.MAX_VALUE;
        for (int i = 0; i < capacity; i++) {
            if ((contents[i] == minimizer && pos[i] < minPos)
                    || (contents[i] + Long.MIN_VALUE < minimizer + Long.MIN_VALUE)) {
                minimizer = contents[i];
                minPos = pos[i];
            }
        }
        return minPos;
    }

    @Override
    public @NotNull String toString() {
        final var sb = new StringBuilder();
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
        return "MinimizerRingBuffer[%s] (%d)".formatted(sb, getCurrentMinimizer());
    }
}

/**
 * <b>References</b>
 *
 * <ul>
 *   <li>Roberts, W. Hayes, B. R. Hunt, S. M. Mount, and J. A. Yorke, “Reducing storage requirements
 *       for biological sequence comparison,” Bioinformatics, vol. 20, no. 18, pp. 3363–3369, Dec.
 *       2004 <a href="https://doi.org/10.1093/bioinformatics/bth408">DOI</a>
 * </ul>
 */
public final class MinimizerCalculator {
    private MinimizerCalculator() {}
    /**
     * Calculate minimizers offsets.
     *
     * @param hashes Result of a rolling hashing function.
     * @param windowSize Calculate minimizer per {@code windowSize} hashes.
     * @param endHash Whether to calculate endHash as-is specified in the article. Calculating
     *     endHash enables "overlapping matches".
     * @return As described.
     */
    public static @NotNull List<Integer> getMinimizerPositions(
            final @NotNull List<Long> hashes, final int windowSize, final boolean endHash) {
        var hashFrom = -1;
        var hashTo = 0;
        var hLen = hashes.size();
        var retl = new IntArrayList(hLen);
        if (!endHash && windowSize > hLen) {
            return retl;
        }
        var finalWindowSize = Integer.min(windowSize, hLen);
        var minimizerRingBuffer = new MinimizerRingBuffer(finalWindowSize);

        // Populating the rolling buffer
        while (hashTo < finalWindowSize - 1 && hashTo < hLen) {
            minimizerRingBuffer.add(hashes.get(hashTo));
            if (endHash) {
                retl.add(minimizerRingBuffer.getCurrentMinimizer());
            }
            // System.out.printf("%d %d -- %s%n", hashFrom, hashTo, minimizerRingBuffer);
            hashTo++;
        }
        while (hashTo < hLen) {
            minimizerRingBuffer.add(hashes.get(hashTo));
            retl.add(minimizerRingBuffer.getCurrentMinimizer());
            // System.out.printf("%d %d -- %s%n", hashFrom, hashTo, minimizerRingBuffer);
            hashTo++;
            hashFrom++;
        }
        hashFrom++;
        while (hashFrom < hashTo) {
            minimizerRingBuffer.add(MinimizerRingBuffer.MAX_ULONG);
            if (endHash) {
                retl.add(minimizerRingBuffer.getCurrentMinimizer());
            }
            // System.out.printf("%d %d -- %s%n", hashFrom, hashTo, minimizerRingBuffer);
            hashFrom++;
        }
        return retl;
    }
}
