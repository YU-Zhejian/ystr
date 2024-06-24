package com.github.yu_zhejian.ystr.minimizer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MinimizerRingBufferTest {
    @Test
    void test() {
        // [M, M, M, M]
        // [9, 9, 9, 9]
        var mrb = new MinimizerRingBuffer(4);
        assertEquals(0, mrb.getCurrentMinimizer());
        // [15, M, M, M]
        // [10, 9, 9, 9]
        mrb.add(15L, 10);
        mrb.resetMinAndPos();
        assertEquals(10, mrb.getCurrentMinimizer());
        // [15, 13, M, M]
        // [10, 11, 9, 9]
        mrb.add(13L, 11);
        assertEquals(11, mrb.getCurrentMinimizer());
        // [15, 13, 23, M]
        // [10, 11, 12, 9]
        mrb.add(23L, 12);
        assertEquals(11, mrb.getCurrentMinimizer());
        // [15, 13, 23, 13]
        // [10, 11, 12, 13]
        mrb.add(13L, 13);
        assertEquals(11, mrb.getCurrentMinimizer());
        // [13, 13, 23, 13]
        // [14, 11, 12, 13]
        mrb.add(13L, 14);
        assertEquals(11, mrb.getCurrentMinimizer());
        // [13, 13, 23, 13]
        // [14, 15, 12, 13]
        mrb.add(13L, 15);
        assertEquals(13, mrb.getCurrentMinimizer());
        // [13, 13, 11, 13]
        // [14, 15, 16, 13]
        mrb.add(11L, 16);
        assertEquals(16, mrb.getCurrentMinimizer());
        // [13, 13, 11, M]
        // [14, 15, 16, 17]
        mrb.add(MinimizerRingBuffer.MAX_ULONG, 17);
        assertEquals(16, mrb.getCurrentMinimizer());
        // [M, 13, 11, M]
        // [18, 15, 16, 17]
        mrb.add(MinimizerRingBuffer.MAX_ULONG, 18);
        assertEquals(16, mrb.getCurrentMinimizer());
        // [M, M, 11, M]
        // [18, 19, 16, 17]
        mrb.add(MinimizerRingBuffer.MAX_ULONG, 19);
        assertEquals(16, mrb.getCurrentMinimizer());
        // [M, M, M, M]
        // [18, 19, 20, 17]
        mrb.add(MinimizerRingBuffer.MAX_ULONG, 20);
        assertEquals(17, mrb.getCurrentMinimizer());
    }
}
