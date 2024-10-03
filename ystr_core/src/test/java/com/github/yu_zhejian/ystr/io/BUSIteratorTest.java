package com.github.yu_zhejian.ystr.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import com.github.yu_zhejian.ystr.test_utils.GitUtils;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

class BUSIteratorTest {
    @Test
    void test() throws IOException {
        var actualResults = new ObjectArrayList<String>();
        var expectedResults = new ObjectArrayList<String>();
        try (var parser = BUSIterator.read(
                Path.of(GitUtils.getGitRoot(), "test", "small", "test_bus", "output.s.c.bus"))) {
            var bh = parser.getHeader();
            assertEquals(new BUSHeader(1, 16, 10, "BUS file produced by kallisto"), bh);
            assertEquals(bh.tlen(), bh.text().length());
            while (parser.hasNext()) {
                actualResults.add(parser.next().toString().trim());
            }
        }
        try (var parser = new BufferedReader(new FileReader(
                Path.of(GitUtils.getGitRoot(), "test", "small", "test_bus", "output.s.c.bus.tsv")
                        .toFile(),
                StandardCharsets.US_ASCII))) {
            String line;
            while ((line = parser.readLine()) != null) {
                expectedResults.add(line.trim());
            }
        }
        assertIterableEquals(expectedResults, actualResults);
    }
}
