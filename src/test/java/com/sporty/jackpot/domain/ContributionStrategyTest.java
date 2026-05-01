package com.sporty.jackpot.domain;

import com.sporty.jackpot.domain.strategy.contribution.FixedContributionStrategy;
import com.sporty.jackpot.domain.strategy.contribution.VariableContributionStrategy;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ContributionStrategyTest {

    @Test
    void fixedStrategy_contributesFixedPercentageOfBetAmount() {
        var strategy = new FixedContributionStrategy(new BigDecimal("5.0"));

        BigDecimal contribution = strategy.calculate(new BigDecimal("100.00"), new BigDecimal("1000.00"));

        assertThat(contribution).isEqualByComparingTo("5.00");
    }

    @Test
    void fixedStrategy_poolSizeDoesNotAffectContribution() {
        var strategy = new FixedContributionStrategy(new BigDecimal("5.0"));

        BigDecimal contributionSmallPool = strategy.calculate(new BigDecimal("100.00"), new BigDecimal("100.00"));
        BigDecimal contributionLargePool = strategy.calculate(new BigDecimal("100.00"), new BigDecimal("100000.00"));

        assertThat(contributionSmallPool).isEqualByComparingTo(contributionLargePool);
    }

    @Test
    void variableStrategy_contributionDecaysAsPoolGrows() {
        var strategy = new VariableContributionStrategy(new BigDecimal("10.0"), new BigDecimal("1.0"));

        BigDecimal atZeroPool    = strategy.calculate(new BigDecimal("100.00"), BigDecimal.ZERO);
        BigDecimal atFiveKPool   = strategy.calculate(new BigDecimal("100.00"), new BigDecimal("5000.00"));
        BigDecimal atNineKPool   = strategy.calculate(new BigDecimal("100.00"), new BigDecimal("9000.00"));

        assertThat(atZeroPool).isEqualByComparingTo("10.00");   // 10% at £0 pool
        assertThat(atFiveKPool).isEqualByComparingTo("5.00");   // 5% at £5k pool
        assertThat(atNineKPool).isEqualByComparingTo("1.00");   // minimum 1% at £9k pool
    }

    @Test
    void variableStrategy_neverGoesBelowMinimumRate() {
        var strategy = new VariableContributionStrategy(new BigDecimal("10.0"), new BigDecimal("1.0"));

        // Even at £100k pool, contribution should not go below 1%
        BigDecimal contribution = strategy.calculate(new BigDecimal("100.00"), new BigDecimal("100000.00"));

        assertThat(contribution).isEqualByComparingTo("1.00");
    }
}
