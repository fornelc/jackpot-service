package com.sporty.jackpot.infrastructure.persistence.repository;

import com.sporty.jackpot.infrastructure.persistence.entity.JackpotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JackpotJpaRepository extends JpaRepository<JackpotEntity, String> {
}
