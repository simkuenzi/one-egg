package com.github.simkuenzi.oneegg;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class FractionQuantity implements ScalarQuantity {
    private final int counter;
    private final int denominator;

    public FractionQuantity(int counter, int denominator) {
        this.counter = counter;
        this.denominator = denominator;
    }

    public FractionQuantity(BigDecimal counter, BigDecimal denominator) {
        this(shift(counter, counter, denominator), shift(denominator, counter, denominator));
    }

    private static int shift(BigDecimal x, BigDecimal counter, BigDecimal denominator) {
        return x.multiply(BigDecimal.valueOf(10).pow(Math.max(counter.scale(), denominator.scale()))).intValueExact();
    }

    @Override
    public String asText() {
        int intPart = counter / denominator;
        int remainingCounter = counter % denominator;

        if (remainingCounter == 0) {
            return String.format("%d", intPart);
        } else if (intPart == 0) {
            return String.format("%d/%d", remainingCounter, denominator);
        } else {
            return String.format("%d %d/%d", intPart, remainingCounter, denominator);
        }
    }

    @Override
    public ScalarQuantity multiply(ScalarQuantity multiplicand) {
        if (multiplicand instanceof FractionQuantity) {
            FractionQuantity fractionMultiplicand = (FractionQuantity) multiplicand;
            return new FractionQuantity(
                    this.counter * fractionMultiplicand.counter,
                    this.denominator * fractionMultiplicand.denominator).reduce();
        } else {
            return new DecimalQuantity(decimal().multiply(multiplicand.decimal()));
        }
    }

    @Override
    public ScalarQuantity divide(ScalarQuantity divisor) {
        if (divisor instanceof FractionQuantity) {
            FractionQuantity fractionDivisor = (FractionQuantity) divisor;
            MathContext mathContext = new MathContext(3, RoundingMode.HALF_UP);
            return new FractionQuantity(
                    BigDecimal.valueOf(this.counter).divide(BigDecimal.valueOf(fractionDivisor.counter), mathContext),
                    BigDecimal.valueOf(this.denominator).divide(BigDecimal.valueOf(fractionDivisor.denominator), mathContext)).reduce();
        } else {
            return new DecimalQuantity(decimal()).divide(new DecimalQuantity(divisor.decimal()));
        }
    }

    private ScalarQuantity reduce() {
        for (int i = 1; i <= 20; i++) {
            if (i * counter % denominator == 0) {
                return new FractionQuantity(i * counter / denominator, i);
            }
        }
        return new DecimalQuantity(decimal());
    }

    @Override
    public BigDecimal decimal() {
        return BigDecimal.valueOf(counter).divide(BigDecimal.valueOf(denominator), new MathContext(3, RoundingMode.HALF_UP));
    }

    @Override
    public ScalarQuantity unit() {
        return new FractionQuantity(1, 1);
    }
}
