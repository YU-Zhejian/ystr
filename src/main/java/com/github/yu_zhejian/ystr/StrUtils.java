package com.github.yu_zhejian.ystr;

public final class StrUtils {
    private StrUtils() {}

    public static int pow(int p, int q) {
        int retv = 1;
        for (int i = q; i > 0; i--) {
            retv = retv * p;
        }
        return retv;
    }

    public static long pow(long p, int q) {
        long retv = 1;
        for (int i = q; i > 0; i--) {
            retv = retv * p;
        }
        return retv;
    }
}
