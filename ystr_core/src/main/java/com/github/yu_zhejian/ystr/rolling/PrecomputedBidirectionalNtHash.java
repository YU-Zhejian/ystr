package com.github.yu_zhejian.ystr.rolling;

/**
 * Pre-computed {@link NtHash} implementation without updating {@link #currentValueUnboxed}. Only
 * {@link #getFwdHash()} and {@link #getRevHash()} is working;
 * {@link RollingHashInterface#nextLong()} will always return 0.
 */
public class PrecomputedBidirectionalNtHash extends NtHashBase {

    @Override
    protected void initCurrentValue() {
        ensureAttached();
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
        ensureAttached();
        final var i = curPos - 1;
        final var seqi = string[i] & 0xFF;
        final var seqk = string[i + k] & 0xFF;
        fwdHash = Long.rotateLeft(fwdHash, 1) ^ MS_TAB[seqi][k % 64] ^ MS_TAB[seqk][0];
        revHash = Long.rotateRight(revHash, 1)
                ^ MS_TAB[seqi & CP_OFF][63]
                ^ MS_TAB[seqk & CP_OFF][(k - 1) % 64];
    }
}
