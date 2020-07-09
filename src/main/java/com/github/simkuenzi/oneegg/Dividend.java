package com.github.simkuenzi.oneegg;

public interface Dividend<A extends Quantity<A>, B extends Quantity<B>> {
    A divide(B divisor);
}
