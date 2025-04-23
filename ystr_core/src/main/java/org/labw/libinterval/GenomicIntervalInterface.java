package org.labw.libinterval;

import org.jetbrains.annotations.NotNull;

/**
 * Minimal requirements for intervals on genomes.
 *
 * <p>This interface adds two critical properties:
 *
 * <ul>
 *   <li><b>Strand</b>, see {@link StrandUtils}.
 *   <li><b>Contig</b>, which can be a chromosome, assembly scaffold, plasmid, TGS sequencing reads,
 *       or any other immutable continuous sequences.
 * </ul>
 */
public interface GenomicIntervalInterface extends IntervalInterface {
    /**
     * Get contig name.
     *
     * @return As described.
     */
    String getContigName();

    /**
     * Get strand as integer.
     *
     * @return As described.
     */
    int getStrand();

    /**
     * Enhanced {@link IntervalInterface#compareTo} that compares contig names but ignores strands.
     *
     * @return see {@link IntervalInterface#compareTo}
     * @param other See {@link IntervalInterface#compareTo}.
     */
    int compareTo(@NotNull GenomicIntervalInterface other);

    default String simplifiedToGenomicString() {
        return "%s:%d-%d:%s"
                .formatted(
                        this.getContigName(),
                        this.getStart(),
                        this.getEnd(),
                        StrandUtils.strandRepr(this.getStrand()));
    }
}
