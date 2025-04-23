package org.labw.gtf_stream;

import java.util.Collection;
import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * Configurations for {@link GtfIteratorReader}.
 *
 * @param attributeConverter A hashmap whose keys are keys in GTF attribute with values as
 *     converters. A converter is a function that accepts and returns String.
 * @param attributeNamesToKeep Attribute names to keep to reduce memory footprint.
 */
public record GtfReaderConfig(
        Map<String, UnaryOperator<String>> attributeConverter,
        Collection<String> attributeNamesToKeep) {

    /**
     * Detect whether the current key is allowed. Will return true if {@link #attributeNamesToKeep}
     * is empty.
     *
     * @param currentKey As described.
     * @return As described.
     */
    public boolean attributeNamesToKeepContains(String currentKey) {
        if (attributeNamesToKeep.isEmpty()) {
            return true;
        }
        return this.attributeNamesToKeep.contains(currentKey);
    }

    /**
     * Apply the converter to GTF key. If there's no converter registered, will do nothing.
     *
     * @param currentKey As described.
     * @param currentValue As described.
     * @return As described.
     */
    public String convert(String currentKey, String currentValue) {
        var converter = this.attributeConverter.getOrDefault(currentKey, null);
        if (converter != null) {
            return converter.apply(currentValue);
        } else {
            return currentValue;
        }
    }
}
