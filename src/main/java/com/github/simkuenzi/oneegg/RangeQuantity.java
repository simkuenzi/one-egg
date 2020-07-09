package com.github.simkuenzi.oneegg;

public class RangeQuantity implements Quantity<RangeQuantity> {
    private final ScalarQuantity from;
    private final ScalarQuantity to;

    public RangeQuantity(ScalarQuantity from, ScalarQuantity to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return asText();
    }

    @Override
    public String asText() {
        return String.format("%s - %s", from.asText(), to.asText());
    }

    @Override
    public Dividend<RangeQuantity, ScalarQuantity> multiplyScalar(ScalarQuantity multiplicand) {
        return divisor -> new RangeQuantity(
                from.multiplyScalar(multiplicand).divide(divisor),
                to.multiplyScalar(multiplicand).divide(divisor));
    }

    public RangeQuantity multiply(ScalarQuantity multiplicand) {
        return new RangeQuantity(
                from.multiply(multiplicand),
                to.multiply(multiplicand));
    }

    @Override
    public Dividend<RangeQuantity, RangeQuantity> multiplyRange(RangeReference multiplicand) {
        return multiplicand.multiplyRange(this);
    }

    public ScalarQuantity divideFromUpper(ScalarQuantity dividend) {
        return dividend.divide(to);
    }

    public RangeQuantity divideByUpper(RangeQuantity divisor) {
        return new RangeQuantity(from.divide(divisor.to), to.divide(divisor.to));
    }

    public ScalarQuantity divideFromLower(ScalarQuantity dividend) {
        return dividend.divide(from);
    }

    public RangeQuantity divideByLower(RangeQuantity divisor) {
        return new RangeQuantity(from.divide(divisor.from), to.divide(divisor.from));
    }
}
