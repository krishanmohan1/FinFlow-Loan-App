package com.capg.lpu.finflow.application.producer;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.capg.lpu.finflow.application.config.RabbitMQConfig;

@Service
@RequiredArgsConstructor
public class LoanProducer {

    private static final Logger log = LoggerFactory.getLogger(LoanProducer.class);

    private final RabbitTemplate rabbitTemplate;

    // ✅ Send message to RabbitMQ queue
    // ✅ FIX: Wrapped in try-catch so RabbitMQ failure does NOT
    //         crash the loan save operation
    public void sendMessage(String message) {
        try {
            log.info("📤 Sending to queue [{}]: {}", RabbitMQConfig.LOAN_QUEUE, message);
            rabbitTemplate.convertAndSend(RabbitMQConfig.LOAN_QUEUE, message);
            log.info("✅ Message sent to RabbitMQ successfully");
        } catch (Exception e) {
            // ✅ Log the error but do NOT rethrow
            // Loan is already saved in DB — RabbitMQ failure is non-critical
            log.error("🔴 Failed to send message to RabbitMQ: {}", e.getMessage(), e);
            log.warn("⚠️ Loan was saved but Document Service was NOT notified via queue");
        }
    }
}