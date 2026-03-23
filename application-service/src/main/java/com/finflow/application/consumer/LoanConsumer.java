package com.finflow.application.consumer;

import com.finflow.application.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class LoanConsumer {

    private static final Logger log = LoggerFactory.getLogger(LoanConsumer.class);

    // Application Service can optionally listen for its own echo or future events
    @RabbitListener(queues = RabbitMQConfig.LOAN_QUEUE)
    public void receiveMessage(String message) {
        log.info("📩 [Application Consumer] Message received from queue: {}", message);
        // Future: trigger notifications, auditing, etc.
    }
}