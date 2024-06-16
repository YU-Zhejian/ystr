package com.github.yu_zhejian.ystr.io;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Streamline parsing of FASTA/FASTQ.
 *
 * <p>Implemented with the help of TONGYI Lingma.
 */
public final class FastxIterator implements Iterator<FastxRecord>, AutoCloseable {
    private static final String SPLIT_REGEX = "\\s+";
    private final BufferedReader reader;
    private FastxRecord currentRecord;
    private String currentSeqID;
    private boolean isFASTQ = false;

    /** Wrapper for {@link RuntimeException} to make SonarLint silence. */
    public static class FastxIOException extends RuntimeException {
        /**
         * Conbvenient constructor.
         *
         * @param description As described.
         * @param e As described.
         */
        public FastxIOException(String description, Exception e) {
            super(description, e);
        }
    }

    private void populateFirstSeqID() throws IOException {
        String line;
        while (currentSeqID == null && (line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            if (line.charAt(0) == '>') {
                currentSeqID = line.substring(1).split(SPLIT_REGEX)[0].trim();
                isFASTQ = false;
            } else if (line.charAt(0) == '@') {
                currentSeqID = line.substring(1).split(SPLIT_REGEX)[0].trim();
                isFASTQ = true;
            }
        }
    }

    /**
     * Default constructor.
     *
     * @param reader A reader using (preferably) {@link StandardCharsets#UTF_8} encoding or
     *     {@link StandardCharsets#US_ASCII} encoding.
     */
    public FastxIterator(Reader reader) {
        this.reader = new BufferedReader(reader);
        try {
            populateFirstSeqID();
            nextRecord();
        } catch (IOException e) {
            throw new FastxIOException("Error reading file", e);
        }
    }

    /**
     * Open a file using {@link StandardCharsets#UTF_8} encoding.
     *
     * @param file As described.
     * @return As described.
     * @throws IOException As described.
     */
    @Contract("_ -> new")
    public static @NotNull FastxIterator read(File file) throws IOException {
        return new FastxIterator(new FileReader(file, StandardCharsets.UTF_8));
    }

    /**
     * Wrapper for {@link #read(File)}
     *
     * @param path As described.
     * @return As described.
     * @throws IOException As described.
     */
    @Contract("_ -> new")
    public static @NotNull FastxIterator read(@NotNull Path path) throws IOException {
        return read(path.toFile());
    }

    /**
     * Wrapper for {@link #read(File)}
     *
     * @param path As described.
     * @return As described.
     * @throws IOException As described.
     */
    @Contract("_ -> new")
    public static @NotNull FastxIterator read(String path) throws IOException {
        return read(Path.of(path));
    }

    /**
     * Populate the reader with next record.
     *
     * @throws IOException As described.
     */
    private void nextRecord() throws IOException {
        currentRecord = null;
        if (currentSeqID == null) {
            close();
            return;
        }

        String nextSeqID = null;
        final StringBuilder currentSequence = new StringBuilder();
        final StringBuilder currentQuality = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            if ((!isFASTQ && line.charAt(0) == '>') || (isFASTQ && line.charAt(0) == '@')) {
                // Beginning of next record
                nextSeqID = line.substring(1).split(SPLIT_REGEX)[0].trim();
                break;
            } else if (isFASTQ) {
                if (line.charAt(0) == '+') {
                    continue;
                }
                if (currentSequence.isEmpty()) {
                    currentSequence.append(line);
                } else {
                    currentQuality.append(line);
                }
            } else {
                // Quality of current record
                currentSequence.append(line);
            }
        }
        currentRecord = new FastxRecord(
                currentSeqID,
                currentSequence.toString().getBytes(StandardCharsets.US_ASCII),
                isFASTQ ? currentQuality.toString().getBytes(StandardCharsets.US_ASCII) : null);
        currentSeqID = nextSeqID;
    }

    @Override
    public boolean hasNext() {
        return currentRecord != null;
    }

    @Override
    public @NotNull FastxRecord next() {
        if (currentRecord == null) throw new NoSuchElementException();
        var retv = currentRecord;
        try {
            nextRecord(); // Prepare for the next record
        } catch (IOException e) {
            throw new FastxIOException("Error reading file", e);
        }
        return retv;
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
