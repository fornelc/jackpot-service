package com.sporty.jackpot.infrastructure.persistence.repository;

import com.sporty.jackpot.infrastructure.persistence.entity.JackpotRewardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JackpotRewardJpaRepository extends JpaRepository<JackpotRewardEntity, String> {
}
