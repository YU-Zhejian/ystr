package com.github.yu_zhejian.ystr.translate;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

/**
 * NCBI codon tables.
 *
 * @see <a href="https://www.ncbi.nlm.nih.gov/Taxonomy/Utils/wprintgc.cgi">Data source</a>
 */
public final class Codons {
    private Codons() {}

    /** The codon table. */
    static final byte[][] NCBI_CODON_TABLE = {
        /* 0 */ null,
        /* 1 */ "FFLLSSSSYY**CC*WLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG"
                .getBytes(StandardCharsets.US_ASCII),
        /* 2 */ "FFLLSSSSYY**CCWWLLLLPPPPHHQQRRRRIIMMTTTTNNKKSS**VVVVAAAADDEEGGGG"
                .getBytes(StandardCharsets.US_ASCII),
        /* 3 */ "FFLLSSSSYY**CCWWTTTTPPPPHHQQRRRRIIMMTTTTNNKKSSRRVVVVAAAADDEEGGGG"
                .getBytes(StandardCharsets.US_ASCII),
        /* 4 */ "FFLLSSSSYY**CCWWLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG"
                .getBytes(StandardCharsets.US_ASCII),
        /* 5 */ "FFLLSSSSYY**CCWWLLLLPPPPHHQQRRRRIIMMTTTTNNKKSSSSVVVVAAAADDEEGGGG"
                .getBytes(StandardCharsets.US_ASCII),
        /* 6 */ "FFLLSSSSYYQQCC*WLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG"
                .getBytes(StandardCharsets.US_ASCII),
        /* 7 */ null,
        /* 8 */ null,
        /* 9 */ "FFLLSSSSYY**CCWWLLLLPPPPHHQQRRRRIIIMTTTTNNNKSSSSVVVVAAAADDEEGGGG"
                .getBytes(StandardCharsets.US_ASCII),
        /* 10 */ "FFLLSSSSYY**CCCWLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG"
                .getBytes(StandardCharsets.US_ASCII),
        /* 11 */ "FFLLSSSSYY**CC*WLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG"
                .getBytes(StandardCharsets.US_ASCII),
        /* 12 */ "FFLLSSSSYY**CC*WLLLSPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG"
                .getBytes(StandardCharsets.US_ASCII),
        /* 13 */ "FFLLSSSSYY**CCWWLLLLPPPPHHQQRRRRIIMMTTTTNNKKSSGGVVVVAAAADDEEGGGG"
                .getBytes(StandardCharsets.US_ASCII),
        /* 14 */ "FFLLSSSSYYY*CCWWLLLLPPPPHHQQRRRRIIIMTTTTNNNKSSSSVVVVAAAADDEEGGGG"
                .getBytes(StandardCharsets.US_ASCII),
        /* 15 */ "FFLLSSSSYY*QCC*WLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG"
                .getBytes(StandardCharsets.US_ASCII),
        /* 16 */ "FFLLSSSSYY*LCC*WLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG"
                .getBytes(StandardCharsets.US_ASCII),
        /* 17 */ null,
        /* 18 */ null,
        /* 19 */ null,
        /* 20 */ null,
        /* 21 */ "FFLLSSSSYY**CCWWLLLLPPPPHHQQRRRRIIMMTTTTNNNKSSSSVVVVAAAADDEEGGGG"
                .getBytes(StandardCharsets.US_ASCII),
        /* 22 */ "FFLLSS*SYY*LCC*WLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG"
                .getBytes(StandardCharsets.US_ASCII),
        /* 23 */ "FF*LSSSSYY**CC*WLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG"
                .getBytes(StandardCharsets.US_ASCII),
        /* 24 */ "FFLLSSSSYY**CCWWLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSSKVVVVAAAADDEEGGGG"
                .getBytes(StandardCharsets.US_ASCII),
        /* 25 */ "FFLLSSSSYY**CCGWLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG"
                .getBytes(StandardCharsets.US_ASCII),
        /* 26 */ "FFLLSSSSYY**CC*WLLLAPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG"
                .getBytes(StandardCharsets.US_ASCII),
        /* 27 */ "FFLLSSSSYYQQCCWWLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG"
                .getBytes(StandardCharsets.US_ASCII),
        /* 28 */ "FFLLSSSSYYQQCCWWLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG"
                .getBytes(StandardCharsets.US_ASCII),
        /* 29 */ "FFLLSSSSYYYYCC*WLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG"
                .getBytes(StandardCharsets.US_ASCII),
        /* 30 */ "FFLLSSSSYYEECC*WLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG"
                .getBytes(StandardCharsets.US_ASCII),
        /* 31 */ "FFLLSSSSYYEECCWWLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG"
                .getBytes(StandardCharsets.US_ASCII),
        /* 32 */ null,
        /* 33 */ "FFLLSSSSYYY*CCWWLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSSKVVVVAAAADDEEGGGG"
                .getBytes(StandardCharsets.US_ASCII)
    };

    /**
     * Create {@link SimpleTranslator} using desired codon table.
     *
     * @param ncbiCodonIndex As described.
     * @param startCodons As described.
     * @return As described.
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull SimpleTranslator getTranslator(
            int ncbiCodonIndex, final byte[][] startCodons) {
        return new SimpleTranslator(NCBI_CODON_TABLE[ncbiCodonIndex], startCodons);
    }
}
