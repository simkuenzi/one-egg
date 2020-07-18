package com.github.simkuenzi.oneegg;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.stream.Stream;

public class ListIngredients implements Ingredients {

    private final List<Ingredient<?>> all;
    private final List<Ingredient<ScalarQuantity>> scalar;
    private final List<Ingredient<RangeQuantity>> range;

    public ListIngredients(List<Ingredient<?>> all, List<Ingredient<ScalarQuantity>> scalar, List<Ingredient<RangeQuantity>> range) {
        this.all = all;
        this.scalar = scalar;
        this.range = range;
    }

    @Override
    public Stream<Ingredient<ScalarQuantity>> scalar() {
        return scalar.stream();
    }

    @Override
    public Stream<Ingredient<RangeQuantity>> range() {
        return range.stream();
    }

    @Override
    public Stream<? extends Ingredient<?>> all() {
        return all.stream();
    }

    @Override
    public String asText() {
        StringWriter writer = new StringWriter();
        PrintWriter out = new PrintWriter(writer);
        all().map(Ingredient::asText).forEach(out::println);
        out.flush();
        return writer.toString();
    }
}
