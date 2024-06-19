package com.github.yu_zhejian.ystr;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/** Utilities mimicking Python functions. */
public final class PyUtils {
    /**
     * Configuration for {@link #print(PrintParams, Object...)}.
     *
     * @param sep What to print between each object.
     * @param end What to print after printing all objects.
     * @param file Where to print. Recommended values are {@link System#out} or {@link System#err}
     *     for standard output/error stream.
     * @param flush Whether to flush the stream after the entire string had been printed.
     */
    @SuppressWarnings("java:S106")
    public record PrintParams(String sep, String end, OutputStream file, boolean flush) {
        /**
         * Default Python {@code print} parameters.
         *
         * @return As described.
         */
        @Contract(" -> new")
        public static @NotNull PrintParams getDefault() {
            return new PrintParams(" ", "\n", System.out, false);
        }
    }

    /**
     * {@link #print(PrintParams, Object...)} with default parameters.
     *
     * @param args As described.
     */
    public static void print(Object... args) {
        print(PrintParams.getDefault(), args);
    }

    /**
     * Python-like {@code print} function.
     *
     * <p>Note, all {@link IOException} will be ignored.
     *
     * <p>Note, this method would not close {@link PrintParams#file()}.
     *
     * @param args Objects to be printed. Will use its {@link Object#toString()} if it is not
     *     {@code null}, and {@code "null"} otherwise.
     * @param pp Print configurations.
     * @see <a href="https://docs.python.org/3/library/functions.html#print">Python interface</a>
     */
    public static void print(@NotNull PrintParams pp, Object @NotNull ... args) {
        int i;
        var sepBytes = pp.sep().getBytes(StandardCharsets.UTF_8);
        var endBytes = pp.end().getBytes(StandardCharsets.UTF_8);
        try {
            for (i = 0; i < args.length - 1; i++) {
                pp.file()
                        .write((args[i] == null ? "null" : args[i].toString())
                                .getBytes(StandardCharsets.UTF_8));
                pp.file().write(sepBytes);
            }
            pp.file()
                    .write((args[i] == null ? "null" : args[i].toString())
                            .getBytes(StandardCharsets.UTF_8));
            pp.file().write(endBytes);

            if (pp.flush()) {
                pp.file().flush();
            }
        } catch (IOException ignored) {
            // Do nothing
        }
    }
}
