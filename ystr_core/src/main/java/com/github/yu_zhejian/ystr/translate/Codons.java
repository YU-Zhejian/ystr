package com.github.yu_zhejian.ystr.translate;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * NCBI codon tables.
 *
 * @see <a href="https://www.ncbi.nlm.nih.gov/Taxonomy/Utils/wprintgc.cgi">Data source</a>
 */
public final class Codons {
    /** Defunct Constructor **/
    private Codons() {}

    public static final CodonRecord NOP_CODONS =
            new CodonRecord("X".repeat(64), StandardCharsets.US_ASCII);
    public static final String NOP_CODON_NAME = "NOP";

    /** The codon table. */
    public static final List<CodonRecord> NCBI_CODON_TABLE = List.of(
            /* 0 */ NOP_CODONS,
            /* 1 */ new CodonRecord(
                    "FFLLSSSSYY**CC*WLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG",
                    StandardCharsets.US_ASCII),
            /* 2 */ new CodonRecord(
                    "FFLLSSSSYY**CCWWLLLLPPPPHHQQRRRRIIMMTTTTNNKKSS**VVVVAAAADDEEGGGG",
                    StandardCharsets.US_ASCII),
            /* 3 */ new CodonRecord(
                    "FFLLSSSSYY**CCWWTTTTPPPPHHQQRRRRIIMMTTTTNNKKSSRRVVVVAAAADDEEGGGG",
                    StandardCharsets.US_ASCII),
            /* 4 */ new CodonRecord(
                    "FFLLSSSSYY**CCWWLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG",
                    StandardCharsets.US_ASCII),
            /* 5 */ new CodonRecord(
                    "FFLLSSSSYY**CCWWLLLLPPPPHHQQRRRRIIMMTTTTNNKKSSSSVVVVAAAADDEEGGGG",
                    StandardCharsets.US_ASCII),
            /* 6 */ new CodonRecord(
                    "FFLLSSSSYYQQCC*WLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG",
                    StandardCharsets.US_ASCII),
            /* 7 */ NOP_CODONS,
            /* 8 */ NOP_CODONS,
            /* 9 */ new CodonRecord(
                    "FFLLSSSSYY**CCWWLLLLPPPPHHQQRRRRIIIMTTTTNNNKSSSSVVVVAAAADDEEGGGG",
                    StandardCharsets.US_ASCII),
            /* 10 */ new CodonRecord(
                    "FFLLSSSSYY**CCCWLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG",
                    StandardCharsets.US_ASCII),
            /* 11 */ new CodonRecord(
                    "FFLLSSSSYY**CC*WLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG",
                    StandardCharsets.US_ASCII),
            /* 12 */ new CodonRecord(
                    "FFLLSSSSYY**CC*WLLLSPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG",
                    StandardCharsets.US_ASCII),
            /* 13 */ new CodonRecord(
                    "FFLLSSSSYY**CCWWLLLLPPPPHHQQRRRRIIMMTTTTNNKKSSGGVVVVAAAADDEEGGGG",
                    StandardCharsets.US_ASCII),
            /* 14 */ new CodonRecord(
                    "FFLLSSSSYYY*CCWWLLLLPPPPHHQQRRRRIIIMTTTTNNNKSSSSVVVVAAAADDEEGGGG",
                    StandardCharsets.US_ASCII),
            /* 15 */ new CodonRecord(
                    "FFLLSSSSYY*QCC*WLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG",
                    StandardCharsets.US_ASCII),
            /* 16 */ new CodonRecord(
                    "FFLLSSSSYY*LCC*WLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG",
                    StandardCharsets.US_ASCII),
            /* 17 */ NOP_CODONS,
            /* 18 */ NOP_CODONS,
            /* 19 */ NOP_CODONS,
            /* 20 */ NOP_CODONS,
            /* 21 */ new CodonRecord(
                    "FFLLSSSSYY**CCWWLLLLPPPPHHQQRRRRIIMMTTTTNNNKSSSSVVVVAAAADDEEGGGG",
                    StandardCharsets.US_ASCII),
            /* 22 */ new CodonRecord(
                    "FFLLSS*SYY*LCC*WLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG",
                    StandardCharsets.US_ASCII),
            /* 23 */ new CodonRecord(
                    "FF*LSSSSYY**CC*WLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG",
                    StandardCharsets.US_ASCII),
            /* 24 */ new CodonRecord(
                    "FFLLSSSSYY**CCWWLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSSKVVVVAAAADDEEGGGG",
                    StandardCharsets.US_ASCII),
            /* 25 */ new CodonRecord(
                    "FFLLSSSSYY**CCGWLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG",
                    StandardCharsets.US_ASCII),
            /* 26 */ new CodonRecord(
                    "FFLLSSSSYY**CC*WLLLAPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG",
                    StandardCharsets.US_ASCII),
            /* 27 */ new CodonRecord(
                    "FFLLSSSSYYQQCCWWLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG",
                    StandardCharsets.US_ASCII),
            /* 28 */ new CodonRecord(
                    "FFLLSSSSYYQQCCWWLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG",
                    StandardCharsets.US_ASCII),
            /* 29 */ new CodonRecord(
                    "FFLLSSSSYYYYCC*WLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG",
                    StandardCharsets.US_ASCII),
            /* 30 */ new CodonRecord(
                    "FFLLSSSSYYEECC*WLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG",
                    StandardCharsets.US_ASCII),
            /* 31 */ new CodonRecord(
                    "FFLLSSSSYYEECCWWLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG",
                    StandardCharsets.US_ASCII),
            /* 32 */ NOP_CODONS,
            /* 33 */ new CodonRecord(
                    "FFLLSSSSYYY*CCWWLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSSKVVVVAAAADDEEGGGG",
                    StandardCharsets.US_ASCII));

    public static final List<String> NCBI_CODON_NAMES = List.of(
            /* 0 */ NOP_CODON_NAME,
            /* 1 */ "The Standard Code",
            /* 2 */ "The Vertebrate Mitochondrial Code",
            /* 3 */ "The Yeast Mitochondrial Code",
            /* 4 */ "The Mold, Protozoan, and Coelenterate Mitochondrial Code and the Mycoplasma/Spiroplasma Code",
            /* 5 */ "The Invertebrate Mitochondrial Code",
            /* 6 */ "The Ciliate, Dasycladacean and Hexamita Nuclear Code",
            /* 7 */ NOP_CODON_NAME,
            /* 8 */ NOP_CODON_NAME,
            /* 9 */ "The Echinoderm and Flatworm Mitochondrial Code",
            /* 10 */ "The Euplotid Nuclear Code",
            /* 11 */ "The Bacterial, Archaeal and Plant Plastid Code",
            /* 12 */ "The Alternative Yeast Nuclear Code",
            /* 13 */ "The Ascidian Mitochondrial Code",
            /* 14 */ "The Alternative Flatworm Mitochondrial Code",
            /* 15 */ "Blepharisma Nuclear Code",
            /* 16 */ "Chlorophycean Mitochondrial Code",
            /* 17 */ NOP_CODON_NAME,
            /* 18 */ NOP_CODON_NAME,
            /* 19 */ NOP_CODON_NAME,
            /* 20 */ NOP_CODON_NAME,
            /* 21 */ "Trematode Mitochondrial Code",
            /* 22 */ "Scenedesmus obliquus Mitochondrial Code",
            /* 23 */ "Thraustochytrium Mitochondrial Code",
            /* 24 */ "Rhabdopleuridae Mitochondrial Code",
            /* 25 */ "Candidate Division SR1 and Gracilibacteria Code",
            /* 26 */ "Pachysolen tannophilus Nuclear Code",
            /* 27 */ "Karyorelict Nuclear Code",
            /* 28 */ "Condylostoma Nuclear Code",
            /* 29 */ "Mesodinium Nuclear Code",
            /* 30 */ "Peritrich Nuclear Code",
            /* 31 */ "Blastocrithidia Nuclear Code",
            /* 32 */ "Balanophoraceae Plastid Code",
            /* 33 */ "Cephalodiscidae Mitochondrial UAA-Tyr Code");

    public static CodonRecord getCodonTable(final int ncbiCodonIndex) {
        return NCBI_CODON_TABLE.get(ncbiCodonIndex);
    }

    public static String getCodonName(final int ncbiCodonIndex) {
        return NCBI_CODON_NAMES.get(ncbiCodonIndex);
    }

    public static int size() {
        return NCBI_CODON_NAMES.size();
    }

    /**
     * Create {@link SimpleTranslator} using desired codon table.
     *
     * @param ncbiCodonIndex As described.
     * @param startCodons As described.
     * @return As described.
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull SimpleTranslator getTranslator(
            final int ncbiCodonIndex, final byte[][] startCodons) {
        return new SimpleTranslator(getCodonTable(ncbiCodonIndex), startCodons);
    }
}
