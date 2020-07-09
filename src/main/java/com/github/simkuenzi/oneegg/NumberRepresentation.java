package com.github.simkuenzi.oneegg;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NumberRepresentation {
    private final DigitRepresentation digitRepresentation;

    public NumberRepresentation(DigitRepresentation digitRepresentation) {
        this.digitRepresentation = digitRepresentation;
    }

    public String asText(int value) {
       int remainder = value;
       List<Integer> digits = new ArrayList<>();
       while (remainder > 0) {
           digits.add(remainder % 10);
           remainder = remainder / 10;
       }

       return IntStream.range(0, digits.size())
               .map(i -> digits.size() - i - 1)
               .map(digits::get)
               .map(digitRepresentation::asChar)
               .mapToObj(Character::toString)
               .collect(Collectors.joining());
    }
}
