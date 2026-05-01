package com.sporty.jackpot.application;

import com.sporty.jackpot.api.dto.RewardResponse;
import com.sporty.jackpot.application.service.JackpotRewardService;
import com.sporty.jackpot.infrastructure.persistence.entity.JackpotContributionEntity;
import com.sporty.jackpot.infrastructure.persistence.entity.JackpotEntity;
import com.sporty.jackpot.infrastructure.persistence.repository.JackpotContributionJpaRepository;
import com.sporty.jackpot.infrastructure.persistence.repository.JackpotJpaRepository;
import com.sporty.jackpot.infrastructure.persistence.repository.JackpotRewardJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class JackpotRewardServiceTest {

    @Autowired
    private JackpotRewardService rewardService;

    @Autowired
    private JackpotJpaRepository jackpotRepository;

    @Autowired
    private JackpotContributionJpaRepository contributionRepository;

    @Autowired
    private JackpotRewardJpaRepository rewardRepository;

    private static final String JACKPOT_ID = "test-reward-jackpot";
    private static final String BET_ID = "bet-reward-001";

    @BeforeEach
    void setUp() {
        rewardRepository.deleteAll();
        contributionRepository.deleteAll();
        jackpotRepository.deleteById(JACKPOT_ID);

        // Jackpot with 100% fixed win chance to make tests deterministic
        JackpotEntity jackpot = new JackpotEntity();
        jackpot.setJackpotId(JACKPOT_ID);
        jackpot.setPoolAmount(new BigDecimal("2000.00"));
        jackpot.setInitialPoolAmount(new BigDecimal("1000.00"));
        jackpot.setContributionType(JackpotEntity.ContributionType.FIXED);
        jackpot.setRewardType(JackpotEntity.RewardType.FIXED);
        jackpot.setContributionPercentage(new BigDecimal("5.0"));
        jackpot.setRewardChancePercentage(new BigDecimal("100.0")); // always wins
        jackpotRepository.save(jackpot);

        // Seed a contribution for the test bet
        JackpotContributionEntity contribution = new JackpotContributionEntity();
        contribution.setBetId(BET_ID);
        contribution.setUserId("user-123");
        contribution.setJackpotId(JACKPOT_ID);
        contribution.setStakeAmount(new BigDecimal("100.00"));
        contribution.setContributionAmount(new BigDecimal("5.00"));
        contribution.setCurrentJackpotAmount(new BigDecimal("2000.00"));
        contributionRepository.save(contribution);
    }

    @Test
    void evaluateReward_returnsWin_whenChanceIs100Percent() {
        RewardResponse response = rewardService.evaluateReward(BET_ID);

        assertThat(response.winner()).isTrue();
        assertThat(response.rewardAmount()).isEqualByComparingTo("2000.00");
        assertThat(response.betId()).isEqualTo(BET_ID);
    }

    @Test
    void evaluateReward_resetsJackpotPool_afterWin() {
        rewardService.evaluateReward(BET_ID);

        JackpotEntity jackpot = jackpotRepository.findById(JACKPOT_ID).orElseThrow();
        assertThat(jackpot.getPoolAmount()).isEqualByComparingTo("1000.00"); // reset to initial
    }

    @Test
    void evaluateReward_savesRewardRecord_afterWin() {
        rewardService.evaluateReward(BET_ID);

        assertThat(rewardRepository.count()).isEqualTo(1);
        var reward = rewardRepository.findAll().get(0);
        assertThat(reward.getBetId()).isEqualTo(BET_ID);
        assertThat(reward.getJackpotRewardAmount()).isEqualByComparingTo("2000.00");
    }

    @Test
    void evaluateReward_throwsException_whenBetNotFound() {
        assertThatThrownBy(() -> rewardService.evaluateReward("non-existent-bet"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("non-existent-bet");
    }
}
