package com.github.yu_zhejian.ystr.sort;

import com.github.yu_zhejian.ystr.StrLibc;

import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;

/**
 * Sort using standard Java {@link List#sort(Comparator)} with {@link StrLibc#strcmp} as comparator.
 */
public final class JavaStdSort implements SortInterface {
    @Override
    public void sort(@NotNull List<byte[]> strings) {
        strings.sort(StrLibc::strcmp);
    }
}
