package main.java.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class WholeRoundingStrategy implements RoundingStrategy {

    @Override
    public BigDecimal roundFunc(BigDecimal source) {
        return source.setScale(0, RoundingMode.HALF_UP);
    }
}
