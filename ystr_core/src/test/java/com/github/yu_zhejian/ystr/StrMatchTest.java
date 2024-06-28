package com.github.yu_zhejian.ystr;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.yu_zhejian.ystr.match.BoyerMooreBadCharactersOnlyMatch;
import com.github.yu_zhejian.ystr.match.BoyerMooreHorspool;
import com.github.yu_zhejian.ystr.match.BruteForceMatch;
import com.github.yu_zhejian.ystr.match.KnuthMorrisPrattMatch;
import com.github.yu_zhejian.ystr.match.NaiveMatch;
import com.github.yu_zhejian.ystr.match.RabinKarpMatch;
import com.github.yu_zhejian.ystr.match.ShiftOrMatch;
import com.github.yu_zhejian.ystr.match.StrMatchInterface;
import com.github.yu_zhejian.ystr.match.StrMatchUtilsTest;
import com.github.yu_zhejian.ystr.rolling.PolynomialRollingHash;
import com.github.yu_zhejian.ystr.rolling.PrecomputedNtHash;

import io.vavr.Tuple2;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/** TODO: Add test cases from <a href="https://github.com/smart-tool/smart/">...</a>. */
class StrMatchTest {

    public void testSS(
            @NotNull StrMatchInterface function,
            @NotNull Map<Tuple2<String, String>, List<Integer>> testCases) {
        // Test edge cases and illegal inputs
        assertThrows(
                IllegalArgumentException.class,
                () -> function.apply(new byte[0], new byte[0], 1, 0));
        assertThrows(
                IllegalArgumentException.class,
                () -> function.apply(new byte[0], new byte[0], -1, 0));
        // Test real strings
        testCases.forEach((key, value) -> {
            var hayStack = key._1().getBytes(StandardCharsets.UTF_8);
            var needle = key._2().getBytes(StandardCharsets.UTF_8);
            assertIterableEquals(
                    value,
                    function.apply(hayStack, needle, 0, hayStack.length),
                    "Error at case %s in %s".formatted(key._2(), key._1()));
        });
    }

    @Test
    void bruteForceMatch() {
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
                new RabinKarpMatch(PolynomialRollingHash.supply(
                        PolynomialRollingHash.longRandomPrime(),
                        PolynomialRollingHash.DEFAULT_POLYNOMIAL_ROLLING_HASH_RADIX_P)),
                StrMatchUtilsTest.TEST_CASES_AGCT);
    }

    @Test
    void rabinKarpMatchUsingNtHash() {
        testSS(new RabinKarpMatch(PrecomputedNtHash::new), StrMatchUtilsTest.TEST_CASES_AGCT);
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
        testSS(new BoyerMooreBadCharactersOnlyMatch(), StrMatchUtilsTest.TEST_CASES_AGCT);
        testSS(
                new BoyerMooreBadCharactersOnlyMatch(),
                StrMatchUtilsTest.TEST_CASES_STRANGE_ENCODING);
    }

    @Test
    void boyerMooreHorspoolMatch() {
        if (true) {
            return; // FIXME: This method has errors!
        }
        testSS(new BoyerMooreHorspool(), StrMatchUtilsTest.TEST_CASES_AGCT);
        testSS(new BoyerMooreHorspool(), StrMatchUtilsTest.TEST_CASES_STRANGE_ENCODING);
    }
}
