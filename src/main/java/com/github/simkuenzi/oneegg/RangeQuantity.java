package com.github.simkuenzi.oneegg;

public class RangeQuantity implements Quantity<RangeQuantity> {
    private final ScalarQuantity from;
    private final ScalarQuantity to;

    public RangeQuantity(ScalarQuantity from, ScalarQuantity to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public String asText() {
        return String.format("%s - %s", from.asText(), to.asText());
    }

    @Override
    public RangeQuantity multiply(ScalarQuantity multiplicand) {
        return new RangeQuantity(from.multiply(multiplicand), to.multiply(multiplicand));
    }

    @Override
    public ScalarQuantity divide(RangeQuantity divisor) {
        return from.divide(divisor.from);
    }

    @Override
    public RangeQuantity unit() {
        return new RangeQuantity(
                new FractionQuantity(1, 1),
                to.divide(from));
    }
}
