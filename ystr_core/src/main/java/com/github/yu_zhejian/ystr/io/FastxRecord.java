package com.github.yu_zhejian.ystr.io;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

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
 * @param qual Phred-33 or Phred-64 encoded quality. Please note that the sequence here should
 *     comfort {@link StandardCharsets#US_ASCII} encoding.
 */
public record FastxRecord(String seqid, byte[] seq, byte[] qual) {
    @Override
    public @NotNull String toString() {
        if (qual.length == 0) {
            return ">%s\n%s\n".formatted(seqid, new String(seq, StandardCharsets.US_ASCII));
        }
        return "@%s\n%s\n+\n%s\n"
                .formatted(
                        seqid,
                        new String(seq, StandardCharsets.US_ASCII),
                        new String(qual, StandardCharsets.US_ASCII));
    }
}
