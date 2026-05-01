package com.sporty.jackpot.domain.strategy.contribution;

import java.math.BigDecimal;

public interface ContributionStrategy {

    /**
     * Calculates the contribution amount to add to the jackpot pool.
     *
     * @param betAmount   the amount of the incoming bet
     * @param currentPool the current jackpot pool amount
     * @return the contribution amount
     */
    BigDecimal calculate(BigDecimal betAmount, BigDecimal currentPool);
}
