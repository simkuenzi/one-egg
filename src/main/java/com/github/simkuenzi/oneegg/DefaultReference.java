package com.github.simkuenzi.oneegg;

public class DefaultReference {
    private final String productName;
    private final ReferenceType scalar;

    public DefaultReference(String productName, ReferenceType scalar) {
        this.productName = productName;
        this.scalar = scalar;
    }

    public String getProductName() {
        return productName;
    }

    public ReferenceType getScalar() {
        return scalar;
    }
}
