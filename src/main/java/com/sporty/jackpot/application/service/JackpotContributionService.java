package com.sporty.jackpot.application.service;

import com.sporty.jackpot.domain.model.Bet;
import com.sporty.jackpot.domain.model.Jackpot;
import com.sporty.jackpot.infrastructure.persistence.entity.JackpotContributionEntity;
import com.sporty.jackpot.infrastructure.persistence.entity.JackpotEntity;
import com.sporty.jackpot.infrastructure.persistence.repository.JackpotContributionJpaRepository;
import com.sporty.jackpot.infrastructure.persistence.repository.JackpotJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class JackpotContributionService {

    private final JackpotJpaRepository jackpotRepository;
    private final JackpotContributionJpaRepository contributionRepository;
    private final JackpotMapper jackpotMapper;

    @Transactional
    public void processContribution(Bet bet) {
        Optional<JackpotEntity> jackpotEntityOpt = jackpotRepository.findById(bet.getJackpotId());

        if (jackpotEntityOpt.isEmpty()) {
            log.warn("No jackpot found for jackpotId={}. Bet {} ignored.", bet.getJackpotId(), bet.getBetId());
            return;
        }

        JackpotEntity jackpotEntity = jackpotEntityOpt.get();
        Jackpot jackpot = jackpotMapper.toDomain(jackpotEntity);

        BigDecimal contributionAmount = jackpot.contribute(bet.getBetAmount());

        // Persist updated pool
        jackpotEntity.setPoolAmount(jackpot.getPoolAmount());
        jackpotRepository.save(jackpotEntity);

        // Persist contribution record
        JackpotContributionEntity contribution = new JackpotContributionEntity();
        contribution.setBetId(bet.getBetId());
        contribution.setUserId(bet.getUserId());
        contribution.setJackpotId(bet.getJackpotId());
        contribution.setStakeAmount(bet.getBetAmount());
        contribution.setContributionAmount(contributionAmount);
        contribution.setCurrentJackpotAmount(jackpot.getPoolAmount());
        contributionRepository.save(contribution);

        log.info("Bet {} contributed {} to jackpot {}. New pool: {}",
                bet.getBetId(), contributionAmount, bet.getJackpotId(), jackpot.getPoolAmount());
    }
}
