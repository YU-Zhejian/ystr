package com.github.yu_zhejian.ystr.match;

import com.github.yu_zhejian.ystr.utils.StrUtils;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/** Constants for Boyer-Moore string matching and variants. */
public abstract class BaseBoyerMoore implements StrMatchInterface {
    /** Default constructor. */
    protected BaseBoyerMoore() {}
    /**
     * Generate bad suffix rule for Boyer-Moore matching. Returned is the last occurrence for each
     * character in needle. The array will be initialized with -1 by default.
     *
     * @param needle As described,
     * @return As described,
     */
    public static int @NotNull [] bmBadCharacterRule(final byte @NotNull [] needle) {
        final var occ = new int[StrUtils.ALPHABET_SIZE];
        Arrays.fill(occ, -1);

        for (var needlePos = 0; needlePos < needle.length; needlePos++) {
            occ[needle[needlePos] & StrUtils.BYTE_TO_UNSIGNED_MASK] = needlePos;
        }
        return occ;
    }
}
