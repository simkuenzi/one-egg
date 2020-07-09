package com.github.simkuenzi.oneegg;

import java.math.BigDecimal;

public class ScalarQuantity implements Quantity<ScalarQuantity> {
    private final int counter;
    private final int denominator;

    public ScalarQuantity(int counter, int denominator) {
        this.counter = counter;
        this.denominator = denominator;
    }

    public ScalarQuantity(BigDecimal value) {
        this(value, BigDecimal.ONE);
    }

    public ScalarQuantity(BigDecimal counter, BigDecimal denominator) {
        this(shift(counter, counter, denominator), shift(denominator, counter, denominator));
    }

    private static int shift(BigDecimal x, BigDecimal counter, BigDecimal denominator) {
        return x.multiply(BigDecimal.valueOf(10).pow(Math.max(counter.scale(), denominator.scale()))).intValueExact();
    }

    @Override
    public String toString() {
        return asText();
    }

    @Override
    public String asText() {
        ScalarQuantity reduced = reduce();
        int intPart = reduced.counter / reduced.denominator;
        int remainingCounter = reduced.counter % reduced.denominator;

        if (remainingCounter == 0) {
            return String.format("%d", intPart);
        } else if (intPart == 0) {
            return String.format("%d/%d", remainingCounter, reduced.denominator);
        } else {
            return String.format("%d %d/%d", intPart, remainingCounter, reduced.denominator);
        }
    }

    public ScalarQuantity divide(ScalarQuantity divisor) {
        return new ScalarQuantity(counter * divisor.denominator, denominator * divisor.counter);
    }

    public ScalarQuantity multiply(ScalarQuantity multiplicand) {
        return new ScalarQuantity(counter * multiplicand.counter, denominator * multiplicand.denominator);
    }

    @Override
    public Dividend<ScalarQuantity, ScalarQuantity> multiplyScalar(ScalarQuantity multiplicand) {
        return divisor -> new ScalarQuantity(
                counter * multiplicand.counter,
                denominator * multiplicand.denominator)
                .divide(divisor);
    }

    @Override
    public Dividend<ScalarQuantity, RangeQuantity> multiplyRange(RangeReference multiplicand) {
        return multiplicand.multiplyScalar(this);
    }


    private ScalarQuantity reduce() {
        for (int i = 1; i <= 20; i++) {
            if (i * counter % denominator == 0) {
                return new ScalarQuantity(i * counter / denominator, i);
            }
        }
        return this;
    }

    public <T extends Quantity<T>> Dividend<T, ScalarQuantity> multiply(T multiplicand) {
        return multiplicand.multiplyScalar(this);
    }
}
