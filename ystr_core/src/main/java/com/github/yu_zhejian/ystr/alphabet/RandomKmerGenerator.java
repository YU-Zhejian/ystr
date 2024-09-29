package com.github.yu_zhejian.ystr.alphabet;

import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.util.Iterator;
import java.util.Random;

/**
 * A random k-mer generator that generates infinite number of K-mers. Duplications may occur.
 *
 * @see KmerGenerator
 */
public final class RandomKmerGenerator implements Iterator<byte[]> {

    /** As described. */
    private final Alphabet alphabet;
    /** As described. */
    public final int k;

    /** The random generator * */
    private final Random rand = new SecureRandom();

    /**
     * The default constructor
     *
     * @param alphabet The alphabet. See {@link AlphabetConstants} for a series of pre-defined
     *     alphabets.
     * @param k K-mer size.
     */
    public RandomKmerGenerator(final @NotNull Alphabet alphabet, final int k) {
        if (alphabet.length() == 0) {
            throw new IllegalArgumentException("alphabet must contain at least one character");
        }
        if (k <= 0) {
            throw new IllegalArgumentException("k must be positive");
        }
        this.alphabet = alphabet;
        this.k = k;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @SuppressWarnings("java:S2272")
    @Override
    public byte @NotNull [] next() {
        var retb = new byte[k];
        for (int i = 0; i < k; i++) {
            retb[i] = alphabet.at(rand.nextInt(alphabet.length()));
        }
        return retb;
    }
}
