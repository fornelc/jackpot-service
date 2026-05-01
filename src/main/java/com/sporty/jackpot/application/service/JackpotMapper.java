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

        Jackpot jackpot = new Jackpot(
                entity.getJackpotId(),
                entity.getInitialPoolAmount(),
                contributionStrategy,
                rewardStrategy
        );

        // Restore current pool state
        if (entity.getPoolAmount().compareTo(entity.getInitialPoolAmount()) != 0) {
            // Sync pool from persistence — we do this by direct field reflection substitute:
            // pool is set via contribute() normally, but here we restore from DB
            restorePool(jackpot, entity);
        }

        return jackpot;
    }

    private void restorePool(Jackpot jackpot, JackpotEntity entity) {
        // Since poolAmount is set by contribute(), we restore it directly here
        // by using a package-level trick or a dedicated restore method
        jackpot.restorePool(entity.getPoolAmount());
    }
}
