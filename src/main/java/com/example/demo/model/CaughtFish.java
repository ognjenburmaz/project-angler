package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "caught_fish")
public class CaughtFish {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String type;
    @Column(nullable = false)
    private Double weight;
    @Column(nullable = false)
    private Double length;
    @Column(nullable = false)
    private String note;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User owner;

    @Column(nullable = false)
    private Double airTemperature;
    @Column(nullable = false)
    private Double airPressure;
    @Column(nullable = false)
    private String weatherCondition;
    @Column(nullable = false)
    private Integer cloudCover;
    @Column(nullable = false)
    private Double uvIndex;
    @Column(nullable = false)
    private Double windSpeed;
    @Column(nullable = false)
    private String windDirection;
    @Column(nullable = false)
    private Double rainPrecipitation;
    @Column(nullable = false)
    private Integer humidity;
    @Column(nullable = false)
    private String waterTemperature;
    @Column(nullable = false)
    private String waterFlow;
    @Column(nullable = false)
    private String waterHeight;
    @Column(nullable = false)
    private String moonPhase;
    @Column(nullable = false)
    private Double illumination;

    @Column(nullable = false)
    private Double longitude;
    @Column(nullable = false)
    private Double latitude;
    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private LocalDateTime time;

    @Column
    private String imagePath;

    public CaughtFish(String type, Double weight, Double length, String note,
                      User owner, Double airTemperature, Double airPressure,
                      String weatherCondition, Integer cloudCover, Double uvIndex,
                      Double windSpeed, String windDirection, Double rainPrecipitation, Integer humidity,
                      String waterTemperature, String waterFlow, String waterHeight,
                      String moonPhase, Double illumination, Double longitude, Double latitude,
                      String city, LocalDateTime time, String imagePath) {
        this.type = type;
        this.weight = weight;
        this.length = length;
        this.note = note;
        this.owner = owner;
        this.airTemperature = airTemperature;
        this.airPressure = airPressure;
        this.weatherCondition = weatherCondition;
        this.cloudCover = cloudCover;
        this.uvIndex = uvIndex;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        this.rainPrecipitation = rainPrecipitation;
        this.humidity = humidity;
        this.waterTemperature = waterTemperature;
        this.waterFlow = waterFlow;
        this.waterHeight = waterHeight;
        this.moonPhase = moonPhase;
        this.illumination = illumination;
        this.longitude = longitude;
        this.latitude = latitude;
        this.city = city;
        this.time = time;
        this.imagePath = imagePath;
    }
}
