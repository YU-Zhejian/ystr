package com.github.yu_zhejian.ystr.utils;

import it.unimi.dsi.fastutil.ints.IntArrayList;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * <b>References</b>
 *
 * <ul>
 *   <li>Roberts, W. Hayes, B. R. Hunt, S. M. Mount, and J. A. Yorke, "Reducing storage requirements
 *       for biological sequence comparison," Bioinformatics, vol. 20, no. 18, pp. 3363-3369, Dec.
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
            minimizerRingBuffer.add(hashes.get(hashTo), hashTo);
            if (endHash) {
                retl.add(minimizerRingBuffer.getCurrentMinimizer());
            }
            // System.out.printf("%d %d -- %s%n", hashFrom, hashTo, minimizerRingBuffer);
            hashTo++;
        }
        while (hashTo < hLen) {
            minimizerRingBuffer.add(hashes.get(hashTo), hashTo);
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
