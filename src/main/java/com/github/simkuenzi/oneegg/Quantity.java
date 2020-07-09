package com.github.simkuenzi.oneegg;

public interface Quantity<T extends Quantity<T>> {
    String asText();

    String asDecimalText();

    Dividend<T, ScalarQuantity> multiplyScalar(ScalarQuantity multiplicand);

    Dividend<T, RangeQuantity> multiplyRange(RangeReference multiplicand);
}
