package com.sporty.jackpot.infrastructure.kafka;

import com.sporty.jackpot.api.dto.BetRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BetKafkaProducer {

    public static final String TOPIC = "jackpot-bets";

    private final KafkaTemplate<String, BetRequest> kafkaTemplate;

    public void publish(BetRequest bet) {
        log.info("Publishing bet to Kafka topic '{}': betId={}, userId={}, jackpotId={}, amount={}",
                TOPIC, bet.betId(), bet.userId(), bet.jackpotId(), bet.betAmount());

        kafkaTemplate.send(TOPIC, bet.betId(), bet)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish bet {}: {}", bet.betId(), ex.getMessage());
                    } else {
                        log.info("Bet {} published to partition {} offset {}",
                                bet.betId(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
}
