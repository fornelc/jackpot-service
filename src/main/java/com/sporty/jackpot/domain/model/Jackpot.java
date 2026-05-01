package com.sporty.jackpot.domain.model;

import com.sporty.jackpot.domain.strategy.contribution.ContributionStrategy;
import com.sporty.jackpot.domain.strategy.reward.RewardStrategy;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class Jackpot {

    private final String jackpotId;
    private BigDecimal poolAmount;
    private final BigDecimal initialPoolAmount;
    private final ContributionStrategy contributionStrategy;
    private final RewardStrategy rewardStrategy;

    public Jackpot(String jackpotId,
                   BigDecimal initialPoolAmount,
                   ContributionStrategy contributionStrategy,
                   RewardStrategy rewardStrategy) {
        this.jackpotId = jackpotId;
        this.initialPoolAmount = initialPoolAmount;
        this.poolAmount = initialPoolAmount;
        this.contributionStrategy = contributionStrategy;
        this.rewardStrategy = rewardStrategy;
    }

    public BigDecimal contribute(BigDecimal betAmount) {
        BigDecimal contribution = contributionStrategy.calculate(betAmount, poolAmount);
        poolAmount = poolAmount.add(contribution);
        return contribution;
    }

    public boolean evaluateReward() {
        return rewardStrategy.isWinner(poolAmount);
    }

    public void reset() {
        this.poolAmount = initialPoolAmount;
    }

    /**
     * Restores the pool amount from persistence.
     * Used only when rehydrating a Jackpot from the database.
     */
    public void restorePool(java.math.BigDecimal persistedPool) {
        this.poolAmount = persistedPool;
    }
}
