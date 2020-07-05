package com.github.simkuenzi.oneegg;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Ingredient<T extends Quantity<T>> {
    private static final Pattern WORD_PATTERN = Pattern.compile("[^\\p{Punct}\\s\\h]+");

    private final T quantity;
    private final String productName;

    public Ingredient(T quantity, String productName) {
        this.quantity = quantity;
        this.productName = productName;
    }

    public boolean sameProduct(Ingredient<?> other) {
        return productName.equals(other.productName);
    }

    public <U extends Quantity<U>> Ingredient<? extends Quantity<?>> calculateFor(Ingredient<U> reference, Ingredient<U> originalReference) {
        return new Ingredient<>(this.quantity.multiply(reference.quantity.divide(originalReference.quantity)), productName);
    }

    public int rank() {
        Matcher matcher = WORD_PATTERN.matcher(productName);
        Map<Integer, Integer> rankByReference = new HashMap<>();
        String[] references = {"ei", "eier", "eigelb", "eiweiss", "eiklar", "egg"};

        while (matcher.find()) {
            String find = productName.substring(matcher.start(), matcher.end());
            for (int i = 0; i < references.length; i++) {
                if (find.toLowerCase().contains(references[i])) {
                    int rank = (int) (1d / (find.length() - references[i].length()) * 100) * (references.length - i);
                    if (!rankByReference.containsKey(i) || rankByReference.get(i) < rank)
                    rankByReference.put(i, rank);
                }
            }
        }
        return rankByReference.values().stream().max(Comparator.comparingInt(v -> v)).orElse(0);
    }

    public Ingredient<T> asUnit() {
        return new Ingredient<>(quantity.unit(), productName);
    }

    @Override
    public String toString() {
        return quantity.asText() + " " + productName;
    }
}
