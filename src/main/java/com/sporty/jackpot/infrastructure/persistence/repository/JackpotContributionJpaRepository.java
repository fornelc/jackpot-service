package com.sporty.jackpot.infrastructure.persistence.repository;

import com.sporty.jackpot.infrastructure.persistence.entity.JackpotContributionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JackpotContributionJpaRepository extends JpaRepository<JackpotContributionEntity, String> {
    Optional<JackpotContributionEntity> findByBetId(String betId);
}
