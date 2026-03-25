package com.capg.lpu.finflow.application.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // ✅ This queue is consumed by Document Service
    // Application Service ONLY sends to this queue — never listens
    public static final String LOAN_QUEUE = "loanQueue";

    // ✅ FIX: NEW dedicated queue for Application Service to receive events
    // Document Service (or others) send back events on this queue
    // e.g. DOCUMENT_VERIFIED, DOCUMENT_REJECTED
    public static final String APPLICATION_EVENT_QUEUE = "applicationEventQueue";

    // ✅ loanQueue — Document Service listens here
    @Bean
    public Queue loanQueue() {
        // durable=true → survives RabbitMQ restart
        return new Queue(LOAN_QUEUE, true);
    }

    // ✅ applicationEventQueue — Application Service listens here
    @Bean
    public Queue applicationEventQueue() {
        // durable=true → survives RabbitMQ restart
        return new Queue(APPLICATION_EVENT_QUEUE, true);
    }

    // ✅ JSON message converter for serialization
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // ✅ RabbitTemplate uses JSON converter
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}