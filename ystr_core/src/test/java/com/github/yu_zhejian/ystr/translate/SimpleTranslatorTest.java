package com.github.yu_zhejian.ystr.translate;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

class SimpleTranslatorTest {

    @Test
    void translate() {
        var translator =
                Codons.getTranslator(1, new byte[][] {"ATG".getBytes(StandardCharsets.US_ASCII)});
        assertArrayEquals(
                "FDTGTTK".getBytes(StandardCharsets.US_ASCII),
                translator.translate(
                        "ttcgatacaggaactacaaaa".getBytes(StandardCharsets.US_ASCII), 0, 21));
        assertArrayEquals(
                "FDTGTT".getBytes(StandardCharsets.US_ASCII),
                translator.translate(
                        "ttcgatacaggaactacaaaa".getBytes(StandardCharsets.US_ASCII), 0, 20));
        assertArrayEquals(
                "SIQELQ".getBytes(StandardCharsets.US_ASCII),
                translator.translate(
                        "ttcgatacaggaactacaaaa".getBytes(StandardCharsets.US_ASCII), 1, 21));
        assertArrayEquals(
                "RYRNYK".getBytes(StandardCharsets.US_ASCII),
                translator.translate(
                        "ttcgatacaggaactacaaaa".getBytes(StandardCharsets.US_ASCII), 2, 21));
    }
}
