package com.github.yu_zhejian.ystr.match;

import com.github.yu_zhejian.ystr.utils.StrUtils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/** Constants for Boyer-Moore string matching and variants. */
public abstract class BaseBoyerMoore implements StrMatchInterface {
    /** Default constructor. */
    protected BaseBoyerMoore() {}

    /**
     * Generate bad suffix rule for Boyer-Moore matching. Returned is the last occurrence for each
     * character in needle. The array will be of length {@link StrUtils#ALPHABET_SIZE} and
     * initialized with -1 by default.
     *
     * @param needle As described.
     * @return As described.
     */
    public static int @NotNull [] bmBadCharacterRule(final byte @NotNull [] needle) {
        final var occ = new int[StrUtils.ALPHABET_SIZE];
        Arrays.fill(occ, -1);

        for (var needlePos = 0; needlePos < needle.length; needlePos++) {
            occ[needle[needlePos] & StrUtils.BYTE_TO_UNSIGNED_MASK] = needlePos;
        }
        return occ;
    }

    /**
     * Code adapted from C implementation.
     *
     * <p>For any {@code i \in [0, l)}, we have {@code s[bmGs[i] - l + i: bmGs[i]] == s[i: l]},
     * default to zero.
     *
     * @param needle As described.
     * @return As described.
     * @see <a href="https://www-igm.univ-mlv.fr/~lecroq/string/node14.html#SECTION00140">C
     *     Implementation</a>
     */
    @Contract(pure = true)
    public static int @NotNull [] bmSuffixes(final byte @NotNull [] needle) {
        var suff = new int[needle.length];
        int f = 0;

        suff[needle.length - 1] = needle.length;
        int g = needle.length - 1;
        for (int i = needle.length - 2; i >= 0; --i) {
            if (i > g && suff[i + needle.length - 1 - f] < i - g) {
                suff[i] = suff[i + needle.length - 1 - f];
            } else {
                if (i < g) {
                    g = i;
                }
                f = i;
                while (g >= 0 && needle[g] == needle[g + needle.length - 1 - f]) {
                    g--;
                }
                suff[i] = f - g;
            }
        }
        return suff;
    }

    /**
     * @param needle
     * @return
     */
    public static int @NotNull [] bmGoodSuffixRule(final byte @NotNull [] needle) {
        int i;
        var suff = bmSuffixes(needle);
        var bmGs = new int[needle.length];

        for (i = 0; i < needle.length; ++i) {
            bmGs[i] = needle.length;
        }
        int j = 0;
        for (i = needle.length - 1; i >= 0; --i) {
            if (suff[i] == i + 1) {
                for (; j < needle.length - 1 - i; ++j) {
                    if (bmGs[j] == needle.length) {
                        bmGs[j] = needle.length - 1 - i;
                    }
                }
            }
        }
        for (i = 0; i <= needle.length - 2; ++i) {
            bmGs[needle.length - 1 - suff[i]] = needle.length - 1 - i;
        }
        return bmGs;
    }
}
