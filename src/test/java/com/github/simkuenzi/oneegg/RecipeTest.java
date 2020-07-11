package com.github.simkuenzi.oneegg;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

public class RecipeTest {

    @Test
    public void calculate() {
        assertRecipeEquals(Arrays.asList(
                        ingredient("1 ⁷/₈", "1.88", "dl\tMilchwasser (halb Milch/halb Wasser)"),
                        ingredient("³⁄₁₆", "0.188", "TL\tSalz"),
                        ingredient("37 ¹/₂", "37.5", "g\tHartweizengriess"),
                        ingredient("1", "1.00", "Eigelb"),
                        ingredient("37 ¹/₂", "37.5", "g\tMehl"),
                        ingredient("12 ¹/₂", "12.5", "g\tSbrinz AOP, gerieben"),
                        ingredient("1 - 1 ¹/₄", "1.00 - 1.25", "EL\tPaniermehl, zum Wenden, nach Belieben")),
                new Recipe("" +
                        "7,5 dl\tMilchwasser (halb Milch/halb Wasser)\n" +
                        "¾ TL\tSalz\n" +
                        "150 g\tHartweizengriess\n" +
                        "4 \tEigelb\n" +
                        "150 g\tMehl\n" +
                        "50 g\tSbrinz AOP, gerieben\n" +
                        " \tMuskatnuss\n" +
                        "4 - 5 EL\tPaniermehl, zum Wenden, nach Belieben\n" +
                        " \tBratbutter oder Bratcrème",
                        ReferenceType.EXACT, 1, "Eigelb")
                        .calculate());
    }

    @Test
    public void calculateEierOverEigelb() {
        assertRecipeEquals(Arrays.asList(
                        ingredient("¹⁄₂", "0.500", "Eigelb"),
                        ingredient("³⁄₁₆", "0.188", "TL Salz"),
                        ingredient("1", "1.00", "Eier")),
                new Recipe("" +
                        "2 Eigelb\n" +
                        "¾ TL Salz\n" +
                        "4 Eier\n",
                        ReferenceType.EXACT, 1, "Eier")
                        .calculate());
    }

    @Test
    public void calculateNotQuiteOneEggBug() {
        assertRecipeEquals(Arrays.asList(
                        ingredient("¹⁄₁₆", "0.0625", "TL Salz"),
                        ingredient("1", "1.00",  "Eier")),
                new Recipe("" +
                        "¾ TL Salz\n" +
                        "12 Eier\n",
                         ReferenceType.EXACT, 1, "Eier")
                        .calculate());
    }

    @Test
    public void calculateRange() {
        assertRecipeEquals(Arrays.asList(
                ingredient("100", "100", "g \tMehl"),
                ingredient("1 - 2 ¹/₃", "1.00 - 2.33",  "Eier")),
                new Recipe("" +
                                "300 g \tMehl\n" +
                                "3-7  \tEier\n",
                        ReferenceType.AT_LEAST, 1, "Eier")
                        .calculate());
    }

    private void assertRecipeEquals(List<Consumer<Ingredient<?>>> expected, Recipe actual) {
        assertEquals(expected.size(), actual.getIngredients().size());
        IntStream.range(0, expected.size()).forEach(i -> expected.get(i).accept(actual.getIngredients().get(i)));
    }

    private Consumer<Ingredient<?>> ingredient(String quantity, String quantityDec, String productName) {
        return i -> {
            assertEquals(quantity, i.getQuantity());
            assertEquals(quantityDec, i.getQuantityDec());
            assertEquals(productName, i.getProductName());
        };
    }
}