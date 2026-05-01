package com.sporty.jackpot.domain.strategy.contribution;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Contributes a fixed percentage of the bet amount to the jackpot pool,
 * regardless of the current pool size.
 *
 * Example: 5% contribution on a £100 bet always contributes £5.
 */
public class FixedContributionStrategy implements ContributionStrategy {

    private final BigDecimal percentage;

    public FixedContributionStrategy(BigDecimal percentage) {
        this.percentage = percentage;
    }

    @Override
    public BigDecimal calculate(BigDecimal betAmount, BigDecimal currentPool) {
        return betAmount
                .multiply(percentage)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }
}
