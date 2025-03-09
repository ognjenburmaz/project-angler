package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WeatherDTO {
    private Current current;

    public Current getCurrent() {
        return current;
    }

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

        public double getTempC() {
            return tempC;
        }

        public double getPressureMb() {
            return pressureMb;
        }

        public Condition getCondition() {
            return condition;
        }

        public int getCloud() {
            return cloud;
        }

        public double getWindKph() {
            return windKph;
        }

        public String getWindDir() {
            return windDir;
        }

        public double getUv() {
            return uv;
        }

        public double getPrecipMm() {
            return precipMm;
        }

        public int getHumidity() {
            return humidity;
        }
    }

    public static class Condition {
        @JsonProperty("text")
        private String text;

        public String getText() {
            return text;
        }
    }
}