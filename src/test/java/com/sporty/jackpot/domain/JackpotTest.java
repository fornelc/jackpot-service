package com.sporty.jackpot.domain;

import com.sporty.jackpot.domain.model.Jackpot;
import com.sporty.jackpot.domain.strategy.contribution.FixedContributionStrategy;
import com.sporty.jackpot.domain.strategy.reward.FixedRewardStrategy;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.random.RandomGenerator;

import static org.assertj.core.api.Assertions.assertThat;

class JackpotTest {

    @Test
    void contribute_increasesPoolByContributionAmount() {
        Jackpot jackpot = buildJackpot("1000.00", alwaysLose());

        BigDecimal contribution = jackpot.contribute(new BigDecimal("100.00"));

        assertThat(contribution).isEqualByComparingTo("5.00");
        assertThat(jackpot.getPoolAmount()).isEqualByComparingTo("1005.00");
    }

    @Test
    void reset_restoresPoolToInitialValue() {
        Jackpot jackpot = buildJackpot("1000.00", alwaysLose());
        jackpot.contribute(new BigDecimal("200.00"));
        assertThat(jackpot.getPoolAmount()).isGreaterThan(new BigDecimal("1000.00"));

        jackpot.reset();

        assertThat(jackpot.getPoolAmount()).isEqualByComparingTo("1000.00");
    }

    @Test
    void evaluateReward_returnsTrue_whenStrategyWins() {
        Jackpot jackpot = buildJackpot("1000.00", alwaysWin());

        assertThat(jackpot.evaluateReward()).isTrue();
    }

    @Test
    void evaluateReward_returnsFalse_whenStrategyLoses() {
        Jackpot jackpot = buildJackpot("1000.00", alwaysLose());

        assertThat(jackpot.evaluateReward()).isFalse();
    }

    private Jackpot buildJackpot(String initialPool, RandomGenerator random) {
        return new Jackpot(
                "test-jackpot",
                new BigDecimal(initialPool),
                new FixedContributionStrategy(new BigDecimal("5.0")),
                new FixedRewardStrategy(new BigDecimal("10.0"), random)
        );
    }

    private RandomGenerator alwaysWin() {
        return new RandomGenerator() {
            @Override public long nextLong() { return 0; }
            @Override public double nextDouble(double bound) { return 0.0; } // 0.0 < 10% → win
        };
    }

    private RandomGenerator alwaysLose() {
        return new RandomGenerator() {
            @Override public long nextLong() { return 0; }
            @Override public double nextDouble(double bound) { return 99.0; } // 99 > 10% → lose
        };
    }
}
