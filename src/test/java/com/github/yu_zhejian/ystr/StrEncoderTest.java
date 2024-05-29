package com.github.yu_zhejian.ystr;

import static org.junit.jupiter.api.Assertions.*;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.function.Function;

class StrEncoderTest {

    void assertEncoderDecoderPair(
            @NotNull String string,
            @NotNull Function<byte[], byte[]> encoder,
            @NotNull Function<byte[], byte[]> decoder) {
        byte[] expectedDecoded = string.getBytes(StandardCharsets.UTF_8);
        byte[] encoded = encoder.apply(string.getBytes(StandardCharsets.UTF_8));
        byte[] decoded = decoder.apply(encoded);
        assertArrayEquals(expectedDecoded, decoded);
    }

    @Test
    void simpleNucleotideEncoder() {
        assertEncoderDecoderPair(
                "AGCTagctAAAAGGGGCCCCTTTTNAGCTN",
                StrEncoder::simpleKmerEncoder,
                StrEncoder::simpleKmerDecoder);
        assertEncoderDecoderPair(
                "AGCTagctAAAAGGGGCCCCTTunnnuagttaTTN",
                StrEncoder::simpleKmerEncoder,
                StrEncoder::simpleKmerDecoder);
        assertEncoderDecoderPair("A", StrEncoder::simpleKmerEncoder, StrEncoder::simpleKmerDecoder);
        assertEncoderDecoderPair("", StrEncoder::simpleKmerEncoder, StrEncoder::simpleKmerDecoder);
    }

    @Test
    void simpleTwoBitEncoder() {
        assertArrayEquals(
                new byte[] {(byte) 0x9C},
                StrEncoder.simpleTwoBitEncoder("ACGT".getBytes(StandardCharsets.UTF_8)));
        assertArrayEquals(
                new byte[] {(byte) 0x80},
                StrEncoder.simpleTwoBitEncoder("A".getBytes(StandardCharsets.UTF_8)));
        assertArrayEquals(new byte[0], StrEncoder.simpleTwoBitEncoder(new byte[0]));
        assertArrayEquals(
                new byte[] {(byte) 0x9C, (byte) 0x80},
                StrEncoder.simpleTwoBitEncoder("ACGTA".getBytes(StandardCharsets.UTF_8)));
    }
}
