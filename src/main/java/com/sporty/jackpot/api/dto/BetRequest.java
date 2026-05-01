package com.sporty.jackpot.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record BetRequest(
        @NotBlank(message = "betId is required")
        String betId,

        @NotBlank(message = "userId is required")
        String userId,

        @NotBlank(message = "jackpotId is required")
        String jackpotId,

        @NotNull(message = "betAmount is required")
        @DecimalMin(value = "0.01", message = "betAmount must be greater than 0")
        BigDecimal betAmount
) {}
