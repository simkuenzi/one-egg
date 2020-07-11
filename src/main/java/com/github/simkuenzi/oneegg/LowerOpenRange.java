package com.github.simkuenzi.oneegg;

public class LowerOpenRange implements RangeReference {
    private final ScalarQuantity to;

    public LowerOpenRange(ScalarQuantity to) {
        this.to = to;
    }

    @Override
    public <T extends Quantity<T>> Dividend<T, RangeQuantity> multiply(T multiplicand) {
        return multiplicand.multiplyRange(this);
    }

    @Override
    public Dividend<ScalarQuantity, RangeQuantity> multiplyScalar(ScalarQuantity multiplicand) {
        return divisor -> divisor.divideFromUpper(multiplicand.multiply(to));
    }

    @Override
    public Dividend<RangeQuantity, RangeQuantity> multiplyRange(RangeQuantity multiplicand) {
        return divisor ->  multiplicand.multiply(to).divideByUpper(divisor);
    }
}
