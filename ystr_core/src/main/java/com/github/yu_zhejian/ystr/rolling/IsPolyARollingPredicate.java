package com.github.yu_zhejian.ystr.rolling;

import org.jetbrains.annotations.NotNull;

/**
 * k-mers with bases except {@code AGCTUagctu}.
 *
 * <p>TODO: testing
 *
 * <p>Warning, the negative part of {@link Byte} is not used.
 */
public final class IsPolyARollingPredicate extends RollingPredicateBase {
    private static final boolean[] PREDICATE = {
        false, false, false, false, false, false, false, false, // 0..7
        false, false, false, false, false, false, false, false, // 8..15
        false, false, false, false, false, false, false, false, // 16..23
        false, false, false, false, false, false, false, false, // 24..31
        false, false, false, false, false, false, false, false, // 32..39
        false, false, false, false, false, false, false, false, // 40..47
        false, false, false, false, false, false, false, false, // 48..55
        false, false, false, false, false, false, false, false, // 56..63
        false, true, false, false, false, false, false, true, // 64..71
        false, false, false, false, false, false, false, false, // 72..79
        false, false, false, false, true, true, false, false, // 80..87
        false, false, false, false, false, false, false, false, // 88..95
        false, true, false, false, false, false, false, false, // 96..103
        false, false, false, false, false, false, false, false, // 104..111
        false, false, false, false, true, true, false, false, // 112..119
        false, false, false, false, false, false, false, false // 120..127
    };

    /** Number of A, T and U inside the current bin. */
    private int numA;

    /**
     * Minumun number of {@link #numA} to consider a Poly(A). Should not be less than 1.
     *
     * <p>Default to {@code 0.75 * k}.
     */
    private final int numAThreshold;

    /**
     * As described.
     *
     * @param string As described.
     * @param k As described.
     * @param skipFirst As described.
     * @param numAThreshold As described.
     */
    public IsPolyARollingPredicate(
            byte @NotNull [] string, int k, int skipFirst, int numAThreshold) {
        super(string, k, skipFirst);
        this.numAThreshold = numAThreshold;
        if (numAThreshold < 1) {
            throw new IllegalArgumentException(
                    "Number of A should not less than 1! Actual: %s".formatted(numAThreshold));
        }
    }

    /**
     * As described.
     *
     * @param string As described.
     * @param k As described.
     * @param skipFirst As described.
     */
    public IsPolyARollingPredicate(byte @NotNull [] string, int k, int skipFirst) {
        this(string, k, skipFirst, Math.min((int) (0.75 * k), 1));
    }

    @Override
    protected void initCurrentValue() {
        for (int i = 0; i < k; i++) {
            if (PREDICATE[string[i]]) {
                numA += 1;
            }
        }
        currentValueUnboxed = numA >= numAThreshold;
    }

    @Override
    protected void updateCurrentValueToNextState() {
        final var i = curPos - 1;
        final var seqi = string[i];
        final var seqk = string[i + k];
        final var predi = PREDICATE[seqi];
        final var predk = PREDICATE[seqk];
        if (seqi == seqk) {
            return;
        }
        if (predi) {
            numA--;
        }
        if (predk) {
            numA++;
        }
        currentValueUnboxed = numA >= numAThreshold;
    }
}
