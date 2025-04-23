package org.labw.libinterval;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Objects;

public record FusionPoint(
        String contig1, String contig2, long pos1, long pos2, int strand1, int strand2)
        implements Comparable<FusionPoint> {

    /**
     * Calculate distance between 2 fusion points, usable for clustering.
     *
     * @return -1 for non-comparable pairs.
     */
    public long dist(@NotNull FusionPoint other) {
        if (!Objects.equals(other.contig1, this.contig1)
                || !Objects.equals(other.contig2, this.contig2)) {
            return -1;
        } else {
            return Math.abs(this.pos1() - other.pos1()) + Math.abs(this.pos2() - other.pos2());
        }
    }

    @Contract("_ -> new")
    public static @NotNull FusionPoint centroid(@NotNull Collection<FusionPoint> fps) {
        if (fps.isEmpty()) {
            throw new IllegalArgumentException();
        }
        var firstFP = fps.stream().findFirst().orElseThrow();
        var contig1 = firstFP.contig1();
        var contig2 = firstFP.contig2();
        var strand1 = firstFP.strand1();
        var strand2 = firstFP.strand2();
        var pos1 = (int) fps.stream()
                .map(FusionPoint::pos1)
                .mapToLong(Long::longValue)
                .average()
                .orElseThrow();
        var pos2 = (int) fps.stream()
                .map(FusionPoint::pos2)
                .mapToLong(Long::longValue)
                .average()
                .orElseThrow();
        return new FusionPoint(contig1, contig2, pos1, pos2, strand1, strand2);
    }

    /**
     * Sort to (chrom1, chrom2, pos1, pos2) order.
     *
     * <p>Documentations from <a
     * href="https://pairtools.readthedocs.io/en/latest/sorting.html">pairtools</a>:
     *
     * <p><code>pairtools sort</code> arrange pairs in the order of (chrom1, chrom2, pos1, pos2).
     * This order is also known as block sorting, because all pairs between any given pair of
     * chromosomes become grouped into one continuous block. Additionally, <code>pairtools sort
     * </code> also sorts pairs with identical positions by pair_type. This does not really do much
     * for mapped reads, but it nicely splits unmapped reads into blocks of null-mapped and
     * multi-mapped reads.
     *
     * <p>We note that there is an alternative to block sorting, called row sorting, where pairs are
     * sorted by (chrom1, pos1, chrom2, pos2). In <code>pairtools sort</code>, we prefer
     * block-sorting since it cleanly separates cis interactions from trans ones and thus is a more
     * optimal solution for typical use cases.
     */
    @Override
    public int compareTo(@NotNull FusionPoint fusionPoint) {
        var tmpResult = this.contig1().compareTo((fusionPoint).contig1());
        if (tmpResult != 0) {
            return tmpResult;
        }
        tmpResult = this.contig2().compareTo((fusionPoint).contig2());
        if (tmpResult != 0) {
            return tmpResult;
        }
        tmpResult = Long.compare(this.pos1(), fusionPoint.pos1());
        if (tmpResult != 0) {
            return tmpResult;
        }
        return Long.compare(this.pos2(), fusionPoint.pos2());
    }

    /**
     * Flip to upper triangular.
     *
     * <p>Documentations from <a
     * href="https://pairtools.readthedocs.io/en/latest/sorting.html">pairtools</a>:
     *
     * <p>In a typical paired-end experiment, side1 and side2 of a DNA molecule are defined by the
     * order in which they got sequenced. Since this order is essentially random, any given Hi-C
     * pair, e.g. (chr1, 1.1Mb; chr2, 2.1Mb), may appear in a reversed orientation, i.e. (chr2,
     * 2.1Mb; chr1, 1.1Mb). If we were to preserve this order of sides, interactions between the
     * same loci would appear in two different locations of the sorted pair list, which would
     * complicate finding PCR/optical duplicates.
     *
     * <p>To ensure that Hi-C pairs with similar coordinates end up in the same location of the
     * sorted list, we flip pairs, i.e. we choose side1 as the side with the lowest genomic
     * coordinate. Thus, after flipping, for trans pairs (chrom1!=chrom2), order(chrom1) &lt;
     * order(chrom2); and for cis pairs (chrom1==chrom2), pos1 &lt; =pos2. In a matrix
     * representation, flipping is equal to reflecting the lower triangle of the Hi-C matrix onto
     * its upper triangle, such that the resulting matrix is upper-triangular.
     *
     * <p>In <code>pairtools</code>, flipping is done during parsing - that's why <code>
     * pairtools parse</code> requires a .chromsizes file that specifies the order of chromosomes
     * for flipping. Importantly, <code>pairtools parse</code> also flips one-sided pairs such that
     * side1 is always unmapped; and unmapped pairs such that side1 always has a “poorer” mapping
     * type (i.e. null-mapping &lt;multi-mapping).
     *
     * @return The upper triangular representation of current fusion point.
     */
    @Contract(" -> new")
    public @NotNull FusionPoint upperTriangular() {
        boolean swap = false;
        if (contig1.compareTo(contig2) > 0) {
            swap = true;
        } else if (contig1.equals(contig2)) {
            swap = pos1 > pos2;
        }
        if (swap) {
            return new FusionPoint(
                    this.contig2, this.contig1, this.pos2, this.pos1, this.strand2, this.strand1);
        } else {
            return new FusionPoint(
                    this.contig1, this.contig2, this.pos1, this.pos2, this.strand1, this.strand2);
        }
    }
}
