package com.github.simkuenzi.oneegg;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IngredientsTest {

    @Test
    public void calculate() {
        assertEquals("" +
                        "1 7/8 dl\tMilchwasser (halb Milch/halb Wasser)\n" +
                        "3/16 TL\tSalz\n" +
                        "37 1/2 g\tHartweizengriess\n" +
                        "1 Eigelb\n" +
                        "37 1/2 g\tMehl\n" +
                        "12 1/2 g\tSbrinz AOP, gerieben\n" +
                        "1 - 1 1/4 EL\tPaniermehl, zum Wenden, nach Belieben\n",
                new Ingredients(
                        "7,5 dl\tMilchwasser (halb Milch/halb Wasser)\n" +
                        "¾ TL\tSalz\n" +
                        "150 g\tHartweizengriess\n" +
                        "4 \tEigelb\n" +
                        "150 g\tMehl\n" +
                        "50 g\tSbrinz AOP, gerieben\n" +
                        " \tMuskatnuss\n" +
                        "4 - 5 EL\tPaniermehl, zum Wenden, nach Belieben\n" +
                        " \tBratbutter oder Bratcrème")
                        .calculate().asText());
    }

    @Test
    public void calculateEierOverEigelb() {
        assertEquals("" +
                        "1/2 Eigelb\n" +
                        "3/16 TL Salz\n" +
                        "1 Eier\n",
                new Ingredients(
                        "2 Eigelb\n" +
                        "¾ TL Salz\n" +
                        "4 Eier\n")
                        .calculate().asText());
    }

    @Test
    public void calculateNotQuiteOneEggBug() {
        assertEquals("" +
                        "1/16 TL Salz\n" +
                        "1 Eier\n",
                new Ingredients(
                        "¾ TL Salz\n" +
                        "12 Eier\n")
                        .calculate().asText());
    }
}