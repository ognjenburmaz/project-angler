package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class WeatherDto {
    private Current current;

    @Getter
    public static class Current {
        @JsonProperty("temp_c")
        private double tempC;

        @JsonProperty("pressure_mb")
        private double pressureMb;

        private Condition condition;

        @JsonProperty("cloud")
        private int cloud;

        @JsonProperty("wind_kph")
        private double windKph;

        @JsonProperty("wind_dir")
        private String windDir;

        @JsonProperty("uv")
        private double uv;

        @JsonProperty("precip_mm")
        private double precipMm;

        @JsonProperty("humidity")
        private int humidity;
    }

    @Getter
    public static class Condition {
        @JsonProperty("text")
        private String text;
    }
}