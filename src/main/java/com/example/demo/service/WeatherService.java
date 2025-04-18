package com.example.demo.service;

import com.example.demo.dto.WeatherDto;
import com.example.demo.response.WeatherResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class WeatherService {

    @Value("${weather.api.key}")
    String apiKey;

    private static final String CURRENT_WEATHER_URL = "https://api.weatherapi.com/v1/current.json?key={apiKey}&q={city}";

    private final RestTemplate restTemplate;
    private LocalDateTime lastApiCallTime;

    public WeatherService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Cacheable(value = "weatherCache", key = "#city", unless = "#result == null")
    public WeatherResponse getWeatherConditions(String city) {
        return getWeatherData(city);
    }

    protected WeatherResponse getWeatherData(String city) {
        System.out.println("Calling Weather API for city: " + city);
        lastApiCallTime = LocalDateTime.now();

        String url = CURRENT_WEATHER_URL.replace("{city}", city).replace("{apiKey}", apiKey);
        WeatherDto weatherDTO = restTemplate.getForObject(url, WeatherDto.class);

        WeatherResponse weatherResponse = new WeatherResponse();
        assert weatherDTO != null;
        weatherResponse.setTemperatureC(weatherDTO.getCurrent().getTempC());
        weatherResponse.setPressureMb(weatherDTO.getCurrent().getPressureMb());
        weatherResponse.setCondition(weatherDTO.getCurrent().getCondition().getText());
        weatherResponse.setClouds(weatherDTO.getCurrent().getCloud());
        weatherResponse.setWindSpeedKph(weatherDTO.getCurrent().getWindKph());
        weatherResponse.setWindDirection(weatherDTO.getCurrent().getWindDir());
        weatherResponse.setUv(weatherDTO.getCurrent().getUv());
        weatherResponse.setPrecipMm(weatherDTO.getCurrent().getPrecipMm());
        weatherResponse.setHumidity(weatherDTO.getCurrent().getHumidity());

        return weatherResponse;
    }

    @CacheEvict(value = "weatherCache", key = "#city")
    public void evictCache(String city) {
        System.out.println("Evicting weather cache at " + LocalDateTime.now());
    }

    public String getLastApiCallTime() {
        if (lastApiCallTime == null) {
            return "API has not been called yet.";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return lastApiCallTime.format(formatter);
    }
}
