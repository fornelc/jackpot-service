package com.sporty.jackpot.api.controller;

import com.sporty.jackpot.api.dto.BetRequest;
import com.sporty.jackpot.api.dto.RewardResponse;
import com.sporty.jackpot.application.service.JackpotRewardService;
import com.sporty.jackpot.infrastructure.kafka.BetKafkaProducer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/jackpot")
@RequiredArgsConstructor
public class JackpotController {

    private final BetKafkaProducer betKafkaProducer;
    private final JackpotRewardService rewardService;

    /**
     * POST /api/jackpot/bets
     * Publishes a bet to the Kafka jackpot-bets topic.
     * The Kafka consumer will then process the contribution asynchronously.
     */
    @PostMapping("/bets")
    public ResponseEntity<String> placeBet(@Valid @RequestBody BetRequest betRequest) {
        log.info("Received bet request: betId={}, jackpotId={}", betRequest.betId(), betRequest.jackpotId());
        betKafkaProducer.publish(betRequest);
        return ResponseEntity.accepted().body("Bet " + betRequest.betId() + " submitted successfully.");
    }

    /**
     * POST /api/jackpot/bets/{betId}/evaluate
     * Evaluates whether a previously contributed bet wins the jackpot reward.
     */
    @PostMapping("/bets/{betId}/evaluate")
    public ResponseEntity<RewardResponse> evaluateReward(@PathVariable String betId) {
        log.info("Evaluating jackpot reward for betId={}", betId);
        RewardResponse response = rewardService.evaluateReward(betId);
        return ResponseEntity.ok(response);
    }
}
