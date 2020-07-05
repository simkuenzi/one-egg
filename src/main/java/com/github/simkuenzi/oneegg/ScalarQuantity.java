package com.github.simkuenzi.oneegg;

import java.math.BigDecimal;

public interface ScalarQuantity extends Quantity<ScalarQuantity> {
    ScalarQuantity divide(ScalarQuantity divisor);

    BigDecimal decimal();
}
