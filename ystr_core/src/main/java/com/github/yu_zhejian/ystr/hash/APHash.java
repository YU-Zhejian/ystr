package com.github.yu_zhejian.ystr.hash;

/**
 * Arash Partow Hash.
 *
 * <p><b>Description</b>
 *
 * <p>An algorithm produced by me Arash Partow. I took ideas from all of the above hash functions
 * making a hybrid rotative and additive hash function algorithm. There isn't any real mathematical
 * analysis explaining why one should use this hash function instead of the others described above
 * other than the fact that I tired to resemble the design as close as possible to a simple LFSR. An
 * empirical result which demonstrated the distributive abilities of the hash algorithm was obtained
 * using a hash-table with 100003 buckets, hashing <a href="http://www.gutenberg.org/ebooks/673">The
 * Project Gutenberg Etext of Webster's Unabridged Dictionary</a>, the longest encountered chain
 * length was 7, the average chain length was 2, the number of empty buckets was 4579.
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
public final class APHash implements HashInterface {
    private long hash;
    private int i;

    /** Default constructor. */
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
