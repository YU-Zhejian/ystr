package com.github.yu_zhejian.ystr.io;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Streamline parsing of FASTA/FASTQ.
 *
 * <p>Implemented with the help of TONGYI Lingma.
 */
public final class FastxIterator implements Iterator<FastxRecord>, AutoCloseable {
    /** Whitespace characters. */
    private static final String SPLIT_REGEX = "\\s+";
    private static final char FASTA_RECORD_NAME_START = '>';
    private static final char FASTQ_RECORD_NAME_START = '@';
    private static final char FASTQ_QUAL_NAME_START = '+';

    /** As described. */
    private final BufferedReader reader;
    /** The current usable record. */
    private @Nullable FastxRecord currentRecord;
    /** Staged sequence ID for next possible record. */
    private String currentSeqID;
    /** Whether the underlying stream is FASTQ. */
    private boolean isFASTQ; // Default to false
    /** As described. */
    private final boolean trimSeqID;

    /**
     * Move cursor to the sequence of first record while getting its ID. {@link #nextRecord()}
     * should be immediately called otherwise {@link #currentRecord} will be {@code null}.
     *
     * @throws IOException As described.
     */
    private void populateFirstSeqID() throws IOException {
        String line;
        while (currentSeqID == null && (line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            if (line.charAt(0) == FASTA_RECORD_NAME_START) {
                currentSeqID = performTrimSeqID(line);
                isFASTQ = false;
            } else if (line.charAt(0) == FASTQ_RECORD_NAME_START) {
                currentSeqID = performTrimSeqID(line);
                isFASTQ = true;
            }
        }
    }

    /**
     * Trim the sequence ID.
     *
     * @param line As described.
     * @return As described.
     */
    private String performTrimSeqID(String line) {
        return trimSeqID ? line.substring(1).split(SPLIT_REGEX)[0].trim() : line.substring(1);
    }

    /**
     * Default constructor.
     *
     * @param reader A reader using (preferably) {@link StandardCharsets#UTF_8} encoding or
     *     {@link StandardCharsets#US_ASCII} encoding.
     * @param trimSeqID Whether to trim {@link FastxRecord#seqid()} by removing all sequences after
     *     its first whitespace.
     */
    public FastxIterator(Reader reader, boolean trimSeqID) {
        this.reader = new BufferedReader(reader);
        this.trimSeqID = trimSeqID;
        try {
            populateFirstSeqID();
            nextRecord();
        } catch (IOException e) {
            throw new RuntimeIOException("Error reading file", e);
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
    public static @NotNull FastxIterator read(@NotNull File file) throws IOException {
        return read(file.toPath());
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
        return new FastxIterator(Files.newBufferedReader(path, StandardCharsets.UTF_8), true);
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
            if (!(line.isEmpty() || (isFASTQ && line.charAt(0) == FASTQ_QUAL_NAME_START))) {
                line = line.trim();
                if ((!isFASTQ && line.charAt(0) == FASTA_RECORD_NAME_START)
                        || (isFASTQ && line.charAt(0) == FASTQ_RECORD_NAME_START)) {
                    // Beginning of next record
                    nextSeqID = performTrimSeqID(line);
                    break;
                } else if (isFASTQ) {
                    (currentSequence.isEmpty() ? currentSequence : currentQuality).append(line);
                } else {
                    // Quality of current record
                    currentSequence.append(line);
                }
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
        if (currentRecord == null) {
            throw new NoSuchElementException();
        }
        final var retv = currentRecord;
        try {
            nextRecord(); // Prepare for the next record
        } catch (IOException e) {
            throw new RuntimeIOException("Error reading file", e);
        }
        return retv;
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
