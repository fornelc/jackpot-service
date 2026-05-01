package com.sporty.jackpot.api.dto;

import java.math.BigDecimal;

public record RewardResponse(
        String betId,
        String userId,
        String jackpotId,
        boolean winner,
        BigDecimal rewardAmount,
        String message
) {
    public static RewardResponse win(String betId, String userId, String jackpotId, BigDecimal rewardAmount) {
        return new RewardResponse(betId, userId, jackpotId, true, rewardAmount,
                "Congratulations! You won the jackpot!");
    }

    public static RewardResponse noWin(String betId, String userId, String jackpotId) {
        return new RewardResponse(betId, userId, jackpotId, false, BigDecimal.ZERO,
                "Better luck next time.");
    }
}
