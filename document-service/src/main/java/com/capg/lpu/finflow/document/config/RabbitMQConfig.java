package com.capg.lpu.finflow.document.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // ✅ Document Service LISTENS on this queue
    // Application Service SENDS to this queue
    // When a new loan is applied or status is updated
    public static final String LOAN_QUEUE = "loanQueue";

    // ✅ FIX: NEW — Document Service SENDS on this queue
    // Application Service LISTENS on this queue
    // When a document is verified or rejected by ADMIN
    public static final String APPLICATION_EVENT_QUEUE = "applicationEventQueue";

    // ✅ loanQueue — Document Service consumes from here
    @Bean
    public Queue loanQueue() {
        // durable=true → survives RabbitMQ restart
        return new Queue(LOAN_QUEUE, true);
    }

    // ✅ applicationEventQueue — Application Service consumes from here
    // Document Service sends events back on this queue
    @Bean
    public Queue applicationEventQueue() {
        // durable=true → survives RabbitMQ restart
        return new Queue(APPLICATION_EVENT_QUEUE, true);
    }

    // ✅ JSON message converter for proper serialization
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // ✅ RabbitTemplate — used by DocumentService to send
    //    events back to Application Service
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}