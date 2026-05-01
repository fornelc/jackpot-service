package com.sporty.jackpot.domain.strategy.reward;

import java.math.BigDecimal;

public interface RewardStrategy {

    /**
     * Evaluates whether the current bet wins the jackpot reward.
     *
     * @param currentPool the current jackpot pool amount
     * @return true if the bet wins the jackpot
     */
    boolean isWinner(BigDecimal currentPool);
}
