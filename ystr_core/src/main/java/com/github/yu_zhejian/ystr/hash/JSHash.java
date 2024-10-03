package com.github.yu_zhejian.ystr.hash;

/**
 * Justin Sobel Hash.
 *
 * <p><b>Description</b>
 *
 * <p>A bitwise hash function written by Justin Sobel.
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
public final class JSHash implements HashInterface {
    private long hash;

    /** Default constructor. */
    public JSHash() {
        reset();
    }

    @Override
    public long getValue() {
        return hash;
    }

    @Override
    public void reset() {
        hash = 1315423911;
    }

    @Override
    public void update(int b) {
        hash ^= ((hash << 5) + b + (hash >> 2));
    }
}
