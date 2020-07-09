package com.github.simkuenzi.oneegg;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Ingredients {
    private static final String COUNT_INT_GRP = "countInt";
    private static final String COUNT_DEC_GRP = "countDec";
    private static final String DENOM_INT_GRP = "denomInt";
    private static final String DENOM_DEC_GRP = "denomDec";
    private static final String COUNT_TO_INT_GRP = "countToInt";
    private static final String COUNT_TO_DEC_GRP = "countToDec";
    private static final String DENOM_TO_INT_GRP = "denomToInt";
    private static final String DENOM_TO_DEC_GRP = "denomToDec";
    private static final String PRODUCT_GRP = "product";

    private static final String FRACTION_SLASH = "\u2044";
    private static final String DEC_REGEX_FMT = "(?<%s>\\d+)(?:[.,](?<%s>\\d+))?";
    private static final String NUM_REGEX_FMT = DEC_REGEX_FMT + "(?:[/" + FRACTION_SLASH + "]" + DEC_REGEX_FMT + ")?";
    private static final Pattern LINE_PATTERN = Pattern.compile(String.format(NUM_REGEX_FMT + "(?:\\h*-\\h*" + NUM_REGEX_FMT + ")?\\h+(?<" + PRODUCT_GRP +">.*)",
            COUNT_INT_GRP, COUNT_DEC_GRP, DENOM_INT_GRP, DENOM_DEC_GRP, COUNT_TO_INT_GRP, COUNT_TO_DEC_GRP,
            DENOM_TO_INT_GRP, DENOM_TO_DEC_GRP));
    private static final Pattern RANGE_PATTERN = Pattern.compile(String.format(NUM_REGEX_FMT + "\\h*-\\h*" + NUM_REGEX_FMT + "\\h+(?<" + PRODUCT_GRP +">.*)",
            COUNT_INT_GRP, COUNT_DEC_GRP, DENOM_INT_GRP, DENOM_DEC_GRP, COUNT_TO_INT_GRP, COUNT_TO_DEC_GRP,
            DENOM_TO_INT_GRP, DENOM_TO_DEC_GRP));
    private static final Pattern SCALAR_PATTERN = Pattern.compile(String.format(NUM_REGEX_FMT + "\\h+(?<" + PRODUCT_GRP +">.*)",
            COUNT_INT_GRP, COUNT_DEC_GRP, DENOM_INT_GRP, DENOM_DEC_GRP));

    private final String text;

    public Ingredients(String text) {
        this.text = text;
    }

    public Ingredients calculate() {
        Optional<Ingredient<ScalarQuantity>> originalReferenceScalar = scalar().max(Comparator.comparingInt(Ingredient::rank));
        Optional<Ingredient<RangeQuantity>> originalReferenceRange = range().max(Comparator.comparingInt(Ingredient::rank));

        if (originalReferenceScalar.isPresent() || originalReferenceRange.isPresent()) {
            Function<Ingredient<?>, Ingredient<?>> mapping;
            ScalarQuantity reference = new ScalarQuantity(1, 1);
            if (originalReferenceScalar.isPresent() && (originalReferenceRange.isEmpty() ||
                    originalReferenceScalar.get().rank() >= originalReferenceRange.get().rank())) {
                mapping = i -> i.calculateWithScalarReference(reference, originalReferenceScalar.get());
            } else {
                mapping = i -> i.calculateWithRangeReference(new UpperOpenRange(reference), originalReferenceRange.get());
            }
            StringWriter writer = new StringWriter();
            PrintWriter out = new PrintWriter(writer);
            all().map(mapping).map(Ingredient::asText).forEach(out::println);
            out.flush();
            return new Ingredients(writer.toString());
        } else {
            return this;
        }
    }

    public String asText() {
        return text;
    }

    private Stream<Ingredient<ScalarQuantity>> scalar() {
        return asStream(SCALAR_PATTERN, this::parseScalarIngredient);
    }

    private Stream<Ingredient<RangeQuantity>> range() {
        return asStream(RANGE_PATTERN, this::parseRangeIngredient);
    }

    public Stream<? extends Ingredient<?>> all() {
        return asStream(LINE_PATTERN, this::parseIngredient);
    }

    private <T extends Quantity<T>> Stream<Ingredient<T>> asStream(Pattern pattern, Function<Matcher, Ingredient<T>> parser) {
        return Arrays.stream(text.split("\r?\n"))
                .map(l -> Normalizer.normalize(l, Normalizer.Form.NFKD))
                .map(pattern::matcher)
                .filter(Matcher::matches)
                .map(parser);
    }

    private Ingredient<? extends Quantity<?>> parseIngredient(Matcher matcher) {
        return matcher.group(COUNT_TO_INT_GRP) != null || matcher.group(COUNT_TO_DEC_GRP) != null
                ? parseRangeIngredient(matcher)
                : parseScalarIngredient(matcher);
    }

    private Ingredient<ScalarQuantity> parseScalarIngredient(Matcher matcher) {
        String countInt = matcher.group(COUNT_INT_GRP);
        String countDec = matcher.group(COUNT_DEC_GRP);
        String denomInt = matcher.group(DENOM_INT_GRP);
        String denomDec = matcher.group(DENOM_DEC_GRP);
        String product = matcher.group(PRODUCT_GRP);

        return new Ingredient<>(parseScalarQuantity(countInt, countDec, denomInt, denomDec), product);
    }

    private Ingredient<RangeQuantity> parseRangeIngredient(Matcher matcher) {
        String countInt = matcher.group(COUNT_INT_GRP);
        String countDec = matcher.group(COUNT_DEC_GRP);
        String denomInt = matcher.group(DENOM_INT_GRP);
        String denomDec = matcher.group(DENOM_DEC_GRP);
        String countToInt = matcher.group(COUNT_TO_INT_GRP);
        String countToDec = matcher.group(COUNT_TO_DEC_GRP);
        String denomToInt = matcher.group(DENOM_TO_INT_GRP);
        String denomToDec = matcher.group(DENOM_TO_DEC_GRP);
        String product = matcher.group(PRODUCT_GRP);

        return new Ingredient<>(parseRangeQuantity(countInt, countDec, denomInt, denomDec, countToInt, countToDec, denomToInt, denomToDec), product);
    }

    private RangeQuantity parseRangeQuantity(String countInt, String countDec, String denomInt, String denomDec,
                                   String countToInt, String countToDec, String denomToInt, String denomToDec) {
        return new RangeQuantity(parseScalarQuantity(countInt, countDec, denomInt, denomDec), parseScalarQuantity(countToInt, countToDec, denomToInt, denomToDec));
    }

    private ScalarQuantity parseScalarQuantity(String countInt, String countDec, String denomInt, String denomDec) {
        return denomInt != null || denomDec != null
                ? new ScalarQuantity(parseDecimal(countInt, countDec), parseDecimal(denomInt, denomDec))
                : new ScalarQuantity(parseDecimal(countInt, countDec), BigDecimal.ONE);
    }

    private BigDecimal parseDecimal(String integerPart, String decimalPlaces) {
        integerPart = integerPart != null ? integerPart : "";
        decimalPlaces = decimalPlaces != null ? decimalPlaces : "";
        return new BigDecimal(new BigInteger(integerPart + decimalPlaces), decimalPlaces.length());
    }
}
