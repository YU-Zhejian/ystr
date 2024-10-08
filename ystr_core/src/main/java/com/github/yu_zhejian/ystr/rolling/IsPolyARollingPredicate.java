package com.github.yu_zhejian.ystr.rolling;

import com.github.yu_zhejian.ystr.utils.StrUtils;

/**
 * k-mers with bases except {@code AGCTUagctu}.
 *
 * <p>TODO: Things like {@code TATATATAATA} will be considered poly(A), which of course does not
 * make sense.
 *
 * <p>Warning, the negative part of {@link Byte} is not used.
 */
public final class IsPolyARollingPredicate extends RollingPredicateBase {
    /** Whether the underlying base is Poly(A). */
    static final boolean[] PREDICATE = {
        false, false, false, false, false, false, false, false, // 0..7
        false, false, false, false, false, false, false, false, // 8..15
        false, false, false, false, false, false, false, false, // 16..23
        false, false, false, false, false, false, false, false, // 24..31
        false, false, false, false, false, false, false, false, // 32..39
        false, false, false, false, false, false, false, false, // 40..47
        false, false, false, false, false, false, false, false, // 48..55
        false, false, false, false, false, false, false, false, // 56..63
        false, true, false, false, false, false, false, false, // 64..71
        false, false, false, false, false, false, false, false, // 72..79
        false, false, false, false, true, true, false, false, // 80..87
        false, false, false, false, false, false, false, false, // 88..95
        false, true, false, false, false, false, false, false, // 96..103
        false, false, false, false, false, false, false, false, // 104..111
        false, false, false, false, true, true, false, false, // 112..119
        false, false, false, false, false, false, false, false, // 120..127
        false, false, false, false, false, false, false, false, // 128..135
        false, false, false, false, false, false, false, false, // 136..143
        false, false, false, false, false, false, false, false, // 144..151
        false, false, false, false, false, false, false, false, // 152..159
        false, false, false, false, false, false, false, false, // 160..167
        false, false, false, false, false, false, false, false, // 168..175
        false, false, false, false, false, false, false, false, // 176..183
        false, false, false, false, false, false, false, false, // 184..191
        false, false, false, false, false, false, false, false, // 192..199
        false, false, false, false, false, false, false, false, // 200..207
        false, false, false, false, false, false, false, false, // 208..215
        false, false, false, false, false, false, false, false, // 216..223
        false, false, false, false, false, false, false, false, // 224..231
        false, false, false, false, false, false, false, false, // 232..239
        false, false, false, false, false, false, false, false, // 240..247
        false, false, false, false, false, false, false, false // 248..255
    };

    /** Number of A, T and U inside the current bin. */
    private int numA;

    /**
     * Minimum number of {@link #numA} to consider a Poly(A). Should not be less than 1.
     *
     * <p>Default to {@code 0.75 * k}.
     */
    private final int numAThreshold;

    /**
     * See {@link #numAThreshold}.
     *
     * @return As described.
     */
    public int getNumAThreshold() {
        return numAThreshold;
    }

    /**
     * As described.
     *
     * @param numAThreshold As described.
     */
    public IsPolyARollingPredicate(int numAThreshold) {
        this.numAThreshold = numAThreshold;
        if (numAThreshold < 1) {
            throw new IllegalArgumentException(
                    "Number of A should not less than 1! Actual: %s".formatted(numAThreshold));
        }
    }

    @Override
    protected void initCurrentValue() {
        ensureAttached();
        for (int i = 0; i < k; i++) {
            if (PREDICATE[string[i] & StrUtils.BYTE_TO_UNSIGNED_MASK]) {
                numA += 1;
            }
        }
        currentValueUnboxed = numA >= numAThreshold;
    }

    @Override
    protected void updateCurrentValueToNextState() {
        ensureAttached();
        final var i = curPos - 1;
        final var seqi = string[i] & StrUtils.BYTE_TO_UNSIGNED_MASK;
        final var seqk = string[i + k] & StrUtils.BYTE_TO_UNSIGNED_MASK;
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
