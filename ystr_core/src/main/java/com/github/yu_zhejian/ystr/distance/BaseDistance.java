package com.github.yu_zhejian.ystr.distance;

import com.github.yu_zhejian.ystr.utils.StrUtils;
import org.jetbrains.annotations.NotNull;

public abstract class BaseDistance implements DistanceInterface {
    public long apply(
        final byte @NotNull [] string1,
        final byte @NotNull [] string2,
        final int start1,
        final int end1,
        final int start2,
        final int end2){
        check(string1, string2, start1, end1, start2, end2);
        return applyUnchecked(string1, string2, start1, end1, start2, end2);
    }

    @Override
    public long apply(byte @NotNull [] string1, byte @NotNull [] string2) {
        return apply(string1, string2,0, string1.length, 0, string2.length );
    }

    protected void check(
        final byte @NotNull [] string1,
        final byte @NotNull [] string2,
        final int start1,
        final int end1,
        final int start2,
        final int end2){
        StrUtils.ensureStartEndValid(start1, end1, string1.length);
        StrUtils.ensureStartEndValid(start2, end2, string2.length);
    }
}
