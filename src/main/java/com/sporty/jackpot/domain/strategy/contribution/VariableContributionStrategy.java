package com.sporty.jackpot.domain.strategy.contribution;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Contributes a variable percentage of the bet amount.
 * The contribution starts high and decreases as the jackpot pool grows,
 * at a fixed decay rate per unit of pool increase.
 *
 * Formula: effectiveRate = max(initialPercentage - (currentPool / 1000 * decayRate), minimumRate)
 *
 * Example: starts at 10%, decays 1% per £1000 pool increase.
 * At £0 pool   → 10% contribution
 * At £5000 pool → 5% contribution
 * At £9000 pool → 1% contribution (minimum)
 */
public class VariableContributionStrategy implements ContributionStrategy {

    private static final BigDecimal MINIMUM_RATE = BigDecimal.ONE;
    private static final BigDecimal POOL_UNIT = BigDecimal.valueOf(1000);

    private final BigDecimal initialPercentage;
    private final BigDecimal decayRate;

    public VariableContributionStrategy(BigDecimal initialPercentage, BigDecimal decayRate) {
        this.initialPercentage = initialPercentage;
        this.decayRate = decayRate;
    }

    @Override
    public BigDecimal calculate(BigDecimal betAmount, BigDecimal currentPool) {
        BigDecimal poolUnits = currentPool.divide(POOL_UNIT, 4, RoundingMode.HALF_UP);
        BigDecimal decay = poolUnits.multiply(decayRate);
        BigDecimal effectiveRate = initialPercentage.subtract(decay).max(MINIMUM_RATE);

        return betAmount
                .multiply(effectiveRate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }
}
