package org.labw.tcr_annotator.falp4j;

import io.vavr.Tuple2;

import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class AlnConf {
    private final Set<String> startCodons;
    private final Map<String, Character> codonTable;
    private final Map<Tuple2<Character, Character>, Integer> substMtx;
    public final int initiationDropoff;
    /**
     * This matches any amino acid. The resulting score should be the mean matching score of a
     * substitution matrix.
     */
    public static final char ANY_AA = '.';

    public static final char STOP_CODON = '*';
    public static final String STOP_CODON_STR = "*";
    public static final char ERR_CODON = '?';
    public static final char AA_PLACEHOLDER = '_';
    public static final char ALN_GAP = '-';
    public static final char ALN_BLANK = ' ';
    public static final char ALN_MATCH = '=';
    public static final char ALN_MIS_MATCH = 'X';
    public static final char ALN_INS = 'I';
    public static final char ALN_DEL = 'D';
    public static final String ALL_AA =
            String.valueOf(AA_PLACEHOLDER) + ERR_CODON + STOP_CODON + "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    /** See: <a href="https://www.ncbi.nlm.nih.gov/Taxonomy/Utils/wprintgc.cgi">...</a> */
    public static final Map<Integer, String> NCBI_CODON_TABLE = Map.ofEntries(
            Map.entry(1, "FFLLSSSSYY**CC*WLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG"),
            Map.entry(2, "FFLLSSSSYY**CCWWLLLLPPPPHHQQRRRRIIMMTTTTNNKKSS**VVVVAAAADDEEGGGG"),
            Map.entry(3, "FFLLSSSSYY**CCWWTTTTPPPPHHQQRRRRIIMMTTTTNNKKSSRRVVVVAAAADDEEGGGG"),
            Map.entry(4, "FFLLSSSSYY**CCWWLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG"),
            Map.entry(5, "FFLLSSSSYY**CCWWLLLLPPPPHHQQRRRRIIMMTTTTNNKKSSSSVVVVAAAADDEEGGGG"),
            Map.entry(6, "FFLLSSSSYYQQCC*WLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG"),
            Map.entry(9, "FFLLSSSSYY**CCWWLLLLPPPPHHQQRRRRIIIMTTTTNNNKSSSSVVVVAAAADDEEGGGG"),
            Map.entry(10, "FFLLSSSSYY**CCCWLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG"),
            Map.entry(11, "FFLLSSSSYY**CC*WLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG"),
            Map.entry(12, "FFLLSSSSYY**CC*WLLLSPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG"),
            Map.entry(13, "FFLLSSSSYY**CCWWLLLLPPPPHHQQRRRRIIMMTTTTNNKKSSGGVVVVAAAADDEEGGGG"),
            Map.entry(14, "FFLLSSSSYYY*CCWWLLLLPPPPHHQQRRRRIIIMTTTTNNNKSSSSVVVVAAAADDEEGGGG"),
            Map.entry(15, "FFLLSSSSYY*QCC*WLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG"),
            Map.entry(16, "FFLLSSSSYY*LCC*WLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG"),
            Map.entry(21, "FFLLSSSSYY**CCWWLLLLPPPPHHQQRRRRIIMMTTTTNNNKSSSSVVVVAAAADDEEGGGG"),
            Map.entry(22, "FFLLSS*SYY*LCC*WLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG"),
            Map.entry(23, "FF*LSSSSYY**CC*WLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG"),
            Map.entry(24, "FFLLSSSSYY**CCWWLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSSKVVVVAAAADDEEGGGG"),
            Map.entry(25, "FFLLSSSSYY**CCGWLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG"),
            Map.entry(26, "FFLLSSSSYY**CC*WLLLAPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG"),
            Map.entry(27, "FFLLSSSSYYQQCCWWLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG"),
            Map.entry(28, "FFLLSSSSYYQQCCWWLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG"),
            Map.entry(29, "FFLLSSSSYYYYCC*WLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG"),
            Map.entry(30, "FFLLSSSSYYEECC*WLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG"),
            Map.entry(31, "FFLLSSSSYYEECCWWLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG"),
            Map.entry(33, "FFLLSSSSYYY*CCWWLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSSKVVVVAAAADDEEGGGG"));
    /**
     * See: <a href="https://www.ncbi.nlm.nih.gov/Taxonomy/Utils/wprintgc.cgi">...</a>
     *
     * <p>Notice: Current implementation is incomplete. The author is lazy and do not wish to port
     * other translation tables.
     */
    public static final Map<Integer, Set<String>> NCBI_START_CODONS = Map.ofEntries(
            Map.entry(1, Set.of("ATG")) // Incomplete
            );

    public final int gapOpenPenalty;
    public final int gapExtendPenalty;
    public final int frameshiftPenalty;
    private final int meanMatchScore;

    /**
     * Create codon table using NCBI notation. That is, go over each column of
     *
     * <p>
     *
     * <p>AAs = FFLLSSSSYY**CC*WLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG
     *
     * <p>Base1 = TTTTTTTTTTTTTTTTCCCCCCCCCCCCCCCCAAAAAAAAAAAAAAAAGGGGGGGGGGGGGGGG
     *
     * <p>Base2 = TTTTCCCCAAAAGGGGTTTTCCCCAAAAGGGGTTTTCCCCAAAAGGGGTTTTCCCCAAAAGGGG
     *
     * <p>Base3 = TCAGTCAGTCAGTCAGTCAGTCAGTCAGTCAGTCAGTCAGTCAGTCAGTCAGTCAGTCAGTCAG
     *
     * @return As described.
     */
    public static @NotNull Map<String, Character> constCodon(String aas) {
        var retd = new HashMap<String, Character>();
        var ca = "TCAG".toCharArray();
        var i = 0;
        for (var base1 : ca) {
            for (var base2 : ca) {
                for (var base3 : ca) {
                    var codon = String.valueOf(base1) + base2 + base3;
                    retd.put(codon, aas.charAt(i));
                    i++;
                }
            }
        }
        return retd;
    }

    /**
     * Create a simple substitution matrix with constant match and mismatch score.
     *
     * @param matchScore Positive match score.
     * @param mismatchPenalty Positive mismatch penalty.
     * @return As described.
     */
    public static @NotNull Map<Tuple2<Character, Character>, Integer> simpleSubstMtx(
            int matchScore, int mismatchPenalty) {
        var retd = new HashMap<Tuple2<Character, Character>, Integer>();
        for (var aa1 : ALL_AA.toCharArray()) {
            for (var aa2 : ALL_AA.toCharArray()) {
                retd.put(new Tuple2<>(aa1, aa2), aa1 == aa2 ? matchScore : -mismatchPenalty);
            }
        }
        return retd;
    }

    /**
     * Read substitution matrix from resources bundled.
     *
     * @param mtxName As described.
     * @return As described.
     */
    public static @NotNull Map<Tuple2<Character, Character>, Integer> readSubstMtx(String mtxName) {
        var lh = LoggerFactory.getLogger(AlnConf.class);
        var mtxFileName = "matrices/%s.txt".formatted(mtxName);
        var resourceInputStream = ClassLoader.getSystemResourceAsStream(mtxFileName);
        if (resourceInputStream == null) {
            lh.error("File {} not found!", mtxFileName);
            throw new RuntimeException();
        }
        try (var resourceReader = new BufferedReader(
                new InputStreamReader(resourceInputStream, StandardCharsets.UTF_8))) {
            var retd = new HashMap<Tuple2<Character, Character>, Integer>();
            List<Character> aaSymbols = List.of();
            while (true) {
                var line = resourceReader.readLine();
                if (line == null) {
                    break;
                } else if (line.startsWith("#")) {
                    continue;
                }
                if (line.startsWith(" ")) {
                    aaSymbols = Arrays.stream(line.strip().split(" +"))
                            .map(s -> s.charAt(0))
                            .toList();
                } else {
                    var lineContent = line.strip().split(" +");
                    var thisLineSymbol = lineContent[0].charAt(0);
                    for (var i = 1; i < lineContent.length; i++) {
                        retd.put(
                                new Tuple2<>(thisLineSymbol, aaSymbols.get(i - 1)),
                                Integer.parseInt(lineContent[i]));
                    }
                }
            }
            return retd;
        } catch (IOException e) {
            lh.error("File {} unreadable!", mtxFileName);
            throw new RuntimeException(e);
        }
    }

    public AlnConf(
            Map<String, Character> codonTable,
            Collection<String> startCodons,
            Map<Tuple2<Character, Character>, Integer> substMtx,
            int gapOpenPenalty,
            int gapExtendPenalty,
            int frameshiftPenalty,
            int initiationDropoff) {
        this.codonTable = codonTable;
        this.substMtx = substMtx;
        this.startCodons = new HashSet<>(startCodons);
        this.gapOpenPenalty = gapOpenPenalty;
        this.gapExtendPenalty = gapExtendPenalty;
        this.frameshiftPenalty = frameshiftPenalty;
        this.initiationDropoff = initiationDropoff;
        this.meanMatchScore = (int) substMtx.entrySet().stream()
                .filter(it -> {
                    var key = it.getKey();
                    return key._1 == key._2;
                })
                .map(Map.Entry::getValue)
                .mapToInt(Integer::intValue)
                .average()
                .orElseThrow();
    }

    /**
     * Calculate score for amino acid substitution using substitution matrix provided.
     *
     * <p>See special case: {@link #ANY_AA} produces {@link #meanMatchScore}.
     *
     * @param aa1 As described.
     * @param aa2 As described.
     * @return As described.
     */
    public int scoreAASubst(char aa1, char aa2) {
        if (aa1 == ANY_AA || aa2 == ANY_AA) {
            return meanMatchScore;
        }
        try {
            return substMtx.get(new Tuple2<>(aa1, aa2));
        } catch (Exception e) {
            System.out.println(new Tuple2<>(aa1, aa2));
            throw e;
        }
    }

    /**
     * Translate.
     *
     * @param codonDnaSeq As described.
     * @return As described.
     */
    public char codonToAA(@NotNull String codonDnaSeq) {
        if (codonDnaSeq.contains(STOP_CODON_STR)) {
            return STOP_CODON;
        }
        return codonTable.get(codonDnaSeq);
    }

    /**
     * Determine whether a DNA sequence is start codon.
     *
     * @param codonDnaSeq As described.
     * @return As described.
     */
    public boolean isStart(@NotNull String codonDnaSeq) {
        return startCodons.contains(codonDnaSeq);
    }
}
