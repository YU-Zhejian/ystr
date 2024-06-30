package com.github.yu_zhejian.ystr.hash;

/**
 * ELF Hash.
 *
 * <p><b>Description</b>
 *
 * <p>Similar to the PJW Hash function, but tweaked for 32-bit processors. It is a widley used hash
 * function on UNIX based systems.
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
public final class ELFHash implements HashInterface {
    private long hash;
    private long x;
    /** Default constructor. */
    public ELFHash() {
        reset();
    }

    @Override
    public long getValue() {
        return hash;
    }

    @Override
    public void reset() {
        hash = 0;
        x = 0;
    }

    @Override
    public void update(int b) {
        hash = (hash << 4) + b;

        if ((x = hash & 0xF0000000L) != 0) {
            hash ^= (x >> 24);
        }
        hash &= ~x;
    }
}
