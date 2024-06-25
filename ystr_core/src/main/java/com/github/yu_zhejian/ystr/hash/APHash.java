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
public final class APHash implements HashInterface {
    private long hash;
    private int i;

    public APHash() {
        reset();
    }

    @Override
    public long getValue() {
        return hash;
    }

    @Override
    public void reset() {
        hash = 0xAAAAAAAAL;
        i = 0;
    }

    @Override
    public void update(int b) {
        if ((i & 1) == 0) {
            hash ^= ((hash << 7) ^ b * (hash >> 3));
        } else {
            hash ^= (~((hash << 11) + b ^ (hash >> 5)));
        }
        i++;
    }
}
