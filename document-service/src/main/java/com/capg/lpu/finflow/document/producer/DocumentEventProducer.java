package com.capg.lpu.finflow.document.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.capg.lpu.finflow.document.config.RabbitMQConfig;
import com.capg.lpu.finflow.document.dto.DocumentVerificationEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Publishes document verification decisions so downstream services can react
 * asynchronously.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentEventProducer {

    private final RabbitTemplate rabbitTemplate;

    /**
     * Send a verification event back to the application service queue.
     *
     * @param event document verification event payload
     */
    public void publishVerificationEvent(DocumentVerificationEvent event) {
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.APPLICATION_EVENT_QUEUE, event);
            log.info("Published document verification event for loanId={} status={}",
                    event.getLoanId(), event.getStatus());
        } catch (Exception ex) {
            log.error("Failed to publish verification event for loanId={}", event.getLoanId(), ex);
        }
    }
}
