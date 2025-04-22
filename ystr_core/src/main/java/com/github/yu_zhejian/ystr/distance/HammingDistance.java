package com.github.yu_zhejian.ystr.distance;

import org.jetbrains.annotations.NotNull;

public final class HammingDistance extends BaseDistance{
    /**
     * Default initializer.
     */
    public HammingDistance(){}

    @Override
    public long applyUnchecked(byte @NotNull [] string1, byte @NotNull [] string2, int start1, int end1, int start2, int end2) {
        long reti = 0;
        for (int i = 0; i < end1 - start1; i++) {
            if (string1[start1 + i] != string2[start2 + i]) {
                reti++;
            }
        }
        return reti;
    }

    @Override
    protected void check(byte @NotNull [] string1, byte @NotNull [] string2, int start1, int end1, int start2, int end2) {
        super.check(string1, string2, start1, end1, start2, end2);
        if (end2 - start2 != end1 - start1) {
            throw new IllegalArgumentException(
                "Compared region length difference! Actual: [%d, %d) (%d) vs. [%d, %d) (%d)"
                    .formatted(start1, end1, end1 - start1, start2, end2, end2 - start2));
        }
    }
}
