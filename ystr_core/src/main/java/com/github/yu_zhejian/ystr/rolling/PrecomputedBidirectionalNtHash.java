package com.github.yu_zhejian.ystr.rolling;

import org.jetbrains.annotations.NotNull;

/**
 * Pre-computed {@link NtHash} implementation without updating {@link #currentValueUnboxed}. Only
 * {@link #getFwdHash()} and {@link #getRevHash()} is working; {@link #nextUnboxed()} will always
 * return 0.
 */
public final class PrecomputedBidirectionalNtHash extends NtHashBase {

    /**
     * As described.
     *
     * @param string As described.
     * @param k As described.
     * @param start As described.
     */
    public PrecomputedBidirectionalNtHash(
            final byte @NotNull [] string, final int k, final int start) {
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
        currentValueUnboxed = 0L;
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
    }
}
