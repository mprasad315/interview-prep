package main.java.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class HalfEvenRoundingStrategy implements RoundingStrategy {

    private final int scale;
    
    public HalfEvenRoundingStrategy(int scale) {
        this.scale = scale;
    }

    @Override
    public BigDecimal roundFunc(BigDecimal source) {
        return source.setScale(scale, RoundingMode.HALF_EVEN);
    }
}
