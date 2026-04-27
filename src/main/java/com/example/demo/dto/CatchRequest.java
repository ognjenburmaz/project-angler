package com.example.demo.dto;

import com.example.demo.enums.FishType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatchRequest {
    @Enumerated(EnumType.STRING)
    private FishType type;
    private Double weight;
    private Double length;
    private String note;
    private Double latitude;
    private Double longitude;
    private String city;
}