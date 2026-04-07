package com.example.demo.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatchRequest {
    private String type;
    private Double weight;
    private Double length;
    private String note;
    private Double latitude;
    private Double longitude;
    private String city;
}