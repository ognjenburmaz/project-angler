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
public class WeatherSnapshot {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double airTemperature;
    private Double airPressure;
    private String weatherCondition;
    private Integer cloudCover;
    private Double uvIndex;
    private Double windSpeed;
    private String windDirection;
    private Double rainPrecipitation;
    private Integer humidity;
}