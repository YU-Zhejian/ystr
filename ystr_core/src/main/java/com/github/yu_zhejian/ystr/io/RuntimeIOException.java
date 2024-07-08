package com.github.yu_zhejian.ystr.io;

import java.io.Serial;

/** Wrapper for {@link RuntimeException} to make SonarLint silence. */
public class RuntimeIOException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Convenient constructor.
     *
     * @param description As described.
     * @param e As described.
     */
    public RuntimeIOException(String description, Exception e) {
        super(description, e);
    }
}
