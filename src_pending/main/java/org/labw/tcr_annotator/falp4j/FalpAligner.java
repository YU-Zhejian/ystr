package org.labw.tcr_annotator.falp4j;

import io.vavr.Tuple2;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Representation Notice: (1) In any time Amino Acid is before DNA, which is not the same as is in
 * the article.
 *
 * <p>See: <a href="https://doi.org/10.1093/bioinformatics/btu576">this article</a>.
 */
public final class FalpAligner extends AbstractAligner {
    private final Integer[][] substScore;
    private final Integer[][] insScore;
    private final Integer[][] delScore;

    public FalpAligner(
            @NotNull AlnConf alnConf,
            @NotNull String dnaSeq,
            @NotNull String pepSeq,
            @NotNull String dnaName,
            @NotNull String pepName) {
        super(alnConf, dnaSeq, pepSeq, dnaName, pepName);

        substScore = new Integer[pepSeqLen + 1][dnaSeqLen + 4];
        insScore = new Integer[pepSeqLen + 1][dnaSeqLen + 4];
        delScore = new Integer[pepSeqLen + 1][dnaSeqLen + 4];
    }

    /** On deletion of 1 AA. */
    private int calculateInsScore(int posOnAA, int posOnDNA) {
        if (posOnDNA < 0 || posOnAA < 0) {
            return 0;
        }

        if (insScore[posOnAA][posOnDNA] != null) {
            return insScore[posOnAA][posOnDNA];
        }
        var retv = max(
                0,
                max(
                                calculateSubstScore(posOnAA - 1, posOnDNA) - alnConf.gapOpenPenalty,
                                calculateInsScore(posOnAA - 1, posOnDNA))
                        - alnConf.gapExtendPenalty);
        insScore[posOnAA][posOnDNA] = retv;
        return retv;
    }

    /** On deletion of 1 dna codon. */
    private int calculateDelScore(int posOnAA, int posOnDNA) {
        if (posOnDNA < 0 || posOnAA < 0) {
            return 0;
        }

        if (delScore[posOnAA][posOnDNA] != null) {
            return delScore[posOnAA][posOnDNA];
        }
        var retv = max(max(
                        max(
                                calculateSubstScore(posOnAA, posOnDNA - 3) - alnConf.gapOpenPenalty,
                                calculateDelScore(posOnAA, posOnDNA - 3)),
                        max(
                                        calculateSubstScore(posOnAA, posOnDNA - 2)
                                                - alnConf.gapOpenPenalty,
                                        calculateDelScore(posOnAA, posOnDNA - 2),
                                        calculateSubstScore(posOnAA, posOnDNA - 4)
                                                - alnConf.gapOpenPenalty,
                                        calculateDelScore(posOnAA, posOnDNA - 4))
                                - alnConf.frameshiftPenalty)
                - alnConf.gapExtendPenalty);
        delScore[posOnAA][posOnDNA] = retv;
        return retv;
    }

    private int max(int... ints) {
        return Arrays.stream(ints).max().orElseThrow();
    }

    private int calculateSubstScore(int posOnAA, int posOnDNA) {
        if (posOnDNA < 0 || posOnAA < 0) {
            return 0;
        }
        if (substScore[posOnAA][posOnDNA] != null) {
            return substScore[posOnAA][posOnDNA];
        }
        var retv = max(
                0,
                max(
                                max(
                                        calculateSubstScore(posOnAA - 1, posOnDNA - 3),
                                        calculateInsScore(posOnAA - 1, posOnDNA - 3),
                                        calculateDelScore(posOnAA - 1, posOnDNA - 3)),
                                max(
                                                calculateSubstScore(posOnAA - 1, posOnDNA - 2),
                                                calculateInsScore(posOnAA - 1, posOnDNA - 2),
                                                calculateDelScore(posOnAA - 1, posOnDNA - 2),
                                                calculateSubstScore(posOnAA - 1, posOnDNA - 4),
                                                calculateInsScore(posOnAA - 1, posOnDNA - 4),
                                                calculateDelScore(posOnAA - 1, posOnDNA - 4))
                                        - alnConf.frameshiftPenalty)
                        + calcScoreOnPos(
                                posOnAA, posOnDNA)); // Alignment status of the current spot.
        substScore[posOnAA][posOnDNA] = retv;
        return retv;
    }

    public @NotNull String getAlnGraphStr() {
        var sb = new StringBuilder();
        sb.append("0" + "\t");
        for (var dna : dnaSeq.toCharArray()) {
            sb.append(String.format("%1$" + 4 + "s", dna)).append("\t");
        }
        sb.append("\n");
        for (var i = 0; i < substScore.length; i++) {
            if (i < pepSeqLen) {
                sb.append(pepSeq.charAt(i)).append("\t");
            } else {
                sb.append(" \t");
            }
            for (var j : substScore[i]) {
                sb.append(String.format("%1$" + 4 + "s", j)).append("\t");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public @NotNull AlnResult align() {
        calculateSubstScore(pepSeqLen, dnaSeqLen - 1);
        var maxAAPos = 0;
        var maxDNAPos = 0;
        var maxScore = 0;
        for (var i = 0; i < pepSeqLen; i++) {
            for (var j = 0; j < dnaSeqLen; j++) {
                if (substScore[i][j] != null && substScore[i][j] >= maxScore) {
                    maxScore = substScore[i][j];
                    maxDNAPos = j;
                    maxAAPos = i;
                }
            }
        }

        // tback
        var tBack = new LinkedList<Tuple2<Integer, Integer>>();
        var aaPos = maxAAPos;
        var dnaPos = maxDNAPos;
        var pos = new Tuple2<>(aaPos, dnaPos);
        // Whether a position is inside the matrix.
        Predicate<Tuple2<Integer, Integer>> isInside = tup -> tup._1 >= 0 && tup._2 >= 0;
        while (pos._1 >= 0 && pos._2 >= 0) {
            tBack.addFirst(pos);
            var possiblePrevPos = Stream.of(
                            new Tuple2<>(pos._1 - 1, pos._2 - 3), // Match
                            new Tuple2<>(pos._1, pos._2 - 3), // AA del
                            new Tuple2<>(pos._1 - 1, pos._2), // AA ins
                            new Tuple2<>(pos._1 - 1, pos._2 - 2), // Frame Shift
                            new Tuple2<>(pos._1 - 1, pos._2 - 4) // Frame Shift
                            )
                    .filter(isInside)
                    .toList();
            var nextMaxScore = -Integer.MAX_VALUE;
            Tuple2<Integer, Integer> maxPos = new Tuple2<>(0, 0);
            for (var possiblePos : possiblePrevPos) {
                // Disable NT insertion before first AA.
                if (pos._1 == 0 && possiblePos._1 == 0) {
                    continue;
                }
                var score = substScore[possiblePos._1][possiblePos._2];
                if (score != null && score > nextMaxScore) {
                    nextMaxScore = score;
                    maxPos = possiblePos;
                }
            }
            pos = maxPos;
            if (nextMaxScore < alnConf.initiationDropoff) {
                break;
            }
        }

        var alnSegs = new LinkedList<>(tbackToAlnSegs(tBack));

        return new AlnResult(
                alnSegs,
                maxScore,
                alnSegs.getFirst().dnaStart(),
                alnSegs.getLast().dnaEnd(),
                alnSegs.getFirst().pepStart(),
                alnSegs.getLast().pepEnd(),
                dnaName,
                pepName,
                dnaSeqLen,
                pepSeqLen,
                dnaSeq.substring(0, alnSegs.getFirst().dnaStart()),
                dnaSeq.substring(alnSegs.getLast().dnaEnd()));
    }
}
