package com.github.yu_zhejian.ystr.hash;

/**
 * Robert Sedgwick Hash.
 *
 * <p><b>Description</b>
 *
 * <p>A simple hash function from Robert Sedgwick's <i>Algorithms in C</i> book. I've added some
 * simple optimizations to the algorithm in order to speed up its hashing process.
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
public final class RSHash implements HashInterface {
    private int hashA;
    private int hashB;
    private int hash;

    /** Default constructor. */
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
