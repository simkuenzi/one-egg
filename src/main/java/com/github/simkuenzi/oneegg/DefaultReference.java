package com.github.simkuenzi.oneegg;

public class DefaultReference {
    private final String productName;
    private final ReferenceType referenceType;

    public DefaultReference(String productName, ReferenceType referenceType) {
        this.productName = productName;
        this.referenceType = referenceType;
    }

    public String getProductName() {
        return productName;
    }

    public ReferenceType getReferenceType() {
        return referenceType;
    }
}
