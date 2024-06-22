package com.github.yu_zhejian.ystr.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

class FastxRecordTest {

    @Test
    void test() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new FastxRecord("A", new byte[] {'A', 'A'}, new byte[] {'A'}));
        var far = FastxRecord.ofStrings("A", "AGCT");
        var fqr = FastxRecord.ofStrings("A", "AGCT", "!!!!");
        assertEquals(4, far.getLength());
        assertEquals(far, fqr.toFasta());
        assertEquals(fqr, far.toFastq((byte) '!'));
        assertEquals(far.hashCode(), fqr.toFasta().hashCode());
        assertEquals(fqr.hashCode(), far.toFastq((byte) '!').hashCode());
        assertEquals(fqr, far.toFastq("!!!!".getBytes(StandardCharsets.US_ASCII)));
    }
}
