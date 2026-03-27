package com.capg.lpu.finflow.document.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for RabbitMQ messaging infrastructure in the Document service.
 * Defines queues, message converters, and specialized templates for inter-service communication.
 */
@Configuration
public class RabbitMQConfig {

    /**
     * Queue for receiving loan application events to initiate document processing.
     */
    public static final String LOAN_QUEUE = "loanQueue";

    /**
     * Queue for broadcasting verification updates and document status changes back to the Application service.
     */
    public static final String APPLICATION_EVENT_QUEUE = "applicationEventQueue";

    /**
     * Defines the durable queue for processing loan-related document tasks.
     *
     * @return A durable Queue instance for LOAN_QUEUE.
     */
    @Bean
    public Queue loanQueue() {
        return new Queue(LOAN_QUEUE, true);
    }

    /**
     * Defines the durable queue for sending event updates back to the application microservice.
     *
     * @return A durable Queue instance for APPLICATION_EVENT_QUEUE.
     */
    @Bean
    public Queue applicationEventQueue() {
        return new Queue(APPLICATION_EVENT_QUEUE, true);
    }

    /**
     * Configures a JSON message converter for serializing and deserializing message payloads.
     *
     * @return A Jackson2JsonMessageConverter for standardized JSON processing.
     */
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * Configures the RabbitTemplate for reliable message dispatching with JSON support.
     *
     * @param connectionFactory The factory responsible for maintaining broker connections.
     * @return A configured RabbitTemplate for document service event production.
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}