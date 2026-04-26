package com.capg.lpu.finflow.application.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.capg.lpu.finflow.application.config.RabbitMQConfig;
import com.capg.lpu.finflow.application.dto.LoanEventMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Message producer service for the Application microservice.
 * Handles the dispatching of loan-related events and notifications to the message broker.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LoanProducer {

    private final RabbitTemplate rabbitTemplate;

    /**
     * Publishes a strongly typed event when a new loan is created.
     *
     * @param message event payload
     */
    public void sendLoanCreated(LoanEventMessage message) {
        try {
            log.info("Sending event [{}] to queue [{}]", message.getEventType(), RabbitMQConfig.LOAN_QUEUE);
            rabbitTemplate.convertAndSend(RabbitMQConfig.LOAN_QUEUE, message);
            log.info("Message sent to RabbitMQ successfully");
        } catch (Exception e) {
            log.error("Failed to send message to RabbitMQ: {}", e.getMessage(), e);
            log.warn("Loan was saved but Document Service was NOT notified via queue");
        }
    }

    /**
     * Publishes a strongly typed event when a loan status changes.
     *
     * @param message event payload
     */
    public void sendLoanStatusUpdated(LoanEventMessage message) {
        sendLoanCreated(message);
    }
}
