package org.labw.gtf_stream;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class GtfAttributesTokenizerWithFSA implements GtfAttributesTokenizerInterface {
    private enum LexerStatus {
        WAITING_FOR_KEY,
        EXTENDING_KEY,
        WAITING_FOR_VALUE,
        EXTENDING_VALUE
    }

    private enum QuotationMarkStatus {
        UNSET,
        SET_LEFT_SINGLE,
        SET_LEFT_DOUBLE
    }

    private enum TokenType {
        KEY,
        VALUE
    }

    /** Generated tokens */
    private final ObjectList<String> tokensForKeys = new ObjectArrayList<>();

    private final ObjectList<String> tokensForValues = new ObjectArrayList<>();
    private String attributes;

    /** Pointer of insertion locus of current attributes bytes */
    private int currentLexerPosition;

    private final StringBuilder currentTokenValue = new StringBuilder();
    private LexerStatus currentLexerStatus;
    private TokenType currentTokenType;
    private TokenType lastTokenType;
    private int quotationStart;
    private QuotationMarkStatus quotationMarkStatus;

    private void reset() {
        tokensForKeys.clear();
        tokensForValues.clear();
        currentLexerPosition = 0;
        currentTokenValue.setLength(0);
        currentLexerStatus = LexerStatus.WAITING_FOR_KEY;
        lastTokenType = TokenType.VALUE;
        resetQuotationMarkStatus();
    }

    private void resetQuotationMarkStatus() {
        quotationStart = 0;
        quotationMarkStatus = QuotationMarkStatus.UNSET;
    }

    public GtfAttributesTokenizerWithFSA() {
        reset();
    }

    private static boolean isResp(final char c) {
        return c == ';';
    }

    private static boolean isBlank(final char c) {
        return c == ' ' || c == '\t';
    }

    private static QuotationMarkStatus isQuote(final char c) {
        switch (c) {
            case '\'' -> {
                return QuotationMarkStatus.SET_LEFT_SINGLE;
            }
            case '\"' -> {
                return QuotationMarkStatus.SET_LEFT_DOUBLE;
            }
            default -> {
                return QuotationMarkStatus.UNSET;
            }
        }
    }

    /** Whether you have another char to get */
    private boolean isPeekable() {
        return currentLexerPosition + 1 < attributes.length();
    }

    /** Get next char */
    private char peek() {
        return attributes.charAt(currentLexerPosition + 1);
    }

    /** Get char at the current locus of attributes string */
    private char get() {
        return attributes.charAt(currentLexerPosition);
    }

    private void addParsedTokenToTokenList() throws GtfParserException {
        if (lastTokenType == currentTokenType) {
            throw new GtfParserException("Get two tokens of a kind (%s) at %d!"
                    .formatted(lastTokenType, currentLexerPosition));
        }
        if (currentTokenType == TokenType.KEY) {
            tokensForKeys.add(currentTokenValue.toString());
        } else {
            tokensForValues.add(currentTokenValue.toString());
        }
        lastTokenType = currentTokenType;
    }

    private void extendCurrentToken(final char getc) {
        currentTokenValue.append(getc);
    }

    private void throwWaitingForValue() throws GtfParserException {
        throw new GtfParserException("Waiting for values at %d".formatted(currentLexerPosition));
    }

    private void throwWaitingForEndQuote() throws GtfParserException {
        throw new GtfParserException("Waiting for end quote (%s) at %d started at %d"
                .formatted(quotationMarkStatus, currentLexerPosition, quotationStart));
    }

    private synchronized void caseWaitForKey(final char getc) throws GtfParserException {
        if (!(isBlank(getc) || isResp(getc))) {
            if (isQuote(getc) != QuotationMarkStatus.UNSET) {
                throw new GtfParserException(
                        "Error at %d: Quotes not allowd in keys.".formatted(currentLexerPosition));
            } else {
                currentLexerStatus = LexerStatus.EXTENDING_KEY;
                currentTokenValue.setLength(0);
                currentTokenType = TokenType.KEY;
                extendCurrentToken(getc);
            }
        }
    }

    private synchronized void caseWaitForValue(final char getc) {
        if (!isBlank(getc)) {
            if (isQuote(getc) != QuotationMarkStatus.UNSET) {
                quotationMarkStatus = isQuote(getc);
                quotationStart = currentLexerPosition;
                currentLexerStatus = LexerStatus.EXTENDING_VALUE;
                currentTokenValue.setLength(0);
                currentTokenType = TokenType.VALUE;
            } else {
                currentLexerStatus = LexerStatus.EXTENDING_VALUE;
                currentTokenValue.setLength(0);
                currentTokenType = TokenType.VALUE;
                extendCurrentToken(getc);
            }
        }
    }

    private synchronized void caseExtendingKey(final char getc) throws GtfParserException {
        if (isBlank(getc)) {
            currentLexerStatus = LexerStatus.WAITING_FOR_VALUE;
            addParsedTokenToTokenList();
        } else {
            extendCurrentToken(getc);
        }
    }

    private synchronized void caseExtendingValue(final char getc) throws GtfParserException {
        if (quotationMarkStatus != QuotationMarkStatus.UNSET) {
            if (quotationMarkStatus == isQuote(getc)) {
                if (isPeekable()) {
                    var peeked = peek();
                    if (isBlank(peeked) || isResp(peeked)) {
                        currentLexerStatus = LexerStatus.WAITING_FOR_KEY;
                        resetQuotationMarkStatus();
                        addParsedTokenToTokenList();
                    } else {
                        throw new GtfParserException(
                                "Unexpected non-whitespace/termination character (%s) after termination of quote at %d"
                                        .formatted(peeked, currentLexerPosition));
                    }
                } else {
                    currentLexerStatus = LexerStatus.WAITING_FOR_KEY;
                    resetQuotationMarkStatus();
                    addParsedTokenToTokenList();
                }
            } else {
                extendCurrentToken(getc);
            }
        } else if (isBlank(getc) || isResp(getc)) {
            currentLexerStatus = LexerStatus.WAITING_FOR_KEY;
            resetQuotationMarkStatus();
            addParsedTokenToTokenList();
        } else {
            extendCurrentToken(getc);
        }
    }

    @Contract("_ -> new")
    @Override
    public synchronized @NotNull GtfAttributesTokens parse(final @NotNull String attributes)
            throws GtfParserException {
        reset();
        this.attributes = attributes;

        char getc;
        while (currentLexerPosition < attributes.length()) {
            getc = get();
            switch (currentLexerStatus) {
                case WAITING_FOR_KEY -> caseWaitForKey(getc);
                case EXTENDING_KEY -> caseExtendingKey(getc);
                case WAITING_FOR_VALUE -> caseWaitForValue(getc);
                case EXTENDING_VALUE -> caseExtendingValue(getc);
            }
            currentLexerPosition += 1;
        }
        switch (currentLexerStatus) {
            case WAITING_FOR_KEY -> {
                // The normal situation
            }
            case EXTENDING_KEY -> {
                if (quotationMarkStatus == QuotationMarkStatus.UNSET) {
                    throwWaitingForValue();
                } else {
                    throwWaitingForEndQuote();
                }
            }
            case WAITING_FOR_VALUE -> throwWaitingForValue();
            case EXTENDING_VALUE -> {
                if (quotationMarkStatus == QuotationMarkStatus.UNSET) {
                    addParsedTokenToTokenList();
                } else {
                    throwWaitingForEndQuote();
                }
            }
        }
        if (tokensForKeys.size() != tokensForValues.size()) {
            throw new GtfParserException("Different number of keys (%d) and values (%d) at %d"
                    .formatted(tokensForKeys.size(), tokensForValues.size(), currentLexerPosition));
        }
        return new GtfAttributesTokens(tokensForKeys, tokensForValues);
    }
}
