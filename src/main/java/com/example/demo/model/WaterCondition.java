package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Entity @NoArgsConstructor @AllArgsConstructor @Builder
public class WaterCondition {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String waterTemperature;
    private String waterFlow;
    private String waterHeight;
}