package com.github.yu_zhejian.ystr;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.yu_zhejian.ystr.container.Tuple;
import com.github.yu_zhejian.ystr.match.BoyerMooreBCMatch;
import com.github.yu_zhejian.ystr.match.BruteForceMatch;
import com.github.yu_zhejian.ystr.match.KnuthMorrisPrattMatch;
import com.github.yu_zhejian.ystr.match.NaiveMatch;
import com.github.yu_zhejian.ystr.match.RabinKarpMatch;
import com.github.yu_zhejian.ystr.match.ShiftOrMatch;
import com.github.yu_zhejian.ystr.match.StrMatchInterface;
import com.github.yu_zhejian.ystr.match.StrMatchUtilsTest;
import com.github.yu_zhejian.ystr.match.ZMatch;
import com.github.yu_zhejian.ystr.rolling.PolynomialRollingHash;
import com.github.yu_zhejian.ystr.rolling.PrecomputedNtHash;
import com.github.yu_zhejian.ystr.utils.StrUtils;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/** TODO: Add test cases from <a href="https://github.com/smart-tool/smart/">...</a>. */
class StrMatchTest {

    public void testSS(
            @NotNull StrMatchInterface function,
            @NotNull Map<Tuple.Tuple2<String, String>, List<Integer>> testCases) {
        // Test edge cases and illegal inputs
        assertThrows(
                IllegalArgumentException.class,
                () -> function.apply(new byte[0], new byte[0], 1, 0));
        assertThrows(
                IllegalArgumentException.class,
                () -> function.apply(new byte[0], new byte[0], -1, 0));
        assertThrows(
                IllegalArgumentException.class,
                () -> function.apply(new byte[0], new byte[0], 0, 0));
        // Test alternate start/end
        var hayStack = "NNNNATTCCGTAAATTCCAAAATTCCGATTCTCCNNNN".getBytes(StandardCharsets.UTF_8);
        var needle = "TTCC".getBytes(StandardCharsets.UTF_8);

        assertIterableEquals(
                List.of(1 + 4, 10 + 4, 18 + 4),
                function.applyUnchecked(hayStack, needle, 4, hayStack.length));

        assertIterableEquals(
                List.of(10 + 4, 18 + 4),
                function.applyUnchecked(hayStack, needle, 6, hayStack.length));
        assertIterableEquals(List.of(10 + 4), function.applyUnchecked(hayStack, needle, 6, 18));

        // Test real strings
        for (var entry : testCases.entrySet()) {
            var key = entry.getKey();
            var value = entry.getValue();
            hayStack = key.e1().getBytes(StandardCharsets.UTF_8);
            needle = key.e2().getBytes(StandardCharsets.UTF_8);
            assertIterableEquals(
                    value,
                    function.applyUnchecked(hayStack, needle, 0, hayStack.length),
                    "Error at case %s in %s".formatted(key.e2(), key.e1()));
        }
    }

    @Test
    void bruteForceMatch() {
        assertThrows(
                IllegalArgumentException.class,
                () -> StrMatchInterface.convenientApply(
                        BruteForceMatch::new, new byte[0], new byte[0]));
        testSS(new BruteForceMatch(), StrMatchUtilsTest.TEST_CASES_AGCT);
        testSS(new BruteForceMatch(), StrMatchUtilsTest.TEST_CASES_STRANGE_ENCODING);
    }

    @Test
    void naiveMatch() {
        testSS(new NaiveMatch(), StrMatchUtilsTest.TEST_CASES_AGCT);
        testSS(new NaiveMatch(), StrMatchUtilsTest.TEST_CASES_STRANGE_ENCODING);
    }

    @Test
    void rabinKarpMatch() {
        testSS(new RabinKarpMatch(), StrMatchUtilsTest.TEST_CASES_AGCT);
        testSS(new RabinKarpMatch(), StrMatchUtilsTest.TEST_CASES_STRANGE_ENCODING);
    }

    @Test
    void rabinKarpMatchUsingRandomPrime() {
        testSS(
                new RabinKarpMatch(new PolynomialRollingHash(
                        PolynomialRollingHash.longRandomPrime(), StrUtils.ALPHABET_SIZE)),
                StrMatchUtilsTest.TEST_CASES_AGCT);
    }

    @Test
    void rabinKarpMatchUsingNtHash() {
        testSS(new RabinKarpMatch(new PrecomputedNtHash()), StrMatchUtilsTest.TEST_CASES_AGCT);
    }

    @Test
    void knuthMorrisPrattMatch() {
        testSS(new KnuthMorrisPrattMatch(), StrMatchUtilsTest.TEST_CASES_AGCT);
        testSS(new KnuthMorrisPrattMatch(), StrMatchUtilsTest.TEST_CASES_STRANGE_ENCODING);
    }

    @Test
    void shiftOrMatch() {
        testSS(new ShiftOrMatch(), StrMatchUtilsTest.TEST_CASES_AGCT);
        testSS(new ShiftOrMatch(), StrMatchUtilsTest.TEST_CASES_STRANGE_ENCODING);
    }

    @Test
    void boyerMooreBadCharacterRuleOnlyMatch() {
        testSS(new BoyerMooreBCMatch(), StrMatchUtilsTest.TEST_CASES_AGCT);
        testSS(new BoyerMooreBCMatch(), StrMatchUtilsTest.TEST_CASES_STRANGE_ENCODING);
    }

    @Test
    void zMatch() {
        testSS(new ZMatch((byte) '$'), StrMatchUtilsTest.TEST_CASES_AGCT);
    }
}
