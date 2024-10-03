package com.github.yu_zhejian.ystr.translate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CodonsTest {
    @Test
    void test() {
        assertEquals(34, Codons.NCBI_CODON_NAMES.size());
        assertEquals(34, Codons.NCBI_CODON_TABLE.size());
    }
}
