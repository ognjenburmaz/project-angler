package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FishDto {
    private String type;
    private Double length;
    private Double weight;
    private String note;
    private Double latitude;
    private Double longitude;
    private String city;
}
