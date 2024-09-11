package com.github.yu_zhejian.ystr.sort;

import com.github.yu_zhejian.ystr.utils.StrUtils;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class LsdSort implements SortInterface {

    // FIXME: This method have bugs.
    @Override
    public void sort(@NotNull List<byte[]> strings) {
        if (strings.isEmpty()) {
            return;
        }
        int keyLen = strings.get(0).length;
        int[] count = new int[StrUtils.ALPHABET_SIZE];
        var aux = new ObjectArrayList<byte[]>();
        for (int i = 0; i < strings.size(); i++){
            aux.add(null);
        }
        for (int d = keyLen - 1; d >= 0; d--) {
            Arrays.fill(count, 0);
            for (int i = 0; i < StrUtils.ALPHABET_SIZE; i++) {
                count[(strings.get(i)[d] & StrUtils.BYTE_TO_UNSIGNED_MASK) + 1]++;
            }
            for (int k = 1; k < 256; k++) {
                count[k] += count[k - 1];
            }
            for (byte[] string : strings) {
                aux.set(count[(string[d] & StrUtils.BYTE_TO_UNSIGNED_MASK)]++, string);
            }
            for (int i = 0; i < strings.size(); i++) {
                strings.set(i, aux.get(i));
            }
        }
        strings.size();
    }
}
