package com.github.simkuenzi.oneegg;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class DecimalQuantity implements ScalarQuantity {
    private final BigDecimal value;

    public DecimalQuantity(BigDecimal value) {
        this.value = value;
    }

    @Override
    public String asText() {
        return value.toPlainString();
    }

    @Override
    public BigDecimal decimal() {
        return value;
    }

    @Override
    public ScalarQuantity multiply(ScalarQuantity multiplicand) {
        if (value.scale() == 0) {
            return new FractionQuantity(value.intValueExact(), 1).multiply(multiplicand);
        } else {
            return new DecimalQuantity(value.multiply(multiplicand.decimal()));
        }
    }

    @Override
    public ScalarQuantity divide(ScalarQuantity divisor) {
        if (value.scale() == 0) {
            return new FractionQuantity(value.intValueExact(), 1).divide(divisor);
        } else {
            return new DecimalQuantity(value.divide(divisor.decimal(), new MathContext(3, RoundingMode.HALF_UP)));
        }
    }

    @Override
    public ScalarQuantity unit() {
        return new FractionQuantity(1, 1);
    }
}
