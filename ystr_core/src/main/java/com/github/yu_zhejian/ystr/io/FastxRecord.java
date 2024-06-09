package com.github.yu_zhejian.ystr.io;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

/**
 * A FASTA/FASTQ record.
 *
 * <p>Note, this class lacks support of very long records whose length is larger than
 * {@link Integer#MAX_VALUE}.
 *
 * @param seqid As described.
 * @param seq As described.
 * @param qual Empty for FASTA.
 */
public record FastxRecord(String seqid, byte[] seq, byte[] qual) {
    @Override
    public @NotNull String toString() {
        if (qual.length == 0) {
            return ">%s\n%s\n".formatted(seqid, new String(seq, StandardCharsets.UTF_8));
        }
        return "@%s\n%s\n+\n%s\n"
                .formatted(
                        seqid,
                        new String(seq, StandardCharsets.UTF_8),
                        new String(qual, StandardCharsets.UTF_8));
    }
}
