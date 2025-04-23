package com.github.yu_zhejian.ystr.container;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Utilities concerning Tuples.
 *
 * @deprecated Using tuples are strongly discouraged.
 */
@Deprecated
public final class Tuple {

    /** Disabled constructor * */
    private Tuple() {}

    public record Tuple1<T1>(T1 e1) {}

    public record Tuple2<T1, T2>(T1 e1, T2 e2) {}

    public record Tuple3<T1, T2, T3>(T1 e1, T2 e2, T3 e3) {}

    public record Tuple4<T1, T2, T3, T4>(T1 e1, T2 e2, T3 e3, T4 e4) {}

    public record Tuple5<T1, T2, T3, T4, T5>(T1 e1, T2 e2, T3 e3, T4 e4, T5 e5) {}

    public record Tuple6<T1, T2, T3, T4, T5, T6>(T1 e1, T2 e2, T3 e3, T4 e4, T5 e5, T6 e6) {}

    public record Tuple7<T1, T2, T3, T4, T5, T6, T7>(
            T1 e1, T2 e2, T3 e3, T4 e4, T5 e5, T6 e6, T7 e7) {}

    public record Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>(
            T1 e1, T2 e2, T3 e3, T4 e4, T5 e5, T6 e6, T7 e7, T8 e8) {}

    @Contract("_ -> new")
    public static <T1> @NotNull Tuple1<T1> of(T1 t1) {
        return new Tuple1<>(t1);
    }

    public static <T1, T2> @NotNull Tuple2<T1, T2> of(T1 t1, T2 t2) {
        return new Tuple2<>(t1, t2);
    }

    public static <T1, T2, T3> @NotNull Tuple3<T1, T2, T3> of(T1 t1, T2 t2, T3 t3) {
        return new Tuple3<>(t1, t2, t3);
    }

    public static <T1, T2, T3, T4> @NotNull Tuple4<T1, T2, T3, T4> of(T1 t1, T2 t2, T3 t3, T4 t4) {
        return new Tuple4<>(t1, t2, t3, t4);
    }

    public static <T1, T2, T3, T4, T5> @NotNull Tuple5<T1, T2, T3, T4, T5> of(
            T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) {
        return new Tuple5<>(t1, t2, t3, t4, t5);
    }

    public static <T1, T2, T3, T4, T5, T6> @NotNull Tuple6<T1, T2, T3, T4, T5, T6> of(
            T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6) {
        return new Tuple6<>(t1, t2, t3, t4, t5, t6);
    }

    public static <T1, T2, T3, T4, T5, T6, T7> @NotNull Tuple7<T1, T2, T3, T4, T5, T6, T7> of(
            T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7) {
        return new Tuple7<>(t1, t2, t3, t4, t5, t6, t7);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8> @NotNull
            Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> of(
                    T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8) {
        return new Tuple8<>(t1, t2, t3, t4, t5, t6, t7, t8);
    }
}
