package org.labw.gtf_stream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Read GTF from a {@link Reader} to Stream of {@link GtfRecord}. Lazy-evaluated, parallelized, and
 * not interruptable by bugs.
 */
public class GtfIteratorReader {
    /** Disabled constructor. */
    private GtfIteratorReader() {}

    /**
     * See {@link GtfIteratorReader}.
     *
     * @param reader As described.
     * @param gtfReaderConfig As described.
     * @return As described.
     */
    public static Stream<GtfRecord> read(Reader reader, GtfReaderConfig gtfReaderConfig)
            throws IOException {
        var breader = new BufferedReader(reader);
        return breader.lines()
                .map(String::strip)
                .filter(x -> !x.isEmpty() && (x.charAt(0) != '#'))
                .map(x -> {
                    try {
                        return GtfRecord.parse(
                                x,
                                new GtfAttributesTokenizerWithFSA(),
                                new GtfAttributesParser(gtfReaderConfig));
                    } catch (GtfParserException ignored) {
                        return null;
                    }
                })
                .filter(Objects::nonNull);
    }
}
