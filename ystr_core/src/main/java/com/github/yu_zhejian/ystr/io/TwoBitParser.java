package com.github.yu_zhejian.ystr.io;

import com.github.yu_zhejian.ystr.codec.TwoBitCodec;
import com.github.yu_zhejian.ystr.utils.StrUtils;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.roaringbitmap.RoaringBitmap;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

/**
 * A UCSC 2bit file parser supporting version 1 of the 2bit format.
 *
 * <p>TODO: Get a big endian testing file somewhere.
 *
 * @see TwoBitCodec
 */
public final class TwoBitParser extends BaseRandomBinaryFileParser {
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
    /** The codec * */
    private final TwoBitCodec codec = new TwoBitCodec();

    /**
     * Default constructor.
     *
     * @param f As described.
     * @throws IOException As described.
     * @throws IllegalArgumentException If file is of incorrect format.
     */
    public TwoBitParser(File f) throws IOException {
        super(f);
        final var signatureBuffer = ByteBuffer.allocateDirect(4);
        signatureBuffer.order(ByteOrder.BIG_ENDIAN);
        fileChannel.read(signatureBuffer);
        signatureBuffer.rewind();

        final var signature = signatureBuffer.getInt();
        if (signature == SIGNATURE_BIG_ENDIAN) {
            setByteOrder(ByteOrder.BIG_ENDIAN);
        } else if (signature == SIGNATURE_LITTLE_ENDIAN) {
            setByteOrder(ByteOrder.LITTLE_ENDIAN);
        } else {
            throw new IllegalArgumentException(String.format(
                    "Wrong start signature in 2BIT format. Required: 0x1A412743 (Big Endian)/0x4327411A (Little Endian). Actual: 0x%s",
                    Long.toHexString(signature)));
        }
        final var version = readFourBytes();
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
        randomAccessFile.skipBytes(4); // reserved - always zero for now
        seqNames = new String[sequenceCount];
        offsets = new long[sequenceCount];
        seqOffsets = new long[sequenceCount];
        dnaSizes = new int[sequenceCount];
        nBlocks = new RoaringBitmap[sequenceCount];
        maskBlocks = new RoaringBitmap[sequenceCount];
        var name = new byte[MAX_SEQ_NAME_LENGTH];
        for (var seqID = 0; seqID < sequenceCount; seqID++) {
            final byte nameSize = randomAccessFile.readByte();
            if (nameSize < 0) {
                throw new IllegalArgumentException(String.format(
                        "Wrong nameSize in 2BIT format. Required: 0 <= nameSize <= %d. Actual: %d",
                        MAX_SEQ_NAME_LENGTH, nameSize));
            }
            randomAccessFile.read(name, 0, nameSize);
            seqNames[seqID] = new String(name, 0, nameSize, StandardCharsets.US_ASCII);
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
        final var retv = new RoaringBitmap();
        final var count = (int) readFourBytes();
        final var starts = new long[count];
        final var sizes = new long[count];
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
        randomAccessFile.seek(offsets[seqID]);
        // Interesting. It seems the latest 2bit format also lacks support of one chromosome >=
        // 4GiB.
        dnaSizes[seqID] = (int) readFourBytes();
        nBlocks[seqID] = populate();
        maskBlocks[seqID] = populate();

        // which is 4 * maskBlockCount * 2 + reserved
        seqOffsets[seqID] = randomAccessFile.getFilePointer() + 4;
    }

    /**
     * Helper function that read one byte for 4 bases.
     *
     * @return As described.
     * @throws IOException As described.
     */
    private byte @NotNull [] readNt() throws IOException {
        final var buffer = new byte[1];
        final var decoded = new byte[4];
        randomAccessFile.read(buffer, 0, 1);
        codec.decode(buffer, decoded, 0, 0, 1);
        return decoded;
    }

    /**
     * Helper function that read multiple bytes.
     *
     * @param dst As described.
     * @param dstStart Start position in {@code outArr}.
     * @param numBytesToRead As described.
     * @throws IOException As described.
     */
    private void readNts(final byte[] dst, final int dstStart, final int numBytesToRead)
            throws IOException {
        var numBytesLeftToRead = numBytesToRead;
        final var buffer = new byte[numBytesLeftToRead];
        int numBytesReadForThisChunk;
        var dstPos = dstStart;
        while (numBytesLeftToRead > CHUNK_SIZE) {
            numBytesReadForThisChunk = randomAccessFile.read(buffer, 0, CHUNK_SIZE);
            if (numBytesReadForThisChunk == -1) {
                throw new EOFException();
            }
            numBytesLeftToRead -= numBytesReadForThisChunk;
            dstPos += codec.decode(buffer, dst, 0, dstPos, numBytesReadForThisChunk);
        }
        if (numBytesLeftToRead > 0) {
            numBytesReadForThisChunk = randomAccessFile.read(buffer, 0, numBytesLeftToRead);
            if (numBytesReadForThisChunk == -1) {
                throw new EOFException();
            }
            codec.decode(buffer, dst, 0, dstPos, numBytesReadForThisChunk);
        }
    }

    /**
     * Get a sequence from the current file.
     *
     * @param seqID As described.
     * @param start As described.
     * @param end As described.
     * @param parseMasks Whether to parse masks, which is time-consuming. Note that this function
     *     will parse {@code N} anyway, otherwise they will be mistaken as {@code T}s.
     * @return As described.
     * @throws IOException As described.
     */
    @Contract("_, _, _, _ -> new")
    public byte @NotNull [] getSequence(
            final int seqID, final int start, final int end, boolean parseMasks)
            throws IOException {
        // Check whether `start == end` will impact performance, so
        StrUtils.ensureStartEndValid(start, end, dnaSizes[seqID]);
        loadSeqInfo(seqID);

        // Number of bases to read at the end.
        final var retLen = end - start;
        final var dst = new byte[retLen];

        // Number of bytes to skip at start.
        var numSkippedBytes = start >>> 2;

        var numBasesToDiscard = start - (numSkippedBytes << 2);
        randomAccessFile.seek(numSkippedBytes + seqOffsets[seqID]);

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
                dst[curPosOnRetSeq] = firstBase[posOnBuffer];
                posOnBuffer++;
                curPosOnRetSeq++;
            }
        }

        var numBytesToRead = (retLen - curPosOnRetSeq) >> 2;
        readNts(dst, curPosOnRetSeq, numBytesToRead);
        curPosOnRetSeq += numBytesToRead << 2;

        // Read the last byte
        if (curPosOnRetSeq < retLen) {
            posOnBuffer = 0;
            var firstBase = readNt();
            while (curPosOnRetSeq < retLen) {
                dst[curPosOnRetSeq] = firstBase[posOnBuffer];
                posOnBuffer++;
                curPosOnRetSeq++;
            }
        }

        final var curMask = new RoaringBitmap();
        curMask.add((long) start, end);

        // N is always dealt with otherwise it will be mistaken as probably T.
        final var cm1 = curMask.clone();
        cm1.and(nBlocks[seqID]);

        final int[] buffer = new int[256];
        final var it1 = cm1.getBatchIterator();
        while (it1.hasNext()) {
            // As suggested by
            // https://richardstartin.github.io/posts/roaringbitmap-performance-tricks
            int batch = it1.nextBatch(buffer);
            for (int i = 0; i < batch; ++i) {
                dst[buffer[i] - start] = 'N';
            }
        }

        if (parseMasks) {
            int batch;
            final var cm2 = curMask.clone();
            cm2.and(maskBlocks[seqID]);
            final var it2 = cm2.getBatchIterator();
            while (it2.hasNext()) {
                batch = it2.nextBatch(buffer);
                for (int i = 0; i < batch; ++i) {
                    dst[buffer[i] - start] += 32;
                }
            }
        }
        return dst;
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
    public boolean isSupportsLongSequences() {
        return supportsLongSequences;
    }

    /**
     * Get sequence names as an ordered list.
     *
     * @return As described.
     */
    @SuppressWarnings("PMD.LooseCoupling")
    public @NotNull ObjectArrayList<String> getSeqNames() {
        var retl = new ObjectArrayList<String>(this.sequenceCount);
        retl.addElements(0, seqNames);
        return retl;
    }

    /**
     * Get sequence lengths as an ordered list.
     *
     * @return As described.
     */
    @SuppressWarnings("PMD.LooseCoupling")
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
    @SuppressWarnings("PMD.LooseCoupling")
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
