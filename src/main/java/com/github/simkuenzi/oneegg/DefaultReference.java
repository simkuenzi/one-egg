package com.github.simkuenzi.oneegg;

public class DefaultReference {
    private final String productName;
    private final QuantityType quantityType;

    public DefaultReference(String productName, QuantityType quantityType) {
        this.productName = productName;
        this.quantityType = quantityType;
    }

    public String getProductName() {
        return productName;
    }

    public QuantityType getQuantityType() {
        return quantityType;
    }
}
