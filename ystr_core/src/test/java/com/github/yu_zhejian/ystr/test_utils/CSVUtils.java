package com.github.yu_zhejian.ystr.test_utils;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.DuplicateHeaderMode;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/** Some Apache CSV wrapper. */
public final class CSVUtils {
    public static final CSVFormat CSV_FMT = CSVFormat.Builder.create()
            .setDelimiter('\t')
            .setQuote('\'')
            .setRecordSeparator('\n')
            .setDuplicateHeaderMode(DuplicateHeaderMode.DISALLOW)
            .get();

    private CSVUtils() {}

    /**
     * Create a labw_utils style {@link CSVPrinter} with tab as delimiter, single quote and
     * UIX-style line separator.
     *
     * @param file File to be written to.
     * @return As described.
     * @throws IOException On filesystem errors.
     */
    @Contract("_ -> new")
    public static @NotNull CSVPrinter createCSVPrinter(File file) throws IOException {
        OutputStream ios;
        FileOutputStream fStream;
        fStream = new FileOutputStream(file);
        ios = new BufferedOutputStream(fStream);
        return new CSVPrinter(new OutputStreamWriter(ios, StandardCharsets.UTF_8), CSV_FMT);
    }
}
