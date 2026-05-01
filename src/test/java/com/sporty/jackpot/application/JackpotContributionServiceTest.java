package com.sporty.jackpot.application;

import com.sporty.jackpot.application.service.JackpotContributionService;
import com.sporty.jackpot.domain.model.Bet;
import com.sporty.jackpot.infrastructure.persistence.entity.JackpotEntity;
import com.sporty.jackpot.infrastructure.persistence.repository.JackpotContributionJpaRepository;
import com.sporty.jackpot.infrastructure.persistence.repository.JackpotJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JackpotContributionServiceTest {

    @Autowired
    private JackpotContributionService contributionService;

    @Autowired
    private JackpotJpaRepository jackpotRepository;

    @Autowired
    private JackpotContributionJpaRepository contributionRepository;

    private static final String JACKPOT_ID = "test-fixed-jackpot";

    @BeforeEach
    void setUp() {
        contributionRepository.deleteAll();
        jackpotRepository.deleteById(JACKPOT_ID);

        JackpotEntity jackpot = new JackpotEntity();
        jackpot.setJackpotId(JACKPOT_ID);
        jackpot.setPoolAmount(new BigDecimal("1000.00"));
        jackpot.setInitialPoolAmount(new BigDecimal("1000.00"));
        jackpot.setContributionType(JackpotEntity.ContributionType.FIXED);
        jackpot.setRewardType(JackpotEntity.RewardType.FIXED);
        jackpot.setContributionPercentage(new BigDecimal("5.0"));
        jackpot.setRewardChancePercentage(new BigDecimal("10.0"));
        jackpotRepository.save(jackpot);
    }

    @Test
    void processContribution_increasesJackpotPool() {
        Bet bet = new Bet("bet-001", "user-123", JACKPOT_ID, new BigDecimal("100.00"));

        contributionService.processContribution(bet);

        JackpotEntity updated = jackpotRepository.findById(JACKPOT_ID).orElseThrow();
        assertThat(updated.getPoolAmount()).isEqualByComparingTo("1005.00");
    }

    @Test
    void processContribution_savesContributionRecord() {
        Bet bet = new Bet("bet-002", "user-123", JACKPOT_ID, new BigDecimal("100.00"));

        contributionService.processContribution(bet);

        var contribution = contributionRepository.findByBetId("bet-002");
        assertThat(contribution).isPresent();
        assertThat(contribution.get().getContributionAmount()).isEqualByComparingTo("5.00");
        assertThat(contribution.get().getStakeAmount()).isEqualByComparingTo("100.00");
        assertThat(contribution.get().getCurrentJackpotAmount()).isEqualByComparingTo("1005.00");
    }

    @Test
    void processContribution_doesNothing_whenJackpotNotFound() {
        Bet bet = new Bet("bet-003", "user-123", "non-existent-jackpot", new BigDecimal("100.00"));

        contributionService.processContribution(bet);

        assertThat(contributionRepository.findByBetId("bet-003")).isEmpty();
    }

    @Test
    void processContribution_multipleBetstAccumulatePool() {
        contributionService.processContribution(new Bet("bet-004", "user-1", JACKPOT_ID, new BigDecimal("100.00")));
        contributionService.processContribution(new Bet("bet-005", "user-2", JACKPOT_ID, new BigDecimal("200.00")));

        JackpotEntity updated = jackpotRepository.findById(JACKPOT_ID).orElseThrow();
        // 1000 + 5 (5% of 100) + 10 (5% of 200) = 1015
        assertThat(updated.getPoolAmount()).isEqualByComparingTo("1015.00");
    }
}
