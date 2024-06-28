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
    protected final RandomAccessFile randomAccessFile;
    /** Channel of {@link #randomAccessFile} */
    protected final FileChannel fileChannel;
    /** Whether the file is created under Little Endian byte order. */
    protected boolean byteOrderIsLittleEndian;
    /** 4k alignment */
    protected static final int CHUNK_SIZE = 4096;
    /** Buffer used in {@link #readFourBytes()}. */
    protected final ByteBuffer intBuffer = ByteBuffer.allocateDirect(4);
    /** Buffer used in {@link #readEightBytes()}. */
    protected final ByteBuffer longBuffer = ByteBuffer.allocateDirect(8);

    /**
     * Set byte order. Should not be used by external methods.
     *
     * @param byteOrder As described.
     */
    protected void setByteOrder(final ByteOrder byteOrder) {
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
    protected BaseRandomBinaryFileParser(final File f) throws IOException {
        randomAccessFile = new RandomAccessFile(f, "r");
        fileChannel = randomAccessFile.getChannel();
    }

    /**
     * Read int64 in desired byte order.
     *
     * @return As described.
     * @throws IOException As described.
     */
    protected long readEightBytes() throws IOException {
        longBuffer.clear();
        fileChannel.read(longBuffer);
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
        fileChannel.read(intBuffer);
        intBuffer.rewind();
        return intBuffer.getInt();
    }

    @Override
    public void close() throws IOException {
        randomAccessFile.close();
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
