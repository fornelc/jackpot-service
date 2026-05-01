package com.sporty.jackpot.domain.strategy.reward;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.random.RandomGenerator;

/**
 * Awards the jackpot with a variable probability that grows as the pool increases.
 * Once the pool reaches the configured limit, the chance becomes 100% (guaranteed win).
 *
 * Formula: effectiveChance = min((currentPool / poolLimit) * 100, 100)
 *
 * Example: poolLimit = £50,000
 * At £10,000 pool → 20% chance
 * At £25,000 pool → 50% chance
 * At £50,000 pool → 100% chance (guaranteed)
 */
public class VariableRewardStrategy implements RewardStrategy {

    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    private final BigDecimal poolLimit;
    private final RandomGenerator random;

    public VariableRewardStrategy(BigDecimal poolLimit) {
        this(poolLimit, RandomGenerator.getDefault());
    }

    // Constructor for testing with injectable random
    public VariableRewardStrategy(BigDecimal poolLimit, RandomGenerator random) {
        this.poolLimit = poolLimit;
        this.random = random;
    }

    @Override
    public boolean isWinner(BigDecimal currentPool) {
        BigDecimal effectiveChance = currentPool
                .divide(poolLimit, 4, RoundingMode.HALF_UP)
                .multiply(ONE_HUNDRED)
                .min(ONE_HUNDRED);

        if (effectiveChance.compareTo(ONE_HUNDRED) >= 0) {
            return true;
        }

        double roll = random.nextDouble(100.0);
        return roll < effectiveChance.doubleValue();
    }
}
