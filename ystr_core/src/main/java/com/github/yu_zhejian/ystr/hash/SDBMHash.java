package com.github.yu_zhejian.ystr.hash;

/**
 * SDBM Hash.
 *
 * <p><b>Description</b>
 *
 * <p>This is the algorithm of choice which is used in the open source SDBM project. The hash
 * function seems to have a good over-all distribution for many different data sets. It seems to
 * work well in situations where there is a high variance in the MSBs of the elements in a data set.
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
public final class SDBMHash implements HashInterface {
    private long hash;

    /** Default constructor. */
    public SDBMHash() {
        reset();
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
        hash = b + (hash << 6) + (hash << 16) - hash;
    }
}
