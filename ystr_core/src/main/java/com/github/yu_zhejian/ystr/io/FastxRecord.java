package com.github.yu_zhejian.ystr.io;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

/**
 * A FASTA/FASTQ record.
 *
 * <p>Note, this class lacks support of very long records whose length is larger than
 * {@link Integer#MAX_VALUE}.
 *
 * @param seqid The name of the sequence, chromosome, scaffold or contig. May comfort
 *     {@link StandardCharsets#UTF_8} encoding.
 * @param seq The sequence, which should be presented in both FASTA and FASTQ type. Could be DNA,
 *     RNA, protein, or other strings. Please note that the sequence here should comfort
 *     {@link StandardCharsets#US_ASCII} encoding.
 * @param qual Phred-33 or Phred-64 encoded quality, which should be set to {@code null} for FASTA
 *     records. Please note that the sequence here should comfort {@link StandardCharsets#US_ASCII}
 *     encoding.
 */
public record FastxRecord(String seqid, byte[] seq, byte @Nullable [] qual) {

    /**
     * Default constructor.
     *
     * @param seqid As described.
     * @param seq As described.
     * @param qual As described.
     * @throws IllegalArgumentException If {@link #qual} is noy {@code null} and length of
     *     {@link #qual} and {@link #seq} differs.
     */
    public FastxRecord {
        if (qual != null && qual.length != seq.length) {
            throw new IllegalArgumentException(
                    "Parsing FASTQ record exception: record '%s': seq and qual length differ. Actual: %d vs %d"
                            .formatted(seqid, seq.length, qual.length));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FastxRecord that)) return false;
        return Objects.deepEquals(seq, that.seq)
                && Objects.deepEquals(qual, that.qual)
                && Objects.equals(seqid, that.seqid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(seqid, Arrays.hashCode(seq), Arrays.hashCode(qual));
    }

    @Override
    public @NotNull String toString() {
        if (qual == null) {
            return ">%s%n%s%n".formatted(seqid, new String(seq, StandardCharsets.US_ASCII));
        }
        return "@%s%n%s%n+%n%s%n"
                .formatted(
                        seqid,
                        new String(seq, StandardCharsets.US_ASCII),
                        new String(qual, StandardCharsets.US_ASCII));
    }

    /**
     * Convert the current record to FASTA by dropping quality values.
     *
     * @return A new FASTA record.
     */
    @Contract(" -> new")
    public @NotNull FastxRecord toFasta() {
        return new FastxRecord(seqid, seq, null);
    }

    /**
     * Convert the current record to FASTQ by adding quality values.
     *
     * @param qual Quality value to apply to all bases.
     * @return A new FASTQ record.
     */
    public @NotNull FastxRecord toFastq(byte qual) {
        var qualArr = new byte[seq.length];
        Arrays.fill(qualArr, qual);
        return new FastxRecord(seqid, seq, qualArr);
    }

    /**
     * Convert the current record to FASTQ by adding quality values.
     *
     * @param qual Quality value for each base.
     * @return A new FASTQ record.
     */
    public @NotNull FastxRecord toFastq(byte[] qual) {
        return new FastxRecord(seqid, seq, qual);
    }

    /**
     * Get the length of the sequence.
     *
     * @return As described.
     */
    public int getLength() {
        return seq.length;
    }

    /**
     * Create a new instance from FASTQ strings.
     *
     * @param seqid As described.
     * @param seq Sequence that will be decoded using {@link StandardCharsets#US_ASCII} encoding.
     * @param qual Quality that will be decoded using {@link StandardCharsets#US_ASCII} encoding.
     * @return A FASTQ record.
     */
    @Contract("_, _, _ -> new")
    public static @NotNull FastxRecord ofStrings(
            String seqid, @NotNull String seq, @NotNull String qual) {
        return new FastxRecord(
                seqid,
                seq.getBytes(StandardCharsets.US_ASCII),
                qual.getBytes(StandardCharsets.US_ASCII));
    }

    /**
     * Create a new instance from FASTA strings.
     *
     * @param seqid As described.
     * @param seq Sequence that will be decoded using {@link StandardCharsets#US_ASCII} encoding.
     * @return A FASTA record.
     */
    @Contract("_, _ -> new")
    public static @NotNull FastxRecord ofStrings(String seqid, @NotNull String seq) {
        return new FastxRecord(seqid, seq.getBytes(StandardCharsets.US_ASCII), null);
    }
}
