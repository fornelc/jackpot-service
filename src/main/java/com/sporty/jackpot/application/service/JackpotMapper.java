package com.sporty.jackpot.application.service;

import com.sporty.jackpot.domain.model.Jackpot;
import com.sporty.jackpot.domain.strategy.contribution.FixedContributionStrategy;
import com.sporty.jackpot.domain.strategy.contribution.VariableContributionStrategy;
import com.sporty.jackpot.domain.strategy.reward.FixedRewardStrategy;
import com.sporty.jackpot.domain.strategy.reward.VariableRewardStrategy;
import com.sporty.jackpot.infrastructure.persistence.entity.JackpotEntity;
import org.springframework.stereotype.Component;

@Component
public class JackpotMapper {

    public Jackpot toDomain(JackpotEntity entity) {
        var contributionStrategy = switch (entity.getContributionType()) {
            case FIXED -> new FixedContributionStrategy(entity.getContributionPercentage());
            case VARIABLE -> new VariableContributionStrategy(
                    entity.getContributionInitialPercentage(),
                    entity.getContributionDecayRate()
            );
        };

        var rewardStrategy = switch (entity.getRewardType()) {
            case FIXED -> new FixedRewardStrategy(entity.getRewardChancePercentage());
            case VARIABLE -> new VariableRewardStrategy(entity.getRewardPoolLimit());
        };

        return new Jackpot(
                entity.getJackpotId(),
                entity.getInitialPoolAmount(),
                entity.getPoolAmount(),
                contributionStrategy,
                rewardStrategy
        );
    }
}
