package com.example.ecommerce.models;

import jakarta.persistence.*;

@Entity
public class EnergyMonitoringData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Lob = "large object"
    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    // getters/setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}