package com.capg.lpu.finflow.application.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for RabbitMQ messaging infrastructure.
 * Defines queues, message converters, and templates for inter-service communication.
 */
@Configuration
public class RabbitMQConfig {

    /**
     * Name of the queue used for loan-related processing tasks.
     */
    public static final String LOAN_QUEUE = "loanQueue";

    /**
     * Name of the queue used for broad application-level event notifications.
     */
    public static final String APPLICATION_EVENT_QUEUE = "applicationEventQueue";

    /**
     * Defines a durable queue for loan processing.
     * Durable queues persist across broker restarts.
     *
     * @return a configured Queue instance for loan tasks.
     */
    @Bean
    public Queue loanQueue() {
        return new Queue(LOAN_QUEUE, true);
    }

    /**
     * Defines a durable queue for application event listening.
     *
     * @return a configured Queue instance for application events.
     */
    @Bean
    public Queue applicationEventQueue() {
        return new Queue(APPLICATION_EVENT_QUEUE, true);
    }

    /**
     * Configures a JSON message converter to facilitate structured data serialization.
     * Uses Jackson2 for mapping Java objects to JSON messages.
     *
     * @return a Jackson2JsonMessageConverter instance.
     */
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * Configures the RabbitTemplate for dispatching messages with JSON serialization.
     *
     * @param connectionFactory the connection factory to be used by the template.
     * @return a configured RabbitTemplate for message production.
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}