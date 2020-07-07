package com.github.simkuenzi.oneegg;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class IngredientTest {

    @Test
    public void rankEier() {
        assertEquals(500, new Ingredient<>(new DecimalQuantity(BigDecimal.ONE), "Eier").rank());
    }

    @Test
    public void rankEigelb() {
        assertEquals(400, new Ingredient<>(new DecimalQuantity(BigDecimal.ONE), "Eigelb").rank());
    }
}