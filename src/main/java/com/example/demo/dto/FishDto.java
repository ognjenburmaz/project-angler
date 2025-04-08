package com.example.demo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class FishDto {
    private Long id;
    private String type;
    private Double length;
    private Double weight;
    private String note;
    private Double latitude;
    private Double longitude;
    private String city;
    private LocalDateTime createdAt;

    public FishDto(Long id, String type, Double length, Double weight, String note, LocalDateTime createdAt) {
        this.id = id;
        this.type = type;
        this.length = length;
        this.weight = weight;
        this.note = note;
        this.createdAt = createdAt;
    }
}
