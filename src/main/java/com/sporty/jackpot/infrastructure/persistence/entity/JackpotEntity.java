package com.sporty.jackpot.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "jackpots")
@Getter
@Setter
@NoArgsConstructor
public class JackpotEntity {

    @Id
    @Column(name = "jackpot_id")
    private String jackpotId;

    @Column(name = "pool_amount", nullable = false)
    private BigDecimal poolAmount;

    @Column(name = "initial_pool_amount", nullable = false)
    private BigDecimal initialPoolAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "contribution_type", nullable = false)
    private ContributionType contributionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "reward_type", nullable = false)
    private RewardType rewardType;

    // Contribution config
    @Column(name = "contribution_percentage")
    private BigDecimal contributionPercentage;

    @Column(name = "contribution_initial_percentage")
    private BigDecimal contributionInitialPercentage;

    @Column(name = "contribution_decay_rate")
    private BigDecimal contributionDecayRate;

    // Reward config
    @Column(name = "reward_chance_percentage")
    private BigDecimal rewardChancePercentage;

    @Column(name = "reward_pool_limit")
    private BigDecimal rewardPoolLimit;

    public enum ContributionType {
        FIXED, VARIABLE
    }

    public enum RewardType {
        FIXED, VARIABLE
    }
}
