package com.github.simkuenzi.oneegg;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Recipe {

    private final Ingredients ingredients;

    public Recipe(Ingredients ingredients) {
        this.ingredients = ingredients;
    }

    public Recipe calculate() {
        Optional<Ingredient<ScalarQuantity>> originalReferenceScalar = ingredients.scalar().max(Comparator.comparingInt(Ingredient::rank));
        Optional<Ingredient<RangeQuantity>> originalReferenceRange = ingredients.range().max(Comparator.comparingInt(Ingredient::rank));

        if (originalReferenceScalar.isPresent() || originalReferenceRange.isPresent()) {
            return new Recipe(new ListIngredients(
                    ingredients.all().map(this::map).collect(Collectors.toList()),
                    ingredients.scalar().map(this::map).collect(Collectors.toList()),
                    ingredients.range().map(this::map).collect(Collectors.toList())));
        } else {
            return this;
        }
    }

    public DefaultReference defaultReference() {
        Optional<Ingredient<ScalarQuantity>> originalReferenceScalar = bestReference(ingredients.scalar());
        Optional<Ingredient<RangeQuantity>> originalReferenceRange = bestReference(ingredients.range());

        if (originalReferenceScalar.isPresent() || originalReferenceRange.isPresent()) {
            ScalarQuantity reference = new ScalarQuantity(1, 1);
            if (originalReferenceScalar.isPresent() && (originalReferenceRange.isEmpty() ||
                    originalReferenceScalar.get().rank() >= originalReferenceRange.get().rank())) {
                return new DefaultReference(originalReferenceScalar.get().getProductName(), ReferenceType.SCALAR);
            } else {
                return new DefaultReference(originalReferenceRange.get().getProductName(), ReferenceType.RANGE);
            }
        } else {
            return new DefaultReference("", ReferenceType.SCALAR);
        }
    }

    private <T extends Quantity<T>> Ingredient<T> map(Ingredient<T> ingredient) {
        Optional<Ingredient<ScalarQuantity>> originalReferenceScalar = bestReference(ingredients.scalar());
        Optional<Ingredient<RangeQuantity>> originalReferenceRange = bestReference(ingredients.range());
        if (originalReferenceScalar.isPresent() || originalReferenceRange.isPresent()) {
            ScalarQuantity reference = new ScalarQuantity(1, 1);
            if (originalReferenceScalar.isPresent() && (originalReferenceRange.isEmpty() ||
                    originalReferenceScalar.get().rank() >= originalReferenceRange.get().rank())) {
                return ingredient.calculateWithScalarReference(reference, originalReferenceScalar.get());
            } else {
                return ingredient.calculateWithRangeReference(new UpperOpenRange(reference), originalReferenceRange.get());
            }
        } else {
            return ingredient;
        }
    }

    private <T extends Quantity<T>> Optional<Ingredient<T>> bestReference(Stream<Ingredient<T>> candidates) {
        return candidates.max(Comparator.comparingInt(Ingredient::rank));
    }

    public List<Ingredient<?>> getIngredients() {
        return ingredients.all().collect(Collectors.toList());
    }
}
