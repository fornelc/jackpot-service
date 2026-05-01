package com.sporty.jackpot.infrastructure.kafka;

import com.sporty.jackpot.api.dto.BetRequest;
import com.sporty.jackpot.application.service.JackpotContributionService;
import com.sporty.jackpot.domain.model.Bet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BetKafkaConsumer {

    private final JackpotContributionService contributionService;

    @KafkaListener(topics = BetKafkaProducer.TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void consume(BetRequest betRequest) {
        log.info("Received bet from Kafka: betId={}, jackpotId={}", betRequest.betId(), betRequest.jackpotId());

        Bet bet = new Bet(
                betRequest.betId(),
                betRequest.userId(),
                betRequest.jackpotId(),
                betRequest.betAmount()
        );

        contributionService.processContribution(bet);
    }
}
