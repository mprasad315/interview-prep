package main.java.impl;

import java.math.BigDecimal;
/**
 * RoundingStrategy
 */


public interface RoundingStrategy {
    BigDecimal roundFunc(BigDecimal source);
}
