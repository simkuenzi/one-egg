package com.github.simkuenzi.oneegg;

import io.javalin.http.Context;

import java.util.*;
import java.util.stream.Collectors;

public class Recipe {

    private final Ingredients ingredients;
    private final ReferenceType referenceType;
    private final int referenceValue;
    private final String referenceName;
    private final String ingredientsText;

    public Recipe() {
        this(new ListIngredients(Collections.emptyList(), Collections.emptyList(), Collections.emptyList()), ReferenceType.EXACT, 1, "", "");
    }

    public Recipe(Context httpContext) {
        this(
                httpContext.formParam("ingredients"),
                ReferenceType.valueOf(httpContext.formParam("referenceType")),
                Integer.parseInt(Objects.requireNonNull(httpContext.formParam("referenceValue", "0"))),
                httpContext.formParam("referenceName"));
    }

    public Recipe(Ingredients ingredients) {
        this(ingredients, ReferenceType.EXACT, 1, "", "");
    }

    public Recipe(String ingredients, ReferenceType referenceType, int referenceValue, String referenceName) {
        this(new TextIngredients(ingredients), referenceType, referenceValue, referenceName, ingredients);
    }

    public Recipe(Ingredients ingredients, ReferenceType referenceType, int referenceValue, String referenceName, String ingredientsText) {
        this.ingredients = ingredients;
        this.referenceType = referenceType;
        this.referenceValue = referenceValue;
        this.referenceName = referenceName;
        this.ingredientsText = ingredientsText;
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

    public ReferenceType getReferenceType() {
        return referenceType;
    }

    public int getReferenceValue() {
        return referenceValue;
    }

    public String getReferenceName() {
        return referenceName;
    }

    public String getIngredientsText() {
        return ingredientsText;
    }
}
