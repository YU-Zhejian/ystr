package com.github.yu_zhejian.ystr.utils;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Iterator;

public final class KmerGenerator implements Iterator<byte[]> {

    private final byte[] alphabet;
    private final int alphabetMax;
    public final int k;
    private final int[] pos;
    private final int[] finalPos;
    private boolean hasStarted;

    public KmerGenerator(byte @NotNull [] alphabet, int k) {
        if (alphabet.length == 0) {
            throw new IllegalArgumentException("alphabet must contain at least one character");
        }
        if (k <= 0) {
            throw new IllegalArgumentException("k must be positive");
        }
        this.alphabet = alphabet;
        this.k = k;
        alphabetMax = alphabet.length - 1;
        pos = new int[k];
        finalPos = new int[k];
        Arrays.fill(finalPos, 0);
    }

    public void advPos() {
        for (int i = k - 1; i >= 0; i--) {
            if (pos[i] == alphabetMax) {
                pos[i] = 0;
            } else {
                pos[i]++;
                return;
            }
        }
    }

    @Override
    public boolean hasNext() {
        return !(hasStarted && Arrays.equals(pos, finalPos));
    }

    @Override
    public byte @NotNull [] next() {
        hasStarted = true;
        var state = new byte[k];
        for (int i = 0; i < k; i++) {
            state[i] = alphabet[pos[i]];
        }
        advPos();
        return state;
    }
}
