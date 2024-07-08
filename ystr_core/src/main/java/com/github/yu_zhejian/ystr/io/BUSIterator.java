package com.github.yu_zhejian.ystr.io;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.NoSuchElementException;

/** @see <a href="https://github.com/BUStools/BUS-format">Specification</a> */
public final class BUSIterator implements Iterator<BUSRecord>, AutoCloseable {

    private final ReadableByteChannel fc;
    private final RandomAccessFile f;
    private final BUSHeader header;
    private final long fLen;
    private boolean atEOF;
    private final ByteBuffer buffer;

    public BUSIterator(@NotNull RandomAccessFile f) throws IOException {
        this.f = f;
        fc = f.getChannel();
        fLen = f.length();
        this.header = BUSHeader.parse(fc);
        atEOF = fLen == f.getFilePointer();
        buffer = ByteBuffer.allocateDirect(BUSRecord.BIN_SIZE);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
    }

    @Contract("_ -> new")
    public static @NotNull BUSIterator read(@NotNull Path path) throws IOException {
        return read(path.toFile());
    }

    @Contract("_ -> new")
    public static @NotNull BUSIterator read(@NotNull String path) throws IOException {
        return read(new File(path));
    }

    @Contract("_ -> new")
    public static @NotNull BUSIterator read(File file) throws IOException {
        return new BUSIterator(new RandomAccessFile(file, "r"));
    }

    public BUSHeader getHeader() {
        return header;
    }

    @Override
    public boolean hasNext() {
        return !atEOF;
    }

    private @NotNull BUSRecord nextRecord() throws IOException {
        buffer.clear();
        fc.read(buffer);
        buffer.rewind();
        var nextRecord = BUSRecord.parse(buffer, header);
        atEOF = fLen == f.getFilePointer();
        return nextRecord;
    }

    @Override
    public @NotNull BUSRecord next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        try {
            return nextRecord();
        } catch (IOException e) {
            throw new RuntimeIOException("", e);
        }
    }

    @Override
    public void close() throws IOException {
        fc.close();
        f.close();
    }
}
