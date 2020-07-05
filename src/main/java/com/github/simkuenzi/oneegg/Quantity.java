package com.github.simkuenzi.oneegg;

public interface Quantity<T extends Quantity<T>> {
    String asText();

    T multiply(ScalarQuantity multiplicand);

    ScalarQuantity divide(T quantity);

    T unit();
}
