package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatchResponse {
    private Long id;
    private LocalDateTime time;
    private String note;
    private String imagePath;

    private FishDetailsDto fish;
    private LocationDto location;
    private WeatherDto weather;
    private WaterDto water;
    private AstronomyDto astronomy;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class FishDetailsDto {
        private String type;
        private Double weight;
        private Double length;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class LocationDto {
        private Double latitude;
        private Double longitude;
        private String city;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class WeatherDto {
        private Double temperatureC;
        private Double pressureMb;
        private String condition;
        private Integer clouds;
        private Double windSpeedKph;
        private String windDirection;
        private Double uv;
        private Double precipMm;
        private Integer humidity;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class WaterDto {
        private String temperature;
        private String flow;
        private String height;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class AstronomyDto {
        private String moonPhase;
        private Double illumination;
    }
}