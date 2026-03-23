package com.finflow.document.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "doc_seq_gen")
    @SequenceGenerator(name = "doc_seq_gen", sequenceName = "doc_seq", allocationSize = 1)
    private Long id;

    private String content;
}