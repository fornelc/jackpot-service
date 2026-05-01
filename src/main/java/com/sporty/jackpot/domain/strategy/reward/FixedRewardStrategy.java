package com.sporty.jackpot.domain.strategy.reward;

import java.math.BigDecimal;
import java.util.random.RandomGenerator;

/**
 * Awards the jackpot with a fixed probability percentage,
 * regardless of the current pool size.
 *
 * Example: 10% chance means 1 in 10 bets wins on average.
 */
public class FixedRewardStrategy implements RewardStrategy {

    private final BigDecimal chancePercentage;
    private final RandomGenerator random;

    public FixedRewardStrategy(BigDecimal chancePercentage) {
        this(chancePercentage, RandomGenerator.getDefault());
    }

    // Constructor for testing with injectable random
    public FixedRewardStrategy(BigDecimal chancePercentage, RandomGenerator random) {
        this.chancePercentage = chancePercentage;
        this.random = random;
    }

    @Override
    public boolean isWinner(BigDecimal currentPool) {
        double roll = random.nextDouble(100.0);
        return roll < chancePercentage.doubleValue();
    }
}
