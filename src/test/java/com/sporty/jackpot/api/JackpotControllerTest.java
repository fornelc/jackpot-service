package com.sporty.jackpot.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sporty.jackpot.api.dto.BetRequest;
import com.sporty.jackpot.api.dto.RewardResponse;
import com.sporty.jackpot.application.service.JackpotRewardService;
import com.sporty.jackpot.infrastructure.kafka.BetKafkaProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
class JackpotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BetKafkaProducer betKafkaProducer;

    @MockBean
    private JackpotRewardService rewardService;

    @Test
    void placeBet_returns202_andPublishesToKafka() throws Exception {
        BetRequest request = new BetRequest("bet-001", "user-123", "jackpot-fixed-001", new BigDecimal("100.00"));

        mockMvc.perform(post("/api/jackpot/bets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(content().string("Bet bet-001 submitted successfully."));

        verify(betKafkaProducer, times(1)).publish(any(BetRequest.class));
    }

    @Test
    void placeBet_returns400_whenBetAmountIsMissing() throws Exception {
        String invalidRequest = """
                {
                  "betId": "bet-001",
                  "userId": "user-123",
                  "jackpotId": "jackpot-fixed-001"
                }
                """;

        mockMvc.perform(post("/api/jackpot/bets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(betKafkaProducer);
    }

    @Test
    void placeBet_returns400_whenBetAmountIsZero() throws Exception {
        BetRequest request = new BetRequest("bet-001", "user-123", "jackpot-fixed-001", BigDecimal.ZERO);

        mockMvc.perform(post("/api/jackpot/bets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void evaluateReward_returns200_withRewardResponse() throws Exception {
        RewardResponse mockResponse = RewardResponse.win("bet-001", "user-123", "jackpot-fixed-001", new BigDecimal("1005.00"));
        when(rewardService.evaluateReward("bet-001")).thenReturn(mockResponse);

        mockMvc.perform(post("/api/jackpot/bets/bet-001/evaluate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.winner").value(true))
                .andExpect(jsonPath("$.betId").value("bet-001"))
                .andExpect(jsonPath("$.rewardAmount").value(1005.00));
    }

    @Test
    void evaluateReward_returns404_whenBetNotFound() throws Exception {
        when(rewardService.evaluateReward("unknown-bet"))
                .thenThrow(new IllegalArgumentException("No contribution found for betId: unknown-bet"));

        mockMvc.perform(post("/api/jackpot/bets/unknown-bet/evaluate"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }
}
