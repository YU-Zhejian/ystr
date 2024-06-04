package org.labw.tcr_annotator.falp4j;

import io.vavr.Tuple2;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractAligner implements AlignerInterface {

    protected final @NotNull AlnConf alnConf;
    protected final @NotNull String dnaSeq;
    protected final @NotNull String pepSeq;
    protected final @NotNull String dnaName;
    protected final @NotNull String pepName;
    protected final int dnaSeqLen;
    protected final int pepSeqLen;

    protected int calcScoreOnPos(int posOnAA, int posOnDNA) {
        if (posOnDNA < 0 || posOnAA < 0 || posOnAA >= pepSeqLen || posOnDNA + 3 > dnaSeqLen) {
            return 0;
        }
        return alnConf.scoreAASubst(
                alnConf.codonToAA(dnaSeq.substring(posOnDNA, posOnDNA + 3)),
                pepSeq.charAt(posOnAA));
    }

    public AbstractAligner(
            @NotNull AlnConf alnConf,
            @NotNull String dnaSeq,
            @NotNull String pepSeq,
            @NotNull String dnaName,
            @NotNull String pepName) {
        this.alnConf = alnConf;
        this.dnaSeq = dnaSeq;
        this.pepSeq = pepSeq;
        dnaSeqLen = dnaSeq.length();
        this.dnaName = dnaName;
        this.pepName = pepName;
        pepSeqLen = this.pepSeq.length();
    }

    public List<AlnSeg> tbackToAlnSegs(@NotNull List<Tuple2<Integer, Integer>> tBack) {

        var tbackIt = tBack.iterator();
        var prevTback = tbackIt.next();
        var alnSegs = new LinkedList<AlnSeg>();

        while (tbackIt.hasNext()) {
            var currentTback = tbackIt.next();
            int dnaStart = prevTback._2;
            var pepStart = prevTback._1;
            var codon = dnaSeq.substring(dnaStart, currentTback._2);
            var aa = pepSeq.substring(pepStart, currentTback._1);
            var alnSeg = new AlnSeg(
                    dnaStart,
                    pepStart,
                    codon,
                    codon.length() == 3 ? String.valueOf(alnConf.codonToAA(codon)) : "?",
                    aa);
            prevTback = currentTback;
            alnSegs.add(alnSeg);
        }
        // Add last match

        int dnaStart = prevTback._2;
        var pepStart = prevTback._1;
        var codon = dnaSeq.substring(dnaStart, dnaStart + 3);
        var aa = pepSeq.substring(pepStart, pepStart + 1);
        var alnSeg =
                new AlnSeg(dnaStart, pepStart, codon, String.valueOf(alnConf.codonToAA(codon)), aa);
        alnSegs.add(alnSeg);
        return alnSegs;
    }
}
