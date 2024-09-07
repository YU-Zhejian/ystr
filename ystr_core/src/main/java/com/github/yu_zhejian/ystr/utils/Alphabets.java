package com.github.yu_zhejian.ystr.utils;

import com.github.yu_zhejian.ystr.container.ImmutableByteArray;

/** Commonly used alphabets within range of signed {@link Byte}. */
public final class Alphabets {
    /** DNA Alphabet. */
    public static final ImmutableByteArray DNA_ALPHABET =
            new ImmutableByteArray(new byte[] {'A', 'C', 'G', 'T'});
    /** {@link #DNA_ALPHABET} with <code>N</code>. * */
    public static final ImmutableByteArray DNA5_ALPHABET =
            new ImmutableByteArray(new byte[] {'A', 'C', 'G', 'T', 'N'});
    /** {@link #DNA_ALPHABET} with smaller cases. */
    public static final ImmutableByteArray DNA_ALPHABET_BC =
            new ImmutableByteArray(new byte[] {'A', 'C', 'G', 'T', 'a', 'g', 'c', 't'});
    /** {@link #DNA_ALPHABET_BC} with <code>N</code>. * */
    public static final ImmutableByteArray DNA5_ALPHABET_BC =
            new ImmutableByteArray(new byte[] {'A', 'C', 'G', 'T', 'N', 'a', 'g', 'c', 't', 'n'});

    /** RNA Alphabet. */
    public static final ImmutableByteArray RNA_ALPHABET =
            new ImmutableByteArray(new byte[] {'A', 'C', 'G', 'U'});
    /** {@link #RNA_ALPHABET} with <code>N</code> * */
    public static final ImmutableByteArray RNA5_ALPHABET =
            new ImmutableByteArray(new byte[] {'A', 'C', 'G', 'U', 'N'});

    /** Amino acid Alphabet. */
    public static final ImmutableByteArray AA_ALPHABET = new ImmutableByteArray(new byte[] {
        'A', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'V',
        'W', 'Y'
    });

    /** Alphabet that contains only 0 and 1 * */
    public static final ImmutableByteArray BINARY_ALPHABET =
            new ImmutableByteArray(new byte[] {0, 1});

    /** Alphabet of all values inside {@link Byte}. */
    public static final ImmutableByteArray FULL_ALPHABET = new ImmutableByteArray(new byte[] {
        -128, -127, -126, -125, -124, -123, -122, -121, -120, -119, -118, -117, -116, -115, -114,
        -113, -112, -111, -110, -109, -108, -107, -106, -105, -104, -103, -102, -101, -100, -99,
        -98, -97, -96, -95, -94, -93, -92, -91, -90, -89, -88, -87, -86, -85, -84, -83, -82, -81,
        -80, -79, -78, -77, -76, -75, -74, -73, -72, -71, -70, -69, -68, -67, -66, -65, -64, -63,
        -62, -61, -60, -59, -58, -57, -56, -55, -54, -53, -52, -51, -50, -49, -48, -47, -46, -45,
        -44, -43, -42, -41, -40, -39, -38, -37, -36, -35, -34, -33, -32, -31, -30, -29, -28, -27,
        -26, -25, -24, -23, -22, -21, -20, -19, -18, -17, -16, -15, -14, -13, -12, -11, -10, -9, -8,
        -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17,
        18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
        41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63,
        64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86,
        87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107,
        108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125,
        126, 127
    });

    /**
     * Alphabet of valid ASCII values.
     *
     * @see java.nio.charset.StandardCharsets#US_ASCII
     */
    public static final ImmutableByteArray ASCII_ALPHABET = new ImmutableByteArray(new byte[] {
        0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24,
        25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47,
        48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70,
        71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93,
        94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112,
        113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127
    });

    private Alphabets() {}
}
