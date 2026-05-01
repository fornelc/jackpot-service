package com.sporty.jackpot.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "jackpot_contributions")
@Getter
@Setter
@NoArgsConstructor
public class JackpotContributionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "bet_id", nullable = false)
    private String betId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "jackpot_id", nullable = false)
    private String jackpotId;

    @Column(name = "stake_amount", nullable = false)
    private BigDecimal stakeAmount;

    @Column(name = "contribution_amount", nullable = false)
    private BigDecimal contributionAmount;

    @Column(name = "current_jackpot_amount", nullable = false)
    private BigDecimal currentJackpotAmount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
