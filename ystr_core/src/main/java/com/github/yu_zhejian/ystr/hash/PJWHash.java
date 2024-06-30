package com.github.yu_zhejian.ystr.hash;

/**
 * Peter J Weinberger Hash.
 *
 * <p><b>Description</b>
 *
 * <p>This hash algorithm is based on work by Peter J. Weinberger of <a
 * href="https://www.rentec.com/">Renaissance Technologies</a>. The book Compilers (Principles,
 * Techniques and Tools) by Aho, Sethi and Ulman, recommends the use of hash functions that employ
 * the hashing methodology found in this particular algorithm.
 *
 * <p><b>Copyright</b>
 *
 * <p>General Purpose Hash Function Algorithms Library
 *
 * <p>Free use of the General Purpose Hash Function Algorithms Library is permitted under the
 * guidelines and in accordance with the MIT License.
 *
 * @author Arash Partow <a href="http://www.partow.net">...</a>
 * @see <a href="http://www.partow.net/programming/hashfunctions/index.html">...</a>
 */
public final class PJWHash implements HashInterface {
    private long hash;
    private static final long BITS_IN_UNSIGNED_INT = 4 * 8L;
    private static final long THREE_QUARTERS = (BITS_IN_UNSIGNED_INT * 3) / 4;
    private static final long ONE_EIGHTH = BITS_IN_UNSIGNED_INT / 8;
    private static final long HIGH_BITS =
            (long) (0xFFFFFFFF) << (BITS_IN_UNSIGNED_INT - ONE_EIGHTH);
    long test;
    /** Default constructor. */
    public PJWHash() {
        reset();
    }

    @Override
    public long getValue() {
        return hash;
    }

    @Override
    public void reset() {
        hash = 0;
        test = 0;
    }

    @Override
    public void update(int b) {
        hash = (hash << ONE_EIGHTH) + b;

        if ((test = hash & HIGH_BITS) != 0) {
            hash = ((hash ^ (test >> THREE_QUARTERS)) & (~HIGH_BITS));
        }
    }
}
