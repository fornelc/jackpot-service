package com.sporty.jackpot.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "jackpot_rewards")
@Getter
@Setter
@NoArgsConstructor
public class JackpotRewardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "bet_id", nullable = false)
    private String betId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "jackpot_id", nullable = false)
    private String jackpotId;

    @Column(name = "jackpot_reward_amount", nullable = false)
    private BigDecimal jackpotRewardAmount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
