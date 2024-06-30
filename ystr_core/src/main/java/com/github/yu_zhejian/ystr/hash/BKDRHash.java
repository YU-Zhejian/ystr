package com.github.yu_zhejian.ystr.hash;

/**
 * Brian Kernighan and Dennis Ritchie Hash.
 *
 * <p><b>Description</b>
 *
 * <p>This hash function comes from Brian Kernighan and Dennis Ritchie's book "The C Programming
 * Language". It is a simple hash function using a strange set of possible seeds which all
 * constitute a pattern of 31....31...31 etc, it seems to be very similar to the DJB hash function.
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
public final class BKDRHash implements HashInterface {
    private long hash;
    private final long seed;

    /**
     * Default constructor.
     *
     * @param seed 31 131 1313 13131 131313
     */
    public BKDRHash(long seed) {
        this.seed = seed;
        reset();
    }
    /** Default constructor with 131 as seed. */
    public BKDRHash() {
        this(131);
    }

    @Override
    public long getValue() {
        return hash;
    }

    @Override
    public void reset() {
        hash = 0;
    }

    @Override
    public void update(int b) {
        hash = (hash * seed) + b;
    }
}
