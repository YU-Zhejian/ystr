package com.github.yu_zhejian.ystr_demo.tinymap.ds;

import com.github.yu_zhejian.ystr.hash.CRC32Hash;
import com.github.yu_zhejian.ystr.io.BaseRandomBinaryFileParser;
import com.github.yu_zhejian.ystr.io.LongEncoder;
import it.unimi.dsi.fastutil.io.FastByteArrayOutputStream;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;

// FIXME: Add unit tests.
public final class IndexedPositions {
    private static final int RESERVED_SIZE = 1024;
    private final LongList contigIDs;
    private final LongList encodedPositions;
    private final long hash;
    
    public IndexedPositions(final long hash) {
        this.contigIDs = new LongArrayList(RESERVED_SIZE);
        this.encodedPositions = new LongArrayList(RESERVED_SIZE);
        this.hash = hash;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(hash);
    }

    public void add(long contigID, long encodedPosition){
        contigIDs.add(contigID);
        encodedPositions.add(encodedPosition);
    }
    
    public void sort(){
        // FIXME: Not implemented
    }
    
    public @NotNull List<Anchor> toAnchors(final long posOnQuery){
        List<Anchor> anchors = new ObjectArrayList<>(contigIDs.size());
        for (int i = 0; i < contigIDs.size(); i++) {
            anchors.add(new Anchor(posOnQuery, contigIDs.getLong(i), encodedPositions.getLong(i)));
        }
        return anchors;
    }
    
    public void combine(@NotNull IndexedPositions other){
        if (hash != other.hash) {
            throw new IllegalArgumentException("Cannot combine IndexedPositions with different contigIDs");
        }
        contigIDs.addAll(other.contigIDs);
        encodedPositions.addAll(other.encodedPositions);
    }

    public static int estSerializedSize(final int size){
        return
            Long.BYTES + // hash
                Long.BYTES + // size
                Long.BYTES * size + // encodedPositions
                Long.BYTES * size+ // contigIDs
                Long.BYTES // CRC
            ;
    }

    public byte[] serialize() throws IOException {
        try(var w = new FastByteArrayOutputStream()) {
            final var crc = new CRC32Hash();

            final var hashEncoded = LongEncoder.encodeLongs(hash).array();
            w.write(hashEncoded);
            crc.update(hashEncoded);

            final var sizeEncoded = LongEncoder.encodeLongs(encodedPositions.size()).array();
            w.write(sizeEncoded);
            crc.update(sizeEncoded);

            final var encodedPositionsEncoded = LongEncoder.encodeLongList(encodedPositions).array();
            w.write(encodedPositionsEncoded);
            crc.update(encodedPositionsEncoded);

            final var contigIDsEncoded = LongEncoder.encodeLongList(contigIDs).array();
            w.write(contigIDsEncoded);
            crc.update(contigIDsEncoded);

            final var crcEncoded = LongEncoder.encodeLongs(crc.getValue()).array();
            w.write(crcEncoded);

            return w.array;
        }
    }

    public static @NotNull IndexedPositions deserialize(final @NotNull RandomAccessFile f) throws IOException {
        final long pos = f.getFilePointer();
        final long hash = f.readLong();
        final int size = (int) f.readLong();
        final int estSizes = estSerializedSize(size);
        final byte[] buffer = new byte[estSizes];
        f.seek(pos);
        f.readFully(buffer);

        final var bb = ByteBuffer.wrap(buffer);

        final var crc = new CRC32Hash();
        crc.update(buffer, 0, buffer.length - Long.BYTES);

        if (bb.getLong(buffer.length - Long.BYTES) != crc.getValue()){
            throw new IOException("CRC mismatch");
        }
        bb.position(Long.BYTES * 3);
        final var encodedPositions = new LongArrayList(size);
        for (int i = 0; i < size; i++) {
            encodedPositions.add(bb.getLong());
        }
        final var contigIDs = new LongArrayList(size);
        for (int i = 0; i < size; i++) {
            contigIDs.add(bb.getLong());
        }
        final var ip = new IndexedPositions(hash);
        ip.encodedPositions.addAll(encodedPositions);
        ip.contigIDs.addAll(contigIDs);
        return ip;
    }
}
