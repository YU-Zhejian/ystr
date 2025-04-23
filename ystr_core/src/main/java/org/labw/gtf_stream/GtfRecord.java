package org.labw.gtf_stream;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.labw.libinterval.GenomicIntervalInterface;
import org.labw.libinterval.GenomicSimpleInterval;
import org.labw.libinterval.StrandUtils;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * A canonical GTF record for data class.
 *
 * @param seqname name of the chromosome or scaffold; chromosome names can be given with or without
 *     the 'chr' prefix. Important note: the seqname must be one used within Ensembl, i.e. a
 *     standard chromosome name or an Ensembl identifier such as a scaffold ID, without any
 *     additional content such as species or assembly. See the example GFF output below.
 * @param source name of the program that generated this feature, or the data source (database or
 *     project name)
 * @param feature feature type name, e.g. Gene, Variation, Similarity
 * @param start Start position* of the feature, with sequence numbering starting at 1.
 * @param end End position of the feature, with sequence numbering starting at 1.
 * @param score A floating point value.
 * @param strand See {@link StrandUtils#strandRepr}
 * @param frame One of '0', '1' or '2'. '0' indicates that the first base of the feature is the
 *     first base of a codon, '1' that the second base is the first base of a codon, and so on.
 * @param attributes A semicolon-separated list of tag-value pairs, providing additional information
 *     about each feature.
 * @param attributesMulti Attributes with multiple values.
 */
public record GtfRecord(
        String seqname,
        String source,
        String feature,
        long start,
        long end,
        @Nullable Double score,
        int strand,
        @Nullable Integer frame,
        Map<String, String> attributes,
        Map<String, List<String>> attributesMulti) {
    /**
     * Convert current GTF record to {@link GenomicIntervalInterface}. Note that the starting and
     * ending conventions will be adapted.
     *
     * @return As described.
     */
    @Contract(" -> new")
    public @NotNull GenomicIntervalInterface toGenomicInterval() {
        return new GenomicSimpleInterval(this.seqname, this.start - 1, this.end, this.strand);
    }

    /**
     * Parse current GTF line to GTF record.
     *
     * @param l As described.
     * @param gtfAttributesTokenizer As described.
     * @param gtfAttributesParser As described.
     * @return As described.
     * @throws GtfParserException As described.
     */
    @Contract("_, _, _ -> new")
    public static @NotNull GtfRecord parse(
            @NotNull String l,
            GtfAttributesTokenizerInterface gtfAttributesTokenizer,
            GtfAttributesParser gtfAttributesParser)
            throws GtfParserException {
        String seqname;
        String source;
        String feature;
        long start;
        long end;
        Double score;
        int strand;
        Integer frame;
        String attributesString;
        var lsplit = l.split("\t");
        if (lsplit.length != 9) {
            throw new GtfParserException("Should have 9 fields");
        }
        seqname = lsplit[0];
        source = lsplit[1];
        feature = lsplit[2];
        start = Long.parseLong(lsplit[3], 10);
        end = Long.parseLong(lsplit[4], 10);
        var scoreStr = lsplit[5];
        if (".".equals(scoreStr)) {
            score = null;
        } else {
            score = Double.parseDouble(scoreStr);
        }
        var strandStr = lsplit[6];
        strand = StrandUtils.strandStrToInt(strandStr);
        var frameStr = lsplit[7];
        if (".".equals(frameStr)) {
            frame = null;
        } else {
            frame = Integer.parseInt(frameStr, 10);
        }
        attributesString = lsplit[8];
        var attributesTokens = gtfAttributesTokenizer.parse(attributesString);
        var parsedAttributes = gtfAttributesParser.parse(attributesTokens);
        return new GtfRecord(
                seqname,
                source,
                feature,
                start,
                end,
                score,
                strand,
                frame,
                parsedAttributes.attributes(),
                parsedAttributes.attributesMulti());
    }

    public String toString() {
        var attrStr = getAttrStr();

        var sj = new StringJoiner("\t");
        sj.add(seqname);
        sj.add(source);
        sj.add(feature);
        sj.add(Long.toString(start));
        sj.add(Long.toString(end));
        sj.add(score == null ? "." : Double.toString(score));
        sj.add(StrandUtils.strandRepr(strand));
        sj.add(frame == null ? "." : Integer.toString(frame));
        sj.add(attrStr);
        return sj.toString();
    }

    @NotNull
    private String getAttrStr() {
        var sb = new StringBuilder();
        for (var attrKV : attributes.entrySet()) {
            sb.append(attrKV.getKey());
            sb.append(" \"");
            sb.append(attrKV.getValue());
            sb.append("\"; ");
        }
        for (var attrKV : attributesMulti.entrySet()) {
            for (var value : attrKV.getValue()) {
                sb.append(attrKV.getKey());
                sb.append(" \"");
                sb.append(value);
                sb.append("\"; ");
            }
        }
        if (!sb.isEmpty()) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public String getAttribute(String attrName, String defaultValue) {
        return this.attributes.getOrDefault(attrName, defaultValue);
    }

    public List<String> getAttributeMulti(String attrName, List<String> defaultValue) {
        return this.attributesMulti.getOrDefault(attrName, defaultValue);
    }
}
