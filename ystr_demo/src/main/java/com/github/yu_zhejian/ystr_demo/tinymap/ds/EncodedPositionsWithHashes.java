package com.github.yu_zhejian.ystr_demo.tinymap.ds;

import com.github.yu_zhejian.ystr.hash.CRC32Hash;
import com.github.yu_zhejian.ystr.io.LongEncoder;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * The encoded positions with hashes. Used <b>WITHIN ONE CONTIG</b> only.
 * <p>
 *               TODO: Have it indexed somehow.
 *
 * @param encodedPositions Positive positions when forward hash is smaller than reverse hash.
 *                         Note that the positions should be related to the contig instead of the chunk.
 * @param hashes           Hashes for each k-mer.
 */
public record EncodedPositionsWithHashes(
    LongList encodedPositions, LongList hashes
) {
    public static byte[] MAGIC = "TM-EPH".getBytes(StandardCharsets.US_ASCII);

    /**
     * Return hash-to-position mapping.
     *
     * @return As described.
     */
    public @NotNull Long2ObjectMap<IndexedPositions> toIndexedPositionsMap(final long contigID) {
        final var encodedHashOffsetDict = new Long2ObjectOpenHashMap<IndexedPositions>();
        for (var i = 0; i < hashes().size(); i++) {
            final long hashValue = encodedPositions().getLong(i);
            encodedHashOffsetDict.computeIfAbsent(hashValue, k -> new IndexedPositions(hashValue));
            encodedHashOffsetDict
                .get(hashValue)
                .add(contigID, encodedPositions().getLong(i));
        }
        return encodedHashOffsetDict;
    }
}
