package com.sporty.jackpot.api.controller;

import com.sporty.jackpot.api.dto.BetRequest;
import com.sporty.jackpot.api.dto.RewardResponse;
import com.sporty.jackpot.application.service.JackpotRewardService;
import com.sporty.jackpot.infrastructure.kafka.BetKafkaProducer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/jackpot")
@Tag(name = "Jackpot", description = "Jackpot contribution and reward endpoints")
public class JackpotController {

    private final BetKafkaProducer betKafkaProducer;
    private final JackpotRewardService rewardService;

    @Operation(summary = "Place a bet", description = "Publishes a bet to Kafka. The consumer processes the contribution asynchronously.")
    @ApiResponse(responseCode = "202", description = "Bet submitted successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request — missing or invalid fields")
    @PostMapping("/bets")
    public ResponseEntity<String> placeBet(@Valid @RequestBody BetRequest betRequest) {
        log.info("Received bet request: betId={}, jackpotId={}", betRequest.betId(), betRequest.jackpotId());
        betKafkaProducer.publish(betRequest);
        return ResponseEntity.accepted().body("Bet " + betRequest.betId() + " submitted successfully.");
    }

    @Operation(summary = "Evaluate jackpot reward", description = "Evaluates whether a contributing bet wins the jackpot. The bet must have been processed by Kafka before calling this.")
    @ApiResponse(responseCode = "200", description = "Evaluation result returned — check 'winner' field")
    @ApiResponse(responseCode = "404", description = "No contribution found for the given betId")
    @ApiResponse(responseCode = "500", description = "Internal data integrity error")
    @PostMapping("/bets/{betId}/evaluate")
    public ResponseEntity<RewardResponse> evaluateReward(@PathVariable String betId) {
        log.info("Evaluating jackpot reward for betId={}", betId);
        RewardResponse response = rewardService.evaluateReward(betId);
        return ResponseEntity.ok(response);
    }
}
