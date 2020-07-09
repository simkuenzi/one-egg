package com.github.simkuenzi.oneegg;

public class SubscriptRepresentation implements DigitRepresentation {
    @Override
    public char asChar(int digit) {
        return (char) (0x2080 + digit);
    }
}
