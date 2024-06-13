package com.github.yu_zhejian.ystr.rolling;

import org.jetbrains.annotations.NotNull;

class IntegerWrapper {
    private int val;

    public IntegerWrapper() {
        val = 0;
    }

    public int getVal() {
        return val;
    }

    public void increase() {
        val++;
    }

    public void reduce() {
        val--;
    }

    @Override
    public String toString() {
        return String.valueOf(val);
    }
}

public final class NtShannonEntropy extends RollingEntropyBase {
    private final IntegerWrapper numA = new IntegerWrapper();
    private final IntegerWrapper numG = new IntegerWrapper();
    private final IntegerWrapper numC = new IntegerWrapper();
    private final IntegerWrapper numT = new IntegerWrapper();
    private final IntegerWrapper numN = new IntegerWrapper();

    private final IntegerWrapper[] MAPPING_TABLE = {
        numN, numN, numN, numN, numN, numN, numN, numN, // 0..7
        numN, numN, numN, numN, numN, numN, numN, numN, // 8..15
        numN, numN, numN, numN, numN, numN, numN, numN, // 16..23
        numN, numN, numN, numN, numN, numN, numN, numN, // 24..31
        numN, numN, numN, numN, numN, numN, numN, numN, // 32..39
        numN, numN, numN, numN, numN, numN, numN, numN, // 40..47
        numN, numN, numN, numN, numN, numN, numN, numN, // 48..55
        numN, numN, numN, numN, numN, numN, numN, numN, // 56..63
        numN, numA, numN, numC, numN, numN, numN, numG, // 64..71
        numN, numN, numN, numN, numN, numN, numN, numN, // 72..79
        numN, numN, numN, numN, numT, numT, numN, numN, // 80..87
        numN, numN, numN, numN, numN, numN, numN, numN, // 88..95
        numN, numA, numN, numC, numN, numN, numN, numG, // 96..103
        numN, numN, numN, numN, numN, numN, numN, numN, // 104..111
        numN, numN, numN, numN, numT, numT, numN, numN, // 112..119
        numN, numN, numN, numN, numN, numN, numN, numN // 120..127
    };

    /**
     * As described.
     *
     * @param string As described.
     * @param k As described.
     * @param skipFirst As described.
     */
    public NtShannonEntropy(byte @NotNull [] string, int k, int skipFirst) {
        super(string, k, skipFirst);
        initCurrentValue();
    }

    private double getEntropy() {
        if (numN.getVal() != 0) {
            return 0.0;
        }
        var aPi = numA.getVal() == 0 ? 1 : 1.0 * numA.getVal() / k;
        var gPi = numG.getVal() == 0 ? 1 : 1.0 * numG.getVal() / k;
        var cPi = numC.getVal() == 0 ? 1 : 1.0 * numC.getVal() / k;
        var tPi = numT.getVal() == 0 ? 1 : 1.0 * numT.getVal() / k;
        return -(aPi * Math.log(aPi)
                + gPi * Math.log(gPi)
                + cPi * Math.log(cPi)
                + tPi * Math.log(tPi));
    }

    @Override
    protected void initCurrentValue() {
        for (var i = 0; i < k; i++) {
            MAPPING_TABLE[string[i]].increase();
        }
        currentValueUnboxed = getEntropy();
    }

    @Override
    protected void updateCurrentValueToNextState() {
        final var i = curPos - 1;
        final var seqi = string[i];
        final var seqk = string[i + k];
        MAPPING_TABLE[seqi].reduce();
        MAPPING_TABLE[seqk].increase();
        currentValueUnboxed = getEntropy();
    }
}
