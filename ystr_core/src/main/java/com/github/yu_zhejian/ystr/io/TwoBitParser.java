package com.github.yu_zhejian.ystr.io;

import com.github.yu_zhejian.ystr.StrUtils;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.roaringbitmap.RoaringBitmap;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * A UCSC 2bit file parser supporting version 1 of the 2bit format.
 *
 * <p>TODO: Get a big endian testing file somewhere.
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
    /** The number of blocks of Ns in the file (representing unknown sequences) */
    private final RoaringBitmap[] nBlocks;
    /** The number of masked (lower-case) blocks */
    private final RoaringBitmap[] maskBlocks;

    /**
     * the DNA packed to two bits per base, represented as so: T - 00, C - 01, A - 10, G - 11. The
     * first base is in the most significant 2-bit byte; the last base is in the least significant 2
     * bits. For example, the sequence TCAG is represented as 00011011.
     */
    private static final byte[] BASES = new byte[] {'T', 'C', 'A', 'G'};

    /** Pre-computed byte-to-bases table. */
    public static final byte[][] PRE_COMPUTED;

    static {
        PRE_COMPUTED = new byte[256][4];
        for (int i = 0; i <= 0b11_11_11_11; i++) {
            PRE_COMPUTED[i] = decode((byte) i);
        }
    }

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
                | (raf.read());
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
        nBlocks = new RoaringBitmap[sequenceCount];
        maskBlocks = new RoaringBitmap[sequenceCount];
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
     * Populate masks or n count btmap.
     *
     * @return As described.
     * @throws IOException As described.
     */
    private @NotNull RoaringBitmap populate() throws IOException {
        var retv = new RoaringBitmap();
        var count = (int) readFourBytes();
        var starts = new long[count];
        var sizes = new long[count];
        for (var i = 0; i < count; i++) {
            starts[i] = (int) readFourBytes();
        }
        for (var i = 0; i < count; i++) {
            sizes[i] = (int) readFourBytes();
        }
        for (var i = 0; i < count; i++) {
            retv.add(starts[i], starts[i] + sizes[i]);
        }
        return retv;
    }

    /**
     * Ensure the {@link #dnaSizes}, {@link #dnaSizes}, {@link #nBlocks}, {@link #maskBlocks} and
     * {@link #seqOffsets} are populated. This method will be called for all operations inside this
     * class. Users may also call this method in advance for performance gains.
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
        nBlocks[seqID] = populate();
        maskBlocks[seqID] = populate();

        // which is 4 * maskBlockCount * 2 + reserved
        seqOffsets[seqID] = raf.getFilePointer() + 4;
    }

    /**
     * Read int64 in desired byte order.
     *
     * @return As described.
     * @throws IOException As described.
     */
    private long readEightBytes() throws IOException {
        long ret;
        var i1 = readFourBytes();
        var i2 = readFourBytes();
        if (byteOrderIsLittleEndian) {
            ret = i1 | (i2 << 32);
        } else {
            ret = (i1 << 32) | i2;
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
            ret = (raf.read())
                    | (((long) raf.read()) << 8)
                    | (((long) raf.read()) << 16)
                    | (((long) raf.read()) << 24);
        } else {
            ret = (((long) raf.read()) << 24)
                    | (((long) raf.read()) << 16)
                    | (((long) raf.read()) << 8)
                    | (raf.read());
        }
        return ret;
    }

    /**
     * Helper function that read one byte for 4 bases.
     *
     * @return As described.
     * @throws IOException As described.
     */
    private byte @NotNull [] readNt() throws IOException {
        return decode((byte) raf.read());
    }

    /**
     * Reference implementation. Also used to bootstrap {@link #PRE_COMPUTED}.
     *
     * @param encodedByte As described.
     * @return As described.
     */
    @Contract(value = "_ -> new", pure = true)
    public static byte @NotNull [] decode(byte encodedByte) {
        return new byte[] {
            BASES[encodedByte >> 6 & 0b11],
            BASES[encodedByte >> 4 & 0b11],
            BASES[encodedByte >> 2 & 0b11],
            BASES[encodedByte & 0b11]
        };
    }

    /**
     * Decode using pre-computed table.
     *
     * @param encodedByte As described.
     * @return As described.
     */
    @Contract(value = "_ -> new", pure = true)
    public static byte @NotNull [] decodePrecomputed(byte encodedByte) {
        return PRE_COMPUTED[encodedByte & 0xFF];
    }

    /**
     * Helper function that read multiple bytes.
     *
     * @param outArr As described.
     * @param startPos As described.
     * @param numByteToRead As described.
     * @throws IOException As described.
     */
    private void readNts(byte[] outArr, final int startPos, final int numByteToRead)
            throws IOException {
        var buffer = ByteBuffer.allocate(numByteToRead);
        var curPos = startPos;
        var fc = raf.getChannel();
        fc.read(buffer);
        buffer.rewind();
        // The following 6 lines are the most time-consuming. Interesting.
        for (var i = 0; i < numByteToRead; i++) {
            var b = buffer.get();
            var decoded = PRE_COMPUTED[b & 0xFF];
            // This is the fastest way of setting all bits.
            System.arraycopy(decoded, 0, outArr, curPos, 4);
            curPos += 4;
        }
    }

    @Contract("_, _, _, _ -> new")
    public byte @NotNull [] getSequence(
            final int seqID, final int start, final int end, boolean parseMasks)
            throws IOException {
        // Check whether `start == end` will impact performance, so
        StrUtils.ensureStartEndValid(start, end, dnaSizes[seqID]);
        loadSeqInfo(seqID);

        // Number of bases to read at the end.
        var retLen = end - start;
        var retl = new byte[retLen];

        // Number of bytes to skip at start.
        var numSkippedBytes = start >>> 2;

        var numBasesToDiscard = start - (numSkippedBytes << 2);
        raf.seek(numSkippedBytes + seqOffsets[seqID]);

        // Read until we reach the first complete byte
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

        var numBytesToRead = (retLen - curPosOnRetSeq) >> 2;
        readNts(retl, curPosOnRetSeq, numBytesToRead);
        curPosOnRetSeq += numBytesToRead << 2;

        // Read the last byte
        if (curPosOnRetSeq < retLen) {
            posOnBuffer = 0;
            var firstBase = readNt();
            while (curPosOnRetSeq < retLen) {
                retl[curPosOnRetSeq] = firstBase[posOnBuffer];
                posOnBuffer++;
                curPosOnRetSeq++;
            }
        }

        final var curMask = new RoaringBitmap();
        curMask.add((long) start, end);

        // N is always dealt with otherwise it will be mistaken as probably T.
        final var cm1 = curMask.clone();
        cm1.and(nBlocks[seqID]);

        int[] buffer = new int[256];
        var it = cm1.getBatchIterator();
        while (it.hasNext()) {
            // As suggested by
            // https://richardstartin.github.io/posts/roaringbitmap-performance-tricks
            int batch = it.nextBatch(buffer);
            for (int i = 0; i < batch; ++i) {
                retl[buffer[i] - start] = 'N';
            }
        }

        if (parseMasks) {
            final var cm2 = curMask.clone();
            cm2.and(maskBlocks[seqID]);
            it = cm2.getBatchIterator();
            while (it.hasNext()) {
                int batch = it.nextBatch(buffer);
                for (int i = 0; i < batch; ++i) {
                    retl[buffer[i] - start] += 32;
                }
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
     * Get sequence lengths as an ordered list.
     *
     * @return As described.
     */
    public @NotNull IntArrayList getSeqLengths() throws IOException {
        var retl = new IntArrayList(this.sequenceCount);
        for (var seqID = 0; seqID < sequenceCount; seqID++) {
            retl.add(getSeqLength(seqID));
        }
        return retl;
    }

    /**
     * Get the name of a specific sequence.
     *
     * @param seqID As described.
     * @return As described.
     */
    public @NotNull String getSeqName(int seqID) {
        return seqNames[seqID];
    }

    /**
     * Get sequence names and lengths of arbitrary order.
     *
     * @return As described.
     */
    public @NotNull Object2IntOpenHashMap<String> getSeqNameLengthMap() throws IOException {
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
}
