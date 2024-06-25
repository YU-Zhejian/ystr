package com.github.yu_zhejian.ystr.hash;

/**
 * <b>Copy Right</b>
 *
 * <p>General Purpose Hash Function Algorithms Library
 *
 * <p>Free use of the General Purpose Hash Function Algorithms Library is permitted under the
 * guidelines and in accordance with the MIT License.
 *
 * @author Arash Partow <a href="http://www.partow.net>...</a>
 * @see <a href="http://www.partow.net/programming/hashfunctions/index.html">...</a>
 */
public final class PJWHash implements HashInterface {
    private long hash;
    private static final long BitsInUnsignedInt = 4 * 8;
    private static final long ThreeQuarters = (BitsInUnsignedInt * 3) / 4;
    private static final long OneEighth = BitsInUnsignedInt / 8;
    private static final long HighBits = (long) (0xFFFFFFFF) << (BitsInUnsignedInt - OneEighth);
    long test;

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
        hash = (hash << OneEighth) + b;

        if ((test = hash & HighBits) != 0) {
            hash = ((hash ^ (test >> ThreeQuarters)) & (~HighBits));
        }
    }
}
