package com.sporty.jackpot.application.service;

import com.sporty.jackpot.api.dto.RewardResponse;
import com.sporty.jackpot.domain.model.Jackpot;
import com.sporty.jackpot.infrastructure.persistence.entity.JackpotContributionEntity;
import com.sporty.jackpot.infrastructure.persistence.entity.JackpotEntity;
import com.sporty.jackpot.infrastructure.persistence.entity.JackpotRewardEntity;
import com.sporty.jackpot.infrastructure.persistence.repository.JackpotContributionJpaRepository;
import com.sporty.jackpot.infrastructure.persistence.repository.JackpotJpaRepository;
import com.sporty.jackpot.infrastructure.persistence.repository.JackpotRewardJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class JackpotRewardService {

    private final JackpotJpaRepository jackpotRepository;
    private final JackpotContributionJpaRepository contributionRepository;
    private final JackpotRewardJpaRepository rewardRepository;
    private final JackpotMapper jackpotMapper;

    @Transactional
    public RewardResponse evaluateReward(String betId) {
        Optional<RewardResponse> existingReward = findExistingReward(betId);
        if (existingReward.isPresent()) {
            return existingReward.get();
        }

        JackpotContributionEntity contribution = contributionRepository.findByBetId(betId)
                .orElseThrow(() -> new IllegalArgumentException("No contribution found for betId: " + betId));

        JackpotEntity jackpotEntity = jackpotRepository.findById(contribution.getJackpotId())
                .orElseThrow(() -> new IllegalStateException("Jackpot not found: " + contribution.getJackpotId()));

        Jackpot jackpot = jackpotMapper.toDomain(jackpotEntity);
        boolean isWinner = jackpot.evaluateReward();

        if (isWinner) {
            return handleWin(contribution, jackpotEntity, jackpot);
        }

        log.info("Bet {} did not win jackpot {}", betId, jackpotEntity.getJackpotId());
        return RewardResponse.noWin(betId, contribution.getUserId(), jackpotEntity.getJackpotId());
    }

    private RewardResponse handleWin(JackpotContributionEntity contribution,
                                     JackpotEntity jackpotEntity,
                                     Jackpot jackpot) {
        var rewardAmount = jackpot.getPoolAmount();

        // Save reward record
        JackpotRewardEntity reward = new JackpotRewardEntity();
        reward.setBetId(contribution.getBetId());
        reward.setUserId(contribution.getUserId());
        reward.setJackpotId(jackpotEntity.getJackpotId());
        reward.setJackpotRewardAmount(rewardAmount);
        rewardRepository.save(reward);

        // Reset jackpot pool
        jackpot.reset();
        jackpotEntity.setPoolAmount(jackpot.getPoolAmount());
        jackpotRepository.save(jackpotEntity);

        log.info("Bet {} WON jackpot {}! Reward: {}. Pool reset to {}",
                contribution.getBetId(), jackpotEntity.getJackpotId(),
                rewardAmount, jackpot.getPoolAmount());

        return RewardResponse.win(
                contribution.getBetId(),
                contribution.getUserId(),
                jackpotEntity.getJackpotId(),
                rewardAmount
        );
    }

    private Optional<RewardResponse> findExistingReward(String betId) {
        return rewardRepository.findByBetId(betId)
                .map(reward -> {
                    log.info("Bet {} was already evaluated and won. Returning existing reward.", betId);
                    return RewardResponse.win(
                            reward.getBetId(),
                            reward.getUserId(),
                            reward.getJackpotId(),
                            reward.getJackpotRewardAmount()
                    );
                });
    }
}
