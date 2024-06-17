package com.github.yu_zhejian.ystr.io;

import com.github.yu_zhejian.ystr.PyUtils;
import com.github.yu_zhejian.ystr.StrUtils;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * A UCSC 2bit file parser supporting the use of:
 *
 * <p>Long formats.
 */
public final class TwoBitParser implements AutoCloseable {

    /** The random accessing engine. */
    private final RandomAccessFile raf;
    /** Whether the file is created under Little Endian byte order. */
    private final boolean byteOrderIsLittleEndian;
    /** Should be 0x0. Readers should abort if they see a version number higher than 0 */
    public static final long VERSION = 0x0;
    /** Version for 2bit files that supports >= 4GB assembly. */
    public static final long VERSION_LONG = 0x1;
    /** the number 0x1A412743 in the architecture of the machine that created the file */
    public static final long SIGNATURE_BIG_ENDIAN = 0x1A412743;
    /** the number 0x1A412743 in the architecture of the machine that created the file */
    public static final long SIGNATURE_LITTLE_ENDIAN = 0x4327411A;
    /** Maximum sequence name length. */
    public static final int MAX_SEQ_NAME_LENGTH = 255;
    /** the number of sequences in the file */
    private final int sequenceCount;
    /**
     * Whether the file supports long sequences, which:
     *
     * <ul>
     *   <li>Have {@link #VERSION_LONG} in {@code version} field.
     *   <li>Supports sequences longer than 4GB</>
     * </ul>
     */
    private final boolean supportsLongSequences;
    /**
     * The sequence name itself (in ASCII-compatible byte string), of variable length depending on
     * nameSize. No longer than 255.
     */
    private final String[] seqNames;
    /**
     * The 32-bit (or 64) offset of the sequence data relative to the start of the file, not aligned
     * to any 4-byte padding boundary
     */
    private final long[] offsets;
    /** number of bases of DNA in the sequence */
    private final int[] dnaSizes;
    /**
     * Lazy-evaluated offset of start of sequences, which is placed after masking information of
     * each block. Will be 0 if not evaluated.
     */
    private final long[] seqOffsets;
    /** the number of blocks of Ns in the file (representing unknown sequence) */
    private final int[] nBlockCount;
    /** the number of masked (lower-case) blocks */
    private final int[] maskBlockCount;

    /**
     * the DNA packed to two bits per base, represented as so: T - 00, C - 01, A - 10, G - 11. The
     * first base is in the most significant 2-bit byte; the last base is in the least significant 2
     * bits. For example, the sequence TCAG is represented as 00011011.
     */
    private static final byte[] BASES = new byte[] {'T', 'C', 'A', 'G'};

    /**
     * Default constructor.
     *
     * @param f As described.
     * @throws IOException As described.
     * @throws IllegalArgumentException If file is of incorrect format.
     */
    public TwoBitParser(File f) throws IOException {
        raf = new RandomAccessFile(f, "r");
        var signature = ((long) raf.read() << 24)
                | (((long) raf.read()) << 16)
                | (((long) raf.read()) << 8)
                | (((long) raf.read()));
        if (signature == SIGNATURE_BIG_ENDIAN) {
            byteOrderIsLittleEndian = false;
        } else if (signature == SIGNATURE_LITTLE_ENDIAN) {
            byteOrderIsLittleEndian = true;
        } else {
            throw new IllegalArgumentException(String.format(
                    "Wrong start signature in 2BIT format. Required: 0x1A412743 (Big Endian)/0x4327411A (Little Endian). Actual: 0x%s",
                    Long.toHexString(signature)));
        }
        var version = readFourBytes();
        if (version == VERSION) {
            supportsLongSequences = false;
        } else if (version == VERSION_LONG) {
            supportsLongSequences = true;
        } else {
            throw new IllegalArgumentException(String.format(
                    "Wrong version in 2BIT format. Required: 0x0/0x1. Actual: 0x%s",
                    Long.toHexString(version)));
        }
        sequenceCount = (int) readFourBytes();
        if (sequenceCount < 0) {
            throw new IllegalArgumentException(String.format(
                    "Wrong sequenceCount in 2BIT format. Required: >=0. Actual: %s",
                    sequenceCount));
        }
        raf.skipBytes(4); // reserved - always zero for now
        seqNames = new String[sequenceCount];
        offsets = new long[sequenceCount];
        seqOffsets = new long[sequenceCount];
        dnaSizes = new int[sequenceCount];
        nBlockCount = new int[sequenceCount];
        maskBlockCount = new int[sequenceCount];
        for (var seqID = 0; seqID < sequenceCount; seqID++) {
            var nameSize = raf.readByte();
            if (nameSize < 0) {
                throw new IllegalArgumentException(String.format(
                        "Wrong nameSize in 2BIT format. Required: 0 <= nameSize <= %d. Actual: %d",
                        MAX_SEQ_NAME_LENGTH, nameSize));
            }
            var name = new byte[nameSize];
            for (var i = 0; i < nameSize; i++) {
                name[i] = raf.readByte();
            }
            seqNames[seqID] = new String(name, StandardCharsets.US_ASCII);
            if (supportsLongSequences) {
                offsets[seqID] = readEightBytes();
            } else {
                offsets[seqID] = readFourBytes();
            }
        }
    }

    /**
     * Ensure the {@link #dnaSizes}, {@link #dnaSizes}, {@link #nBlockCount},
     * {@link #maskBlockCount} and {@link #seqOffsets} are populated. This method will be called for
     * all operations inside this class. Users may also call this method in advance for performance
     * gains.
     *
     * @param seqID As described.
     * @throws IOException As described.
     */
    public void loadSeqInfo(int seqID) throws IOException {
        if (dnaSizes[seqID] != 0) {
            return; // 2bit format does not allow empty DNA sequences.
        }
        raf.seek(offsets[seqID]);
        // Interesting. It seems the latest 2bit format also lacks support of one chromosome >=
        // 4GiB.
        // TODO: Check this.
        dnaSizes[seqID] = (int) readFourBytes();
        nBlockCount[seqID] = (int) readFourBytes();
        // which is 4 * nBlockCount * 2
        raf.skipBytes(nBlockCount[seqID] << 3);
        maskBlockCount[seqID] = (int) readFourBytes();
        // which is 4 * maskBlockCount * 2 + reserved
        seqOffsets[seqID] = raf.getFilePointer() + ((long) maskBlockCount[seqID] << 3) + 4;
    }

    /**
     * Read int64 in desired byte order.
     *
     * @return As described.
     * @throws IOException As described.
     */
    private long readEightBytes() throws IOException {
        long ret;
        if (byteOrderIsLittleEndian) {
            ret = readFourBytes() | (readFourBytes() << 32);
        } else {
            ret = (readFourBytes() << 32) | readFourBytes();
        }
        return ret;
    }

    /**
     * Read int32 in desired byte order.
     *
     * @return As described.
     * @throws IOException As described.
     */
    private long readFourBytes() throws IOException {
        long ret;
        if (byteOrderIsLittleEndian) {
            ret = ((long) raf.read())
                    | (((long) raf.read()) << 8)
                    | (((long) raf.read()) << 16)
                    | (((long) raf.read()) << 24);
        } else {
            ret = (((long) raf.read()) << 24)
                    | (((long) raf.read()) << 16)
                    | (((long) raf.read()) << 8)
                    | ((long) raf.read());
        }
        return ret;
    }

    private byte @NotNull [] readNt() throws IOException {
        var curByte = (byte) raf.read();
        return new byte[] {
            BASES[curByte >> 6 & 0b11],
            BASES[curByte >> 4 & 0b11],
            BASES[curByte >> 2 & 0b11],
            BASES[curByte & 0b11]
        };
    }

    @Contract("_, _, _, _ -> new")
    public byte @NotNull [] getSequence(
            final int seqID, final int start, final int end, final boolean parseMasks)
            throws IOException {
        if (start == end) {
            return new byte[0];
        }
        loadSeqInfo(seqID);
        StrUtils.ensureStartEndValid(start, end, dnaSizes[seqID]);
        var retLen = end - start;
        var retl = new byte[retLen];
        var numSkippedBytes = start >>> 2;
        var numBasesToDiscard = start - (numSkippedBytes << 2);
        raf.seek(numSkippedBytes + seqOffsets[seqID]);

        var posOnBuffer = 0;
        var curPosOnRetSeq = 0;
        if (numBasesToDiscard != 0) {
            var firstBase = readNt();
            while (numBasesToDiscard > 0) {
                posOnBuffer++;
                numBasesToDiscard--;
            }
            while (posOnBuffer < 4 && curPosOnRetSeq < retLen) {
                retl[curPosOnRetSeq] = firstBase[posOnBuffer];
                posOnBuffer++;
                curPosOnRetSeq++;
            }
        }
        while (curPosOnRetSeq < retLen) {
            posOnBuffer = 0;
            var firstBase = readNt();
            while (posOnBuffer < 4 && curPosOnRetSeq < retLen) {
                retl[curPosOnRetSeq] = firstBase[posOnBuffer];
                posOnBuffer++;
                curPosOnRetSeq++;
            }
        }

        return retl;
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
    public long size() {
        return sequenceCount;
    }

    /**
     * As described.
     *
     * @return As described.
     */
    public boolean isLittleEndian() {
        return byteOrderIsLittleEndian;
    }

    /**
     * As described.
     *
     * @return As described.
     */
    public boolean isSupportsLongSequences() {
        return supportsLongSequences;
    }

    /**
     * Get sequence names as an ordered list.
     *
     * @return As described.
     */
    public @NotNull ObjectArrayList<String> getSeqNames() {
        var retl = new ObjectArrayList<String>(this.sequenceCount);
        retl.addAll(Arrays.asList(seqNames));
        return retl;
    }

    /**
     * Get sequence names and lengths of arbitrary order.
     *
     * @return As described.
     */
    public @NotNull Object2IntOpenHashMap<String> getSeqLengths() throws IOException {
        var retl = new Object2IntOpenHashMap<String>(this.sequenceCount);
        for (var seqID = 0; seqID < sequenceCount; seqID++) {
            retl.put(seqNames[seqID], getSeqLength(seqID));
        }
        return retl;
    }

    /**
     * Get length for some sequence ID.
     *
     * @param seqID As described.
     * @return As described.
     */
    public int getSeqLength(final int seqID) throws IOException {
        loadSeqInfo(seqID);
        return dnaSizes[seqID];
    }

    /**
     * @param nBlockStarts an array of length nBlockCount of 32-bit integers indicating the
     *     (0-based) starting position of a block of Ns
     * @param nBlockSizes an array of length nBlockCount of 32-bit integers indicating the length of
     *     a block of Ns
     * @param maskBlockStarts an array of length maskBlockCount of 32-bit integers indicating the
     *     (0-based) starting position of a masked block
     * @param maskBlockSizes an array of length maskBlockCount of 32-bit integers indicating the
     *     length of a masked block
     */
    public record MaskingInformation(
            int[] nBlockStarts, int[] nBlockSizes, int[] maskBlockStarts, int[] maskBlockSizes) {
        @Override
        public @NotNull String toString() {
            return "MaskingInformation{" + "nBlockStarts="
                    + Arrays.toString(nBlockStarts) + ", nBlockSizes="
                    + Arrays.toString(nBlockSizes) + ", maskBlockStarts="
                    + Arrays.toString(maskBlockStarts) + ", maskBlockSizes="
                    + Arrays.toString(maskBlockSizes) + '}';
        }
    }

    @Contract("_ -> new")
    public @NotNull MaskingInformation getMaskingInformation(final int seqID) throws IOException {
        loadSeqInfo(seqID);
        raf.seek(offsets[seqID] + 8); // dnaSize, nBlockCount

        var nBlockCount = this.nBlockCount[seqID];
        var nBlockStarts = new int[nBlockCount];
        var nBlockSizes = new int[nBlockCount];
        for (var i = 0; i < nBlockCount; i++) {
            nBlockStarts[i] = (int) readFourBytes();
        }
        for (var i = 0; i < nBlockCount; i++) {
            nBlockSizes[i] = (int) readFourBytes();
        }

        var maskBlockCount = this.maskBlockCount[seqID];
        raf.skipBytes(4); // maskBlockCount
        var maskBlockStarts = new int[maskBlockCount];
        var maskBlockSizes = new int[maskBlockCount];
        for (var i = 0; i < maskBlockCount; i++) {
            maskBlockStarts[i] = (int) readFourBytes();
        }
        for (var i = 0; i < maskBlockCount; i++) {
            maskBlockSizes[i] = (int) readFourBytes();
        }
        return new MaskingInformation(nBlockStarts, nBlockSizes, maskBlockStarts, maskBlockSizes);
    }
}
