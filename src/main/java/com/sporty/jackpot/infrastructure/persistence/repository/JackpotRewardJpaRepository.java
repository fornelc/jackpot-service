package com.sporty.jackpot.infrastructure.persistence.repository;

import com.sporty.jackpot.infrastructure.persistence.entity.JackpotRewardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JackpotRewardJpaRepository extends JpaRepository<JackpotRewardEntity, String> {
    Optional<JackpotRewardEntity> findByBetId(String betId);
}
