package com.github.yu_zhejian.ystr.io;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
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
public class FastxIterator implements Iterator<FastxRecord>, AutoCloseable {
    private static final String SPLIT_REGEX = "\\s+";
    private final BufferedReader reader;
    private String currentSeqId;
    private final StringBuilder currentSequence = new StringBuilder();
    private final StringBuilder currentQuality = new StringBuilder();
    private boolean hasNext = true;

    public FastxIterator(Reader reader) {
        this.reader = new BufferedReader(reader);
        try {
            nextRecord(); // Prepare for the next record
        } catch (IOException e) {
            throw new RuntimeException("Error reading file", e);
        }
    }

    @Contract("_ -> new")
    public static @NotNull FastxIterator read(File file) throws FileNotFoundException {
        return new FastxIterator(new FileReader(file));
    }

    @Contract("_ -> new")
    public static @NotNull FastxIterator read(@NotNull Path path) throws FileNotFoundException {
        return read(path.toFile());
    }

    @Contract("_ -> new")
    public static @NotNull FastxIterator read(String path) throws FileNotFoundException {
        return read(Path.of(path));
    }

    private void nextRecord() throws IOException {
        boolean isFASTQ = false;
        boolean hadFastqSeqPopulated = false;

        currentSeqId = null;
        currentSequence.setLength(0);
        currentQuality.setLength(0); // Reset quality for FASTQ

        String line;
        while ((line = reader.readLine()) != null) {
            if (line.charAt(0) == '>') {
                if (currentSeqId != null) { // End of previous record
                    break;
                }
                currentSeqId = line.substring(1).split(SPLIT_REGEX)[0].trim();
                isFASTQ = false;
            } else if (line.charAt(0) == '@') {
                if (currentSeqId != null) { // End of previous record
                    break;
                }
                currentSeqId = line.substring(1).split(SPLIT_REGEX)[0].trim();
                isFASTQ = true;
            } else if (isFASTQ && !hadFastqSeqPopulated) {
                currentSequence.append(line.trim());
                hadFastqSeqPopulated = true;
            } else if (isFASTQ && line.charAt(0) == '+') {
                continue;
            } else if (isFASTQ) {
                // Quality scores after '+'
                currentQuality.append(line.trim());
                break; // End of record
            } else {
                currentSequence.append(line.trim());
            }
        }
        if (currentSeqId == null) {
            hasNext = false;
            close();
        }
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public @NotNull FastxRecord next() {
        if (!hasNext) throw new NoSuchElementException();

        var qualityBytes = currentQuality.toString().getBytes(StandardCharsets.UTF_8);
        var record = new FastxRecord(
                currentSeqId,
                currentSequence.toString().getBytes(StandardCharsets.UTF_8),
                qualityBytes);
        try {
            nextRecord(); // Prepare for the next record
        } catch (IOException e) {
            throw new RuntimeException("Error reading file", e);
        }
        return record;
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
