package org.labw.tcr_annotator.falp4j;

import org.jetbrains.annotations.NotNull;

public class TrivialTranslator {
    private final AlnConf alnConf;

    public TrivialTranslator(AlnConf alnConf) {
        this.alnConf = alnConf;
    }

    public String translate(@NotNull String dna) {
        var sb = new StringBuilder();
        var startOffset = 0;
        for (var i = 0; i < dna.length() - 3; i++) {
            var codonDNA = dna.substring(i, i + 3);
            if (alnConf.isStart(codonDNA)) {
                sb.append(alnConf.codonToAA(codonDNA));
                startOffset = i + 3;
                break;
            }
        }
        if (sb.isEmpty()) {
            // Start codon not found
            return "";
        }
        while (startOffset < dna.length()) {
            var codonDNA = dna.substring(startOffset, startOffset + 3);
            var aa = alnConf.codonToAA(codonDNA);
            sb.append(aa);
            if (aa == AlnConf.STOP_CODON) {
                return sb.toString();
            }
            startOffset += 3;
        }
        return sb.toString();
    }
}
