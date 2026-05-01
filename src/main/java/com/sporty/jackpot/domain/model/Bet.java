package com.sporty.jackpot.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class Bet {

    private final String betId;
    private final String userId;
    private final String jackpotId;
    private final BigDecimal betAmount;
}
