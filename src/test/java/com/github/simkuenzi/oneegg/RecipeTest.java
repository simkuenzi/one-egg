package com.github.simkuenzi.oneegg;

import org.junit.Test;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
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

    @Test
    public void kochkinoDe() {
        assertRecipeEquals(Arrays.asList(
                ingredient("66 ²/₃", "66.7", "Mehl"),
                ingredient("1", "1.00", "Eier"),
                ingredient("116 ²/₃", "117", "Milch"),
                ingredient("¹⁄₆", "0.167",  "Salz")),
                new Recipe("" +
                        "\n" +
                        "    200g Mehl\n" +
                        "    3 Eier\n" +
                        "    350ml Milch\n" +
                        "    1/2TL Salz\n" +
                        "    etwas Butter, Öl oder Margarine zum Ausstreichen der Pfanne\n" +
                        "    Optional: Kräuter oder Gewürze schmecken ganz toll im Teig, wenn Ihr die Pfannkuchen später mit Zum Beispiel Käse, Kräuterquark oder Schinken füllen wollt. Wenn Ihr sie süß essen wollt, passt Vanillezucker sehr gut in den Teig oder auch Rosinen oder Apfelstückchen.\n",
                        ReferenceType.EXACT, 1, "Eier")
                        .calculate());
    }

    @Test
    public void chefkochDe() {
        assertRecipeEquals(Arrays.asList(
                ingredient("1", "1.00", "Scheibe/n \tWeißbrot, (oder Toast)"),
                ingredient("¹⁄₂", "0.500", "Zehe/n \tKnoblauch"),
                ingredient("10", "10.0", "g \tButter"),
                ingredient("5", "5.00", "g \tMehl"),
                ingredient("¹⁄₄", "0.250", "Liter \tFleischbrühe"),
                ingredient("⁶⁄₁₀₀", "0.0600", "Liter \tWein, weiß"),
                ingredient("25", "25.0", "g \tKäse (geriebener Hartkäse)"),
                ingredient("125", "125",  "g \tZwiebel(n) ")),
                new Recipe("" +
                        " 1 Scheibe/n \tWeißbrot, (oder Toast)\n" +
                        "½ Zehe/n \tKnoblauch\n" +
                        "10 g \tButter\n" +
                        "\tSalz und Pfeffer, schwarzer\n" +
                        "5 g \tMehl\n" +
                        "¼ Liter \tFleischbrühe\n" +
                        "0,06 Liter \tWein, weiß\n" +
                        "25 g \tKäse (geriebener Hartkäse)\n" +
                        "125 g \tZwiebel(n) ",
                        ReferenceType.AT_LEAST, 1, "Eier")
                        .calculate());
    }

    @Test
    public void guteKuecheCh() {
        assertRecipeEquals(Arrays.asList(
                ingredient("30", "30.0", "g \tButter (Sauerrahmbutter)"),
                ingredient("150", "150", "g \tgelbe Peperoni"),
                ingredient("800", "800", "g \tKohlrabi"),
                ingredient("150", "150", "g \tMais"),
                ingredient("200", "200", "g \tRüebli"),
                ingredient("100", "100", "g \tSpeck, durchwachsen, geräuchert"),
                ingredient("150", "150", "g \tZwiebeln"),
                ingredient("1", "1.00", "TL \tButter zum Einfetten der Auflaufform"),
                ingredient("1", "1.00", "Stk \tEi"),
                ingredient("200", "200", "ml \tGemüsebrühe/Bouillon"),
                ingredient("150", "150", "g \tGouda Käse"),
                ingredient("1", "1.00", "EL \tKerbel"),
                ingredient("1", "1.00", "Prise \tMuskatnuss"),
                ingredient("1", "1.00", "Prise \tSalz und Pfeffer"),
                ingredient("200", "200", "g \tSchlagrahm"),
                ingredient("2", "2.00", "TL \tWeizenmehl ")),
                new Recipe("" +
                        "30 \tg \tButter (Sauerrahmbutter)\n" +
                        "150 \tg \tgelbe Peperoni\n" +
                        "800 \tg \tKohlrabi\n" +
                        "150 \tg \tMais\n" +
                        "200 \tg \tRüebli\n" +
                        "100 \tg \tSpeck, durchwachsen, geräuchert\n" +
                        "150 \tg \tZwiebeln\n" +
                        "Zutaten Sauce\n" +
                        "1 \tTL \tButter zum Einfetten der Auflaufform\n" +
                        "1 \tStk \tEi\n" +
                        "200 \tml \tGemüsebrühe/Bouillon\n" +
                        "150 \tg \tGouda Käse\n" +
                        "1 \tEL \tKerbel\n" +
                        "1 \tPrise \tMuskatnuss\n" +
                        "1 \tPrise \tSalz und Pfeffer\n" +
                        "200 \tg \tSchlagrahm\n" +
                        "2 \tTL \tWeizenmehl ",
                        ReferenceType.AT_LEAST, 1, "Eier")
                        .calculate());
    }

    @Test
    public void getIngredientsText() {
        assertEquals("" +
                        "¹⁄₂ Eigelb\n" +
                        "³⁄₁₆ TL Salz\n" +
                        "1 Eier\n",
                new Recipe("" +
                        "2 Eigelb\n" +
                        "¾ TL Salz\n" +
                        "4 Eier\n",
                        ReferenceType.EXACT, 1, "Eier")
                        .calculate().getIngredientsText());
    }


    private void assertRecipeEquals(List<Consumer<Ingredient<?>>> expected, Recipe actual) {
        assertEquals(expected.size(), actual.getIngredients().size());
        IntStream.range(0, expected.size()).forEach(i -> expected.get(i).accept(actual.getIngredients().get(i)));
    }

    private Consumer<Ingredient<?>> ingredient(String quantity, String quantityDec, String productName) {
        return i -> {
            Function<String, String> message = (x) -> String.format("%s not equals for %s %s.", x, quantity, productName);
            assertEquals(message.apply("productName"), Normalizer.normalize(productName, Normalizer.Form.NFKD), i.getProductName());
            assertEquals(message.apply("quantity"), quantity, i.getQuantity());
            assertEquals(message.apply("quantityDec"), quantityDec, i.getQuantityDec());
        };
    }
}