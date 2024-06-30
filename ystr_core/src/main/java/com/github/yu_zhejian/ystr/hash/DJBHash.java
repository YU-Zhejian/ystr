package com.github.yu_zhejian.ystr.hash;

/**
 * Daniel J Bernstein Hash.
 *
 * <p><b>Description</b>
 *
 * <p>An algorithm produced by Professor Daniel J. Bernstein and shown first to the world on the
 * usenet newsgroup comp.lang.c. It is one of the most efficient hash functions ever published.
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
public final class DJBHash implements HashInterface {
    private long hash;
    /** Default constructor. */
    public DJBHash() {
        reset();
    }

    @Override
    public long getValue() {
        return hash;
    }

    @Override
    public void reset() {
        hash = 5381;
    }

    @Override
    public void update(int b) {
        hash = ((hash << 5) + hash) + b;
    }
}
