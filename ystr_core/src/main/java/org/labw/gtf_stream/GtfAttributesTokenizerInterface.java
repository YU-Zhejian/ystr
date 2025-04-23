package org.labw.gtf_stream;

import java.util.List;

public interface GtfAttributesTokenizerInterface {

    record GtfAttributesTokens(List<String> keys, List<String> values) {}

    /**
     * Parse the GTF attribute string into a list of keys and another list of values.
     *
     * @param s As described.
     * @return As described.
     * @throws GtfParserException As described.
     */
    GtfAttributesTokens parse(String s) throws GtfParserException;
}
