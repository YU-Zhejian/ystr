package org.labw.tcr_annotator.falp4j;

import io.vavr.Tuple4;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/** A nucleotide-to-protein alignment segment. */
public record AlnSeg(int dnaStart, int pepStart, String dnaCodon, String translAA, String refAA) {

    public int dnaEnd() {
        return dnaStart + dnaCodon.length();
    }

    public int pepEnd() {
        return pepStart + refAA.length();
    }

    public MutType mutation() {
        if (Objects.equals(translAA, refAA)) {
            return MutType.NULL;
        }
        if (Objects.equals(translAA, AlnConf.STOP_CODON_STR)) {
            return MutType.NONESENSE;
        }
        if (dnaCodon.length() == 4 || dnaCodon.length() == 2) {
            return MutType.FRAME_SHIFT;
        }
        if (dnaCodon.isEmpty()) {
            return MutType.DEL;
        }
        if (refAA.isEmpty()) {
            return MutType.INS;
        }
        return MutType.MISSENSE;
    }

    @Contract(" -> new")
    public @NotNull Tuple4<String, String, String, String> getTraces() {
        var dnaSeqTrace = new StringBuilder();
        var translPepTrace = new StringBuilder();
        var alnStatTrace = new StringBuilder();
        var pepSeqTrace = new StringBuilder();
        if (dnaCodon.isEmpty()) {
            // This means a deletion on the DNA side
            for (var i = 0; i < 3; i++) {
                dnaSeqTrace.append(AlnConf.ALN_GAP);
                translPepTrace.append(AlnConf.ALN_GAP);
                alnStatTrace.append(AlnConf.ALN_DEL);
            }
            pepSeqTrace.append(refAA);
            pepSeqTrace.append(AlnConf.AA_PLACEHOLDER);
            pepSeqTrace.append(AlnConf.AA_PLACEHOLDER);
        } else if (dnaCodon.length() == 3) {
            dnaSeqTrace.append(dnaCodon);
            if (refAA.isEmpty()) {
                pepSeqTrace.append(String.valueOf(AlnConf.ALN_GAP).repeat(3));
            } else {
                pepSeqTrace.append(refAA);
                pepSeqTrace.append(String.valueOf(AlnConf.AA_PLACEHOLDER).repeat(2));
            }
            translPepTrace.append(translAA);
            translPepTrace.append(String.valueOf(AlnConf.AA_PLACEHOLDER).repeat(2));
            if (refAA.isEmpty()) {
                alnStatTrace.append(String.valueOf(AlnConf.ALN_INS).repeat(3));
            } else {
                alnStatTrace.append(String.valueOf(
                                refAA.equals(translAA) ? AlnConf.ALN_MATCH : AlnConf.ALN_MIS_MATCH)
                        .repeat(3));
            }
        } else {
            dnaSeqTrace.append(dnaCodon);
            if (refAA.isEmpty()) {
                pepSeqTrace.append(String.valueOf(AlnConf.ALN_GAP).repeat(dnaCodon.length()));
            } else {
                pepSeqTrace.append(refAA);
                pepSeqTrace.append(String.valueOf(AlnConf.AA_PLACEHOLDER)
                        .repeat(Math.max(0, dnaCodon.length() - refAA.length())));
            }
            alnStatTrace.append(String.valueOf(AlnConf.ERR_CODON).repeat(dnaCodon.length()));
            translPepTrace.append(AlnConf.ERR_CODON);
            translPepTrace.append(
                    String.valueOf(AlnConf.AA_PLACEHOLDER).repeat(dnaCodon.length() - 1));
        }
        return new Tuple4<>(
                dnaSeqTrace.toString(),
                translPepTrace.toString(),
                alnStatTrace.toString(),
                pepSeqTrace.toString());
    }
}
