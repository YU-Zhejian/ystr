package org.labw.gtf_stream;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class GtfAttributesParser {
    private final GtfReaderConfig gtfReaderConfig;

    /**
     * Default constructor.
     *
     * @param gtfReaderConfig As described.
     */
    GtfAttributesParser(final GtfReaderConfig gtfReaderConfig) {
        this.gtfReaderConfig = gtfReaderConfig;
    }

    /**
     * Parsed GTF attributes.
     *
     * @param attributes See {@link GtfRecord#attributes()}.
     * @param attributesMulti See {@link GtfRecord#attributesMulti()}.
     */
    public record AttributeParseResult(
            Map<String, String> attributes, Map<String, List<String>> attributesMulti) {}

    /**
     * Helper method which converts result from {@link GtfAttributesTokenizerInterface} to
     * {@link AttributeParseResult}.
     *
     * @return As described.
     */
    public @NotNull AttributeParseResult parse(
            @NotNull GtfAttributesTokenizerInterface.GtfAttributesTokens gtfAttributesTokens) {
        var attributes = new HashMap<String, String>();
        var attributesMulti = new HashMap<String, List<String>>(4);
        for (var i = 0; i < gtfAttributesTokens.keys().size(); i++) {
            var currentKey = gtfAttributesTokens.keys().get(i);
            var currentValue = gtfAttributesTokens.values().get(i);
            if (!this.gtfReaderConfig.attributeNamesToKeepContains(currentKey)) {
                continue;
            }
            currentValue = this.gtfReaderConfig.convert(currentKey, currentValue);
            if (attributes.containsKey(currentKey)) {
                attributesMulti.put(
                        currentKey,
                        new ArrayList<>(List.of(attributes.get(currentKey), currentValue)));
                attributes.remove(currentKey);
            } else {
                attributes.put(currentKey, currentValue);
            }
        }
        return new AttributeParseResult(attributes, attributesMulti);
    }
}
