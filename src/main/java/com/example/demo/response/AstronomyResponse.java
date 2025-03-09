package com.example.demo.response;

public class AstronomyResponse {
    private String sunrise;
    private String sunset;
    private String moonPhase;
    private double illumination;

    public String getSunrise() {
        return sunrise;
    }

    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public void setSunset(String sunset) {
        this.sunset = sunset;
    }

    public String getMoonPhase() {
        return moonPhase;
    }

    public void setMoonPhase(String moonPhase) {
        this.moonPhase = moonPhase;
    }

    public double getIllumination() {
        return illumination;
    }

    public void setIllumination(double illumination) {
        this.illumination = illumination;
    }

}
