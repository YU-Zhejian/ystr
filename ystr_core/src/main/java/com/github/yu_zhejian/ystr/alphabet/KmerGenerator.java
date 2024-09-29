package com.github.yu_zhejian.ystr.alphabet;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Generating all k-mers over some alphabet.
 *
 * @see AlphabetConstants
 * @see RandomKmerGenerator
 */
public final class KmerGenerator implements Iterator<byte[]> {
    /** As described. */
    private final Alphabet alphabet;
    /** Maximum index inside {@link #alphabet}, which is {@code len(alphabet) - 1}. */
    private final int alphabetMaxIdx;
    /** As described. */
    public final int k;
    /** Current positions over {@link #alphabet}. */
    private final int[] pos;

    /** The final position, which is all zero. */
    private final int[] finalPos;
    /** Indicator whether the iteration has started. */
    private boolean hasStarted;

    /**
     * Default constructor.
     *
     * @param alphabet As described.
     * @param k As described.
     * @throws IllegalArgumentException If {@link #k} is negative or {@link #alphabet} is empty.
     */
    public KmerGenerator(final @NotNull Alphabet alphabet, final int k) {
        if (alphabet.isEmpty()) {
            throw new IllegalArgumentException("alphabet must contain at least one character");
        }
        if (k <= 0) {
            throw new IllegalArgumentException("k must be positive");
        }
        this.alphabet = alphabet;
        this.k = k;
        alphabetMaxIdx = alphabet.length() - 1;
        pos = new int[k];
        finalPos = new int[k];
        Arrays.fill(finalPos, 0);
    }

    private void advPos() {
        for (int i = k - 1; i >= 0; i--) {
            if (pos[i] == alphabetMaxIdx) {
                pos[i] = 0;
            } else {
                pos[i]++;
                return;
            }
        }
    }

    @Override
    public boolean hasNext() {
        return !(hasStarted && Arrays.equals(pos, finalPos));
    }

    @Override
    public byte @NotNull [] next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        hasStarted = true;
        var state = new byte[k];
        for (int i = 0; i < k; i++) {
            state[i] = alphabet.at(pos[i]);
        }
        advPos();
        return state;
    }
}
