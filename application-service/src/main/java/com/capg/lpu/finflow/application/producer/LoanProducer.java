package com.capg.lpu.finflow.application.producer;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.capg.lpu.finflow.application.config.RabbitMQConfig;

/**
 * Message producer service for the Application microservice.
 * Handles the dispatching of loan-related events and notifications to the message broker.
 */
@Service
@RequiredArgsConstructor
public class LoanProducer {

    private static final Logger log = LoggerFactory.getLogger(LoanProducer.class);

    private final RabbitTemplate rabbitTemplate;

    /**
     * Sends a message to the designated loan processing queue.
     * Includes error handling to ensure application stability if the broker is unavailable.
     *
     * @param message The serialized message payload to be sent.
     */
    public void sendMessage(String message) {
        try {
            log.info("Sending to queue [{}]: {}", RabbitMQConfig.LOAN_QUEUE, message);
            rabbitTemplate.convertAndSend(RabbitMQConfig.LOAN_QUEUE, message);
            log.info("Message sent to RabbitMQ successfully");
        } catch (Exception e) {
            log.error("Failed to send message to RabbitMQ: {}", e.getMessage(), e);
            log.warn("Loan was saved but Document Service was NOT notified via queue");
        }
    }
}