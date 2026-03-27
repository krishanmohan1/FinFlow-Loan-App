package com.capg.lpu.finflow.application.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * Message consumer service for the Application microservice.
 * Orchestrates responses to asynchronous events received via RabbitMQ.
 */
@Service
public class LoanConsumer {

    private static final Logger log = LoggerFactory.getLogger(LoanConsumer.class);

    /**
     * Listener method for application-level events from the message broker.
     * Processes verified or rejected document notifications and other general events.
     *
     * @param message The raw string payload received from the message queue.
     */
    @RabbitListener(queues = "applicationEventQueue")
    public void receiveMessage(String message) {
        log.info("[Application Service] Event received: {}", message);

        if (message.contains("DOCUMENT_VERIFIED")) {
            log.info("Document verification confirmed for loan: {}", message);
            // Future implementation: handle logic for verified documents
        } else if (message.contains("DOCUMENT_REJECTED")) {
            log.warn("Document rejected for loan: {}", message);
            // Future implementation: handle logic for rejected documents
        } else {
            log.info("General event received: {}", message);
        }
    }
}