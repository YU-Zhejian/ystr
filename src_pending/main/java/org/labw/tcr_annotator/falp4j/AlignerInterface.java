package org.labw.tcr_annotator.falp4j;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface AlignerInterface {
    @NotNull
    AlnResult align();

    static @NotNull String mergeAlnResult(@NotNull List<AlnResult> alnResultList) {
        var gaps = new HashMap<Integer, Integer>();
        var individualGaps = new ArrayList<Map<Integer, Integer>>();
        var sb = new StringBuilder();
        var titleLen = 0;

        for (var alnResult : alnResultList) {
            var traces = alnResult.getTraces();
            var individualGap = new HashMap<Integer, Integer>();
            titleLen = Math.max(titleLen, alnResult.getTitle().length());
            int refDnaPos = 0;
            int gapLen = 0;
            int gapStart = 0;
            for (var nt : traces._1.toCharArray()) {
                if (nt == AlnConf.ALN_GAP) {
                    if (gapLen == 0) {
                        gapStart = refDnaPos;
                    }
                    gapLen += 1;
                } else {
                    refDnaPos += 1;
                    if (gapLen != 0) {
                        int finalGapLen = gapLen;
                        gaps.compute(
                                gapStart,
                                (_gapStart, _gapLen) -> _gapLen == null
                                        ? finalGapLen
                                        : Math.max(_gapLen, finalGapLen));
                        individualGap.put(gapStart, finalGapLen);
                        gapLen = 0;
                    }
                }
            }
            if (gapLen != 0) {
                int finalGapLen = gapLen;
                gaps.compute(
                        gapStart,
                        (_gapStart, _gapLen) ->
                                _gapLen == null ? finalGapLen : Math.max(_gapLen, finalGapLen));
                individualGap.put(gapStart, finalGapLen);
            }
            individualGaps.add(individualGap);
        }
        var gaplessDnaSeq = alnResultList.get(0).getTraces()._1.replace("-", "");
        var finalDnaSb = new StringBuilder();
        for (var posOnGaplessDna = 0; posOnGaplessDna < gaplessDnaSeq.length(); posOnGaplessDna++) {
            finalDnaSb.append(gaplessDnaSeq.charAt(posOnGaplessDna));
            if (gaps.containsKey(posOnGaplessDna)) {
                finalDnaSb.append(
                        String.valueOf(AlnConf.ALN_GAP).repeat(gaps.get(posOnGaplessDna)));
            }
        }
        var finalDna = finalDnaSb.toString();
        sb.append(String.format("%1$" + titleLen + "s", alnResultList.get(0).dnaName()));
        sb.append("|");
        sb.append(finalDna);
        sb.append("|\n");
        var alnId = 0;
        for (var alnResult : alnResultList) {
            var diffGap = new HashMap<Integer, Integer>();
            var individualGap = individualGaps.get(alnId);
            for (var gap : gaps.entrySet()) {
                diffGap.put(
                        gap.getKey(), gap.getValue() - individualGap.getOrDefault(gap.getKey(), 0));
            }
            var traces = alnResult.getTraces();
            var curDna = traces._1;
            for (var trace : List.of(traces._2, traces._3, traces._4)) {
                sb.append(String.format("%1$" + titleLen + "s", alnResult.getTitle()));
                sb.append("|");
                var posOnCurDna = 0;
                var posOnGaplessDna = 0;
                var isInUtr = true;
                var curGapLen = diffGap.getOrDefault(0, 0);
                while (posOnGaplessDna < gaplessDnaSeq.length() || posOnCurDna < curDna.length()) {
                    while (curGapLen > 0) {
                        sb.append(isInUtr ? AlnConf.ALN_BLANK : AlnConf.ALN_GAP);
                        curGapLen -= 1;
                    }
                    var curChar = curDna.charAt(posOnCurDna);
                    if (curChar != AlnConf.ALN_GAP) {
                        posOnGaplessDna += 1;
                    }
                    var traceChar = trace.charAt(posOnCurDna);
                    isInUtr = traceChar == AlnConf.ALN_BLANK;
                    sb.append(traceChar);
                    posOnCurDna += 1;
                    curGapLen = diffGap.getOrDefault(posOnGaplessDna, 0);
                }
                sb.append("|\n");
            }
            sb.append(" ".repeat(titleLen));
            sb.append("|");
            sb.append(" ".repeat(finalDna.length()));
            sb.append("|\n");
            alnId += 1;
        }
        return sb.toString();
    }
}
