package com.finflow.application.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.finflow.application.config.RabbitMQConfig;

@Service
public class LoanConsumer {

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void receiveMessage(String message) {
        System.out.println("📩 Message received: " + message);
    }
}