package com.github.yu_zhejian.ystr.rolling;

import org.jetbrains.annotations.NotNull;

/** k-mers with bases except {@code AGCTUagctu}. */
public final class ContainsUnknownBaseRollingPredicate extends RollingPredicateBase {
    private static final boolean[] PREDICATE = {
        false, false, false, false, false, false, false, false, // 0..7
        false, false, false, false, false, false, false, false, // 8..15
        false, false, false, false, false, false, false, false, // 16..23
        false, false, false, false, false, false, false, false, // 24..31
        false, false, false, false, false, false, false, false, // 32..39
        false, false, false, false, false, false, false, false, // 40..47
        false, false, false, false, false, false, false, false, // 48..55
        false, false, false, false, false, false, false, false, // 56..63
        false, true, false, true, false, false, false, true, // 64..71
        false, false, false, false, false, false, false, false, // 72..79
        false, false, false, false, true, true, false, false, // 80..87
        false, false, false, false, false, false, false, false, // 88..95
        false, true, false, true, false, false, false, true, // 96..103
        false, false, false, false, false, false, false, false, // 104..111
        false, false, false, false, true, true, false, false, // 112..119
        false, false, false, false, false, false, false, false // 120..127
    };

    /** Nearest position of unknown base. */
    private int nPos;

    /**
     * As described.
     *
     * @param string As described.
     * @param k As described.
     * @param skipFirst As described.
     */
    public ContainsUnknownBaseRollingPredicate(byte @NotNull [] string, int k, int skipFirst) {
        super(string, k, skipFirst);
        nPos = skipFirst - 1;
    }

    @Override
    protected void initCurrentValue() {
        currentValue = true;
        for (int i = 0; i < k; i++) {
            if (!PREDICATE[string[i]]) {
                currentValue = false;
                nPos = i;
            }
        }
    }

    @Override
    protected void updateCurrentValueToNextState() {
        final var i = curPos - 1;
        final var seqk = string[i + k];
        if (Boolean.FALSE.equals(currentValue) && nPos - curPos > k) {
            currentValue = true;
        }

        if (!PREDICATE[seqk]) {
            currentValue = false;
            nPos = i + k;
        }
    }
}
