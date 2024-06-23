package com.github.yu_zhejian.ystr.io;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

/** Basic random access binary file parser. */
public abstract class BaseRandomBinaryFileParser implements AutoCloseable {
    /** The random accessing engine. */
    protected final RandomAccessFile raf;
    /** Channel of {@link #raf} */
    protected final FileChannel fc;
    /** Whether the file is created under Little Endian byte order. */
    protected boolean byteOrderIsLittleEndian;
    /** 4k alignment */
    protected static final int CHUNK_SIZE = 4096;
    /** Buffer used in {@link #readFourBytes()}. */
    protected final ByteBuffer intBuffer = ByteBuffer.allocateDirect(4);
    /** Buffer used in {@link #readEightBytes()}. */
    protected final ByteBuffer longBuffer = ByteBuffer.allocateDirect(8);

    /**
     * Set byte order. Should not be uised by external methods.
     *
     * @param byteOrder As described.
     */
    protected void setByteOrder(ByteOrder byteOrder) {
        byteOrderIsLittleEndian = byteOrder != ByteOrder.BIG_ENDIAN;
        intBuffer.order(byteOrder);
        longBuffer.order(byteOrder);
    }

    /**
     * Default constructor.
     *
     * @param f As described.
     * @throws IOException As described.
     * @throws IllegalArgumentException If file is of incorrect format.
     */
    protected BaseRandomBinaryFileParser(File f) throws IOException {
        raf = new RandomAccessFile(f, "r");
        fc = raf.getChannel();
    }

    /**
     * Read int64 in desired byte order.
     *
     * @return As described.
     * @throws IOException As described.
     */
    protected long readEightBytes() throws IOException {
        longBuffer.clear();
        fc.read(longBuffer);
        longBuffer.rewind();
        return longBuffer.getInt();
    }

    /**
     * Read int32 in desired byte order.
     *
     * @return As described.
     * @throws IOException As described.
     */
    protected long readFourBytes() throws IOException {
        intBuffer.clear();
        fc.read(intBuffer);
        intBuffer.rewind();
        return intBuffer.getInt();
    }

    @Override
    public void close() throws IOException {
        raf.close();
    }

    /**
     * As described.
     *
     * @return As described.
     */
    public boolean isLittleEndian() {
        return byteOrderIsLittleEndian;
    }
}
