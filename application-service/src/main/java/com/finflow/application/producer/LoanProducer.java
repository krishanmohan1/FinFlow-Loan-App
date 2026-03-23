package com.finflow.application.producer;

import com.finflow.application.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanProducer {

    private static final Logger log = LoggerFactory.getLogger(LoanProducer.class);

    private final RabbitTemplate rabbitTemplate;

    public void sendMessage(String message) {
        log.info("📤 Sending to queue [{}]: {}", RabbitMQConfig.LOAN_QUEUE, message);
        rabbitTemplate.convertAndSend(RabbitMQConfig.LOAN_QUEUE, message);
        log.info("✅ Message sent to RabbitMQ successfully");
    }
}