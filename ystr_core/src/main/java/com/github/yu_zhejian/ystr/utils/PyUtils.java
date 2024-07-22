package com.github.yu_zhejian.ystr.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

/** Utilities mimicking Python functions. */
public final class PyUtils {

    private PyUtils() {}

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
    public record PrintParams(byte[] sep, byte[] end, @NotNull OutputStream file, boolean flush) {

        /**
         * Constructor that uses strings.
         *
         * @param sep As described.
         * @param end As described.
         * @param file As described.
         * @param flush As described.
         */
        public PrintParams(
                @NotNull String sep, @NotNull String end, OutputStream file, boolean flush) {
            this(
                    sep.getBytes(StandardCharsets.UTF_8),
                    end.getBytes(StandardCharsets.UTF_8),
                    file,
                    flush);
        }

        @Contract(pure = true)
        @Override
        public @NotNull String toString() {
            return "PrintParams{sep=%s, end=%s, flush=%s}"
                    .formatted(
                            new String(sep, StandardCharsets.UTF_8),
                            new String(end, StandardCharsets.UTF_8),
                            flush);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof PrintParams that)) {
                return false;
            }
            return flush == that.flush
                    && Objects.deepEquals(sep, that.sep)
                    && Objects.deepEquals(end, that.end);
        }

        @Override
        public int hashCode() {
            return Objects.hash(Arrays.hashCode(sep), Arrays.hashCode(end), flush);
        }

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
        final var sepBytes = pp.sep();
        final var endBytes = pp.end();
        final var f = pp.file();
        try {
            for (i = 0; i < args.length - 1; i++) {
                f.write((args[i] == null ? "null" : args[i].toString()).getBytes(StandardCharsets.UTF_8));
                f.write(sepBytes);
            }
            if (args.length != 0){
                f.write((args[i] == null ? "null" : args[i].toString()).getBytes(StandardCharsets.UTF_8));
            }
            f.write(endBytes);

            if (pp.flush()) {
                f.flush();
            }
        } catch (IOException ignored) {
            // Do nothing
        }
    }
}
