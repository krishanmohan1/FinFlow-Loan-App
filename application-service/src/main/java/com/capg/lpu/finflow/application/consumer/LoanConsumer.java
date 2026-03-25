package com.capg.lpu.finflow.application.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class LoanConsumer {

    private static final Logger log = LoggerFactory.getLogger(LoanConsumer.class);

    // ✅ FIX: Now listens on its OWN dedicated queue → "applicationEventQueue"
    // ✅ No longer stealing messages from "loanQueue" which belongs to Document Service
    // ✅ This queue is for events sent BACK to Application Service
    //    e.g. Document verified, Document rejected etc.
    @RabbitListener(queues = "applicationEventQueue")
    public void receiveMessage(String message) {
        log.info("📩 [Application Service] Event received: {}", message);

        // ✅ Handle incoming events from other services
        if (message.contains("DOCUMENT_VERIFIED")) {
            log.info("✅ Document verification confirmed for loan: {}", message);
            // Future: update loan status, notify user etc.
        } else if (message.contains("DOCUMENT_REJECTED")) {
            log.warn("⚠️ Document rejected for loan: {}", message);
            // Future: flag loan, notify user etc.
        } else {
            log.info("ℹ️ General event received: {}", message);
        }
    }
}