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
public final class RSHash implements HashInterface {
    private int hashA;
    private int hashB;
    private int hash;

    public RSHash() {
        reset();
    }

    @Override
    public long getValue() {
        return hash;
    }

    @Override
    public void reset() {
        hashB = 378551;
        hashA = 63689;
        hash = 0;
    }

    @Override
    public void update(int b) {
        hash = hash * hashA + b;
        hashA *= hashB;
    }
}
