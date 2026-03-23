package com.finflow.document.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.finflow.document.entity.Document;
import com.finflow.document.repository.DocumentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoanConsumer {

    private final DocumentRepository repository;

    @RabbitListener(queues = "loanQueue")
    public void receiveMessage(String message) {

        Document doc = new Document();
        doc.setContent(message);

        repository.save(doc);

        System.out.println("📄 Document saved: " + message);
    }
}