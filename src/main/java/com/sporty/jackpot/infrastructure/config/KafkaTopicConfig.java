package com.sporty.jackpot.infrastructure.config;

import com.sporty.jackpot.infrastructure.kafka.BetKafkaProducer;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic jackpotBetsTopic() {
        return TopicBuilder.name(BetKafkaProducer.TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
