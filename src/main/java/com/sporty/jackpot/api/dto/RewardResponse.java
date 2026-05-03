package com.sporty.jackpot.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Result of jackpot reward evaluation")
public record RewardResponse(
        @Schema(example = "bet-001") String betId,
        @Schema(example = "user-123") String userId,
        @Schema(example = "jackpot-fixed-001") String jackpotId,
        @Schema(description = "True if the bet won the jackpot") boolean winner,
        @Schema(description = "Reward amount if winner, 0 otherwise", example = "1005.00") BigDecimal rewardAmount,
        @Schema(example = "Congratulations! You won the jackpot!") String message
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
