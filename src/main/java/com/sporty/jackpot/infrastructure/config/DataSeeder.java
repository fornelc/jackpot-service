package com.sporty.jackpot.infrastructure.config;

import com.sporty.jackpot.infrastructure.persistence.entity.JackpotEntity;
import com.sporty.jackpot.infrastructure.persistence.repository.JackpotJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final JackpotJpaRepository jackpotRepository;

    @Override
    public void run(String... args) {
        seedFixedJackpot();
        seedVariableJackpot();
        log.info("Seeded {} jackpots", jackpotRepository.count());
    }

    private void seedFixedJackpot() {
        if (jackpotRepository.existsById("jackpot-fixed-001")) return;

        JackpotEntity jackpot = new JackpotEntity();
        jackpot.setJackpotId("jackpot-fixed-001");
        jackpot.setPoolAmount(new BigDecimal("1000.00"));
        jackpot.setInitialPoolAmount(new BigDecimal("1000.00"));
        jackpot.setContributionType(JackpotEntity.ContributionType.FIXED);
        jackpot.setRewardType(JackpotEntity.RewardType.FIXED);
        jackpot.setContributionPercentage(new BigDecimal("5.0"));
        jackpot.setRewardChancePercentage(new BigDecimal("10.0"));
        jackpotRepository.save(jackpot);

        log.info("Seeded fixed jackpot: jackpot-fixed-001 (5% contribution, 10% win chance)");
    }

    private void seedVariableJackpot() {
        if (jackpotRepository.existsById("jackpot-variable-001")) return;

        JackpotEntity jackpot = new JackpotEntity();
        jackpot.setJackpotId("jackpot-variable-001");
        jackpot.setPoolAmount(new BigDecimal("5000.00"));
        jackpot.setInitialPoolAmount(new BigDecimal("5000.00"));
        jackpot.setContributionType(JackpotEntity.ContributionType.VARIABLE);
        jackpot.setRewardType(JackpotEntity.RewardType.VARIABLE);
        jackpot.setContributionInitialPercentage(new BigDecimal("10.0"));
        jackpot.setContributionDecayRate(new BigDecimal("1.0"));
        jackpot.setRewardPoolLimit(new BigDecimal("50000.00"));
        jackpotRepository.save(jackpot);

        log.info("Seeded variable jackpot: jackpot-variable-001 (10% start contribution, 100% win at £50k pool)");
    }
}
