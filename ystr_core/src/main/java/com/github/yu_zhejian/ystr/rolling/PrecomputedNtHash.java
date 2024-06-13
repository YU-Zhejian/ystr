package com.github.yu_zhejian.ystr.rolling;

import org.jetbrains.annotations.NotNull;

/**
 * Pre-computed {@link NtHash} implementation.
 *
 * <p>This is a direct port of C++ version of ntHash 1.0.0.
 */
public final class PrecomputedNtHash extends NtHashBase {
    /**
     * As described.
     *
     * @param string As described.
     * @param k As described.
     * @param start As described.
     */
    public PrecomputedNtHash(final byte @NotNull [] string, final int k, final int start) {
        super(string, k, start);
    }

    @Override
    protected void initCurrentValue() {
        fwdHash = 0;
        for (var i = 0; i < k; i++) {
            fwdHash ^= MS_TAB[string[i + skipFirst]][(k - 1 - i) % 64];
        }
        revHash = 0;
        for (var i = 0; i < k; i++) {
            revHash ^= MS_TAB[string[i + skipFirst] & CP_OFF][i % 64];
        }
        currentValueUnboxed = Long.compareUnsigned(fwdHash, revHash) < 0 ? fwdHash : revHash;
    }

    @Override
    protected void updateCurrentValueToNextState() {
        final var i = curPos - 1;
        final var seqi = string[i];
        final var seqk = string[i + k];
        fwdHash = Long.rotateLeft(fwdHash, 1) ^ MS_TAB[seqi][k % 64] ^ MS_TAB[seqk][0];
        revHash = Long.rotateRight(revHash, 1)
                ^ MS_TAB[seqi & CP_OFF][63]
                ^ MS_TAB[seqk & CP_OFF][(k - 1) % 64];
        // This line redone Long.compareUnsigned
        currentValueUnboxed =
                (fwdHash + Long.MIN_VALUE < revHash + Long.MIN_VALUE) ? fwdHash : revHash;
    }
}
