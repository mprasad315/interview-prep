package main.java.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NearestFiveCentRoundingStrategy implements RoundingStrategy {

    private static final BigDecimal FIVE_CENTS = BigDecimal.valueOf(0.05d);

    @Override
    public BigDecimal roundFunc(BigDecimal source) {
        return source.divide(FIVE_CENTS, 0, RoundingMode.HALF_UP).multiply(FIVE_CENTS); 
    }
}
