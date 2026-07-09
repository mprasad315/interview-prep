package main.java.service;

import java.math.BigDecimal;
import main.java.domain.Currency;
import java.util.Map;

import main.java.impl.RoundingStrategy;

public class RoundingService {
    private final Map<Currency, RoundingStrategy> currencyMap;

    public RoundingService(Map<Currency, RoundingStrategy> currencyMap) {
        this.currencyMap = currencyMap;
    }

    public BigDecimal round(Currency domain, BigDecimal source) {
        RoundingStrategy roundingStrategy = this.currencyMap.get(domain);
        if (roundingStrategy == null) {
            throw new IllegalArgumentException("Unsupported currency type: " + domain);
        }
        return roundingStrategy.roundFunc(source);
    }
}
