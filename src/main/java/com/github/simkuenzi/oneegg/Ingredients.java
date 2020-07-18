package com.github.simkuenzi.oneegg;

import java.util.stream.Stream;

public interface Ingredients {
    Stream<Ingredient<ScalarQuantity>> scalar();

    Stream<Ingredient<RangeQuantity>> range();

    Stream<? extends Ingredient<?>> all();

    String asText();
}
