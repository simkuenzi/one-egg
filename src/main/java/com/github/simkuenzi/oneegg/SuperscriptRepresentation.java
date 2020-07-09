package com.github.simkuenzi.oneegg;

public class SuperscriptRepresentation implements DigitRepresentation {
    @Override
    public char asChar(int digit) {
        return switch (digit) {
            case 1 -> (char) 0x00b9;
            case 2, 3 -> (char) (0x00b0 + digit);
            default -> (char) (0x2070 + digit);
        };
    }
}
