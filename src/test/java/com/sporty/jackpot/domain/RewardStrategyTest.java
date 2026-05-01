package com.sporty.jackpot.domain;

import com.sporty.jackpot.domain.strategy.reward.FixedRewardStrategy;
import com.sporty.jackpot.domain.strategy.reward.VariableRewardStrategy;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.random.RandomGenerator;

import static org.assertj.core.api.Assertions.assertThat;

class RewardStrategyTest {

    @Test
    void fixedStrategy_winsWhenRollIsBelowChance() {
        // Roll of 5.0 < 10% chance → win
        RandomGenerator alwaysLow = new FixedRandom(5.0);
        var strategy = new FixedRewardStrategy(new BigDecimal("10.0"), alwaysLow);

        assertThat(strategy.isWinner(new BigDecimal("1000.00"))).isTrue();
    }

    @Test
    void fixedStrategy_losesWhenRollIsAboveChance() {
        // Roll of 50.0 > 10% chance → lose
        RandomGenerator alwaysHigh = new FixedRandom(50.0);
        var strategy = new FixedRewardStrategy(new BigDecimal("10.0"), alwaysHigh);

        assertThat(strategy.isWinner(new BigDecimal("1000.00"))).isFalse();
    }

    @Test
    void fixedStrategy_poolSizeDoesNotAffectChance() {
        RandomGenerator alwaysLow = new FixedRandom(5.0);
        var strategy = new FixedRewardStrategy(new BigDecimal("10.0"), alwaysLow);

        assertThat(strategy.isWinner(BigDecimal.ZERO)).isTrue();
        assertThat(strategy.isWinner(new BigDecimal("100000.00"))).isTrue();
    }

    @Test
    void variableStrategy_alwaysWinsWhenPoolHitsLimit() {
        RandomGenerator alwaysHigh = new FixedRandom(99.9); // would lose with any fixed strategy
        var strategy = new VariableRewardStrategy(new BigDecimal("50000.00"), alwaysHigh);

        // At the pool limit → 100% chance → always wins
        assertThat(strategy.isWinner(new BigDecimal("50000.00"))).isTrue();
    }

    @Test
    void variableStrategy_chanceGrowsWithPool() {
        // At 50% of pool limit → 50% chance
        // Roll of 49.9 < 50 → win
        RandomGenerator almostHalf = new FixedRandom(49.9);
        var strategy = new VariableRewardStrategy(new BigDecimal("50000.00"), almostHalf);

        assertThat(strategy.isWinner(new BigDecimal("25000.00"))).isTrue();
    }

    @Test
    void variableStrategy_lowPoolMeansLowChance() {
        // At 1% of pool limit → 1% chance
        // Roll of 5.0 > 1% → lose
        RandomGenerator roll5 = new FixedRandom(5.0);
        var strategy = new VariableRewardStrategy(new BigDecimal("50000.00"), roll5);

        assertThat(strategy.isWinner(new BigDecimal("500.00"))).isFalse();
    }

    /** Test helper: a RandomGenerator that always returns the same double value. */
    private record FixedRandom(double value) implements RandomGenerator {
        @Override
        public long nextLong() { return 0; }

        @Override
        public double nextDouble(double bound) { return value; }
    }
}
