package com.github.simkuenzi.oneegg;

public class UpperOpenRange implements RangeReference {
    private final ScalarQuantity from;

    public UpperOpenRange(ScalarQuantity from) {
        this.from = from;
    }

    @Override
    public <T extends Quantity<T>> Dividend<T, RangeQuantity> multiply(T multiplicand) {
        return multiplicand.multiplyRange(this);
    }

    @Override
    public Dividend<ScalarQuantity, RangeQuantity> multiplyScalar(ScalarQuantity multiplicand) {
        return divisor -> divisor.divideFromUpper(multiplicand.multiply(from));
    }

    @Override
    public Dividend<RangeQuantity, RangeQuantity> multiplyRange(RangeQuantity multiplicand) {
        return divisor -> multiplicand.multiply(from).divideByUpper(divisor);
    }
}
