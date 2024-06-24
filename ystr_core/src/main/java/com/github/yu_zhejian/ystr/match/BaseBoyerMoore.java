package com.github.yu_zhejian.ystr.match;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public abstract class BaseBoyerMoore implements StrMatchInterface {
    protected BaseBoyerMoore() {}
    /**
     * Generate bad suffix rule for Boyer-Moore matching. Returned is the last occurrence for each
     * character in needle. The array will be initialized with -1 by default.
     *
     * @param needle As described,
     * @return As described,
     */
    public static int @NotNull [] bmBadCharacterRule(final byte @NotNull [] needle) {
        final var occ = new int[StrMatchUtils.ALPHABET_SIZE];
        Arrays.fill(occ, -1);

        for (var needlePos = 0; needlePos < needle.length; needlePos++) {
            occ[needle[needlePos] & 0xFF] = needlePos;
        }
        return occ;
    }
}
