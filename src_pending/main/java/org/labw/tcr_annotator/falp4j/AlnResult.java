package org.labw.tcr_annotator.falp4j;

import io.vavr.Tuple4;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record AlnResult(
        List<AlnSeg> segments,
        double alnScore,
        int alnDnaStart,
        int alnDnaEnd,
        int alnPepStart,
        int alnPepEnd,
        String dnaName,
        String pepName,
        int dnaLen,
        int pepLen,
        String fivePrimeUtr,
        String threePrimeUtr) {

    public String getTitle() {
        return "%s:%d-%d(%d) vs. %s:%d-%d(%d) (%f)"
                .formatted(
                        dnaName,
                        alnDnaStart,
                        alnDnaEnd,
                        dnaLen,
                        pepName,
                        alnPepStart,
                        alnPepEnd,
                        pepLen,
                        alnScore);
    }

    public @NotNull String toString() {
        var traces = getTraces();
        var rets = new StringBuilder().append(getTitle()).append("\n");
        traces.toSeq().asJava().forEach(it -> rets.append(it).append("\n"));
        rets.append("//\n");
        return rets.toString();
    }

    @Contract(" -> new")
    public @NotNull Tuple4<String, String, String, String> getTraces() {
        var dnaSeqTrace = new StringBuilder();
        var translPepTrace = new StringBuilder();
        var alnStatTrace = new StringBuilder();
        var pepSeqTrace = new StringBuilder();
        // 5' UTR
        dnaSeqTrace.append(fivePrimeUtr);
        for (var i = 0; i < alnDnaStart; i++) {
            translPepTrace.append(AlnConf.ALN_BLANK);
            alnStatTrace.append(AlnConf.ALN_BLANK);
            pepSeqTrace.append(AlnConf.ALN_BLANK);
        }
        for (var alnSeg : segments) {
            var traces = alnSeg.getTraces();

            dnaSeqTrace.append(traces._1);
            translPepTrace.append(traces._2);
            alnStatTrace.append(traces._3);
            pepSeqTrace.append(traces._4);
        }
        // 3' UTR
        dnaSeqTrace.append(threePrimeUtr);
        for (var i = segments.get(segments.size() - 1).dnaEnd(); i < dnaLen; i++) {
            translPepTrace.append(AlnConf.ALN_BLANK);
            alnStatTrace.append(AlnConf.ALN_BLANK);
            pepSeqTrace.append(AlnConf.ALN_BLANK);
        }
        return new Tuple4<>(
                dnaSeqTrace.toString(),
                translPepTrace.toString(),
                alnStatTrace.toString(),
                pepSeqTrace.toString());
    }
}
