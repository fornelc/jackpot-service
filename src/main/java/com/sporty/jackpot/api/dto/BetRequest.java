package com.sporty.jackpot.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Bet to be published to Kafka")
public record BetRequest(
        @Schema(description = "Unique bet identifier", example = "bet-001")
        @NotBlank(message = "betId is required")
        String betId,

        @Schema(description = "User placing the bet", example = "user-123")
        @NotBlank(message = "userId is required")
        String userId,

        @Schema(description = "Jackpot to contribute to", example = "jackpot-fixed-001")
        @NotBlank(message = "jackpotId is required")
        String jackpotId,

        @Schema(description = "Bet amount in GBP", example = "100.00")
        @NotNull(message = "betAmount is required")
        @DecimalMin(value = "0.01", message = "betAmount must be greater than 0")
        BigDecimal betAmount
) {}
