package com.github.simkuenzi.oneegg;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Recipe {

    private final Ingredients ingredients;
    private final ReferenceType referenceType;
    private final int referenceValue;
    private final String referenceName;

    public Recipe(Ingredients ingredients) {
        this.ingredients = ingredients;
        referenceType = ReferenceType.EXACT;
        referenceValue = 1;
        referenceName = "Egg";
    }

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Recipe(@JsonProperty("ingredients") String ingredients,
                  @JsonProperty("referenceType") ReferenceType referenceType,
                  @JsonProperty("referenceValue") int referenceValue,
                  @JsonProperty("referenceName") String referenceName) {
        this.ingredients = new TextIngredients(ingredients);
        this.referenceType = referenceType;
        this.referenceValue = referenceValue;
        this.referenceName = referenceName;
    }

    public Recipe calculate() {
        return new Recipe(new ListIngredients(
                ingredients.all().map(this::map).collect(Collectors.toList()),
                ingredients.scalar().map(this::map).collect(Collectors.toList()),
                ingredients.range().map(this::map).collect(Collectors.toList())));
    }

    public String defaultReference() {
        return ingredients.all().max(Comparator.comparingInt(Ingredient::rank)).map(Ingredient::getProductName).orElse("");
    }

    private <T extends Quantity<T>> Ingredient<T> map(Ingredient<T> ingredient) {
        Optional<Ingredient<ScalarQuantity>> originalReferenceScalar = ingredients.scalar().filter(i -> i.getProductName().equals(referenceName)).findFirst();
        Optional<Ingredient<RangeQuantity>> originalReferenceRange = ingredients.range().filter(i -> i.getProductName().equals(referenceName)).findFirst();

        if (referenceType == ReferenceType.EXACT && originalReferenceScalar.isPresent()) {
            ScalarQuantity scalarReference = new ScalarQuantity(referenceValue);
            return ingredient.calculateWithScalarReference(scalarReference, originalReferenceScalar.get());
        } else if (originalReferenceRange.isPresent()) {
            RangeReference rangeReference = referenceType == ReferenceType.AT_LEAST
                    ? new UpperOpenRange(new ScalarQuantity(referenceValue))
                    : new LowerOpenRange(new ScalarQuantity(referenceValue));
            return ingredient.calculateWithRangeReference(rangeReference, originalReferenceRange.get());
        } else {
            return ingredient;
        }
    }

    public List<Ingredient<?>> getIngredients() {
        return ingredients.all().collect(Collectors.toList());
    }
}
