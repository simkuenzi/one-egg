package com.github.simkuenzi.oneegg;

public interface RangeReference {
    <T extends Quantity<T>> Dividend<T, RangeQuantity> multiply(T multiplicand);

    Dividend<ScalarQuantity, RangeQuantity> multiplyScalar(ScalarQuantity multiplicand);

    Dividend<RangeQuantity, RangeQuantity> multiplyRange(RangeQuantity multiplicand);
}
