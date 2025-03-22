package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AstronomyDto {

    private Astronomy astronomy;

    public Astronomy getAstronomy() {
        return astronomy;
    }

    public static class Astronomy {

        @JsonProperty("astro")
        private Astro astro;

        public Astro getAstro() {
            return astro;
        }
    }

    public static class Astro {
        @JsonProperty("sunrise")
        private String sunrise;

        @JsonProperty("sunset")
        private String sunset;

        @JsonProperty("moonrise")
        private String moonrise;

        @JsonProperty("moonset")
        private String moonset;

        @JsonProperty("moon_phase")
        private String moonPhase;

        @JsonProperty("moon_illumination")
        private int moonIllumination;

        @JsonProperty("is_moon_up")
        private int isMoonUp;

        @JsonProperty("is_sun_up")
        private int isSunUp;

        public String getSunrise() {
            return sunrise;
        }

        public String getSunset() {
            return sunset;
        }

        public String getMoonrise() {
            return moonrise;
        }

        public String getMoonset() {
            return moonset;
        }

        public String getMoonPhase() {
            return moonPhase;
        }


        public int getMoonIllumination() {
            return moonIllumination;
        }

        public int getIsMoonUp() {
            return isMoonUp;
        }

        public int getIsSunUp() {
            return isSunUp;
        }
    }
}