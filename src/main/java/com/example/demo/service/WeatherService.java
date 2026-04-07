package com.example.demo.service;

import com.example.demo.dto.CatchResponse;
import com.example.demo.dto.WeatherDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class WeatherService {
    @Value("${weather.api.key}")
    private String apiKey;
    private final RestTemplate restTemplate;
    private LocalDateTime lastApiCallTime;

    @Cacheable(value = "weatherCache", key = "#city")
    public CatchResponse.WeatherDto getWeatherConditions(String city) {
        this.lastApiCallTime = LocalDateTime.now(); // Track the time here
        String url = "https://api.weatherapi.com/v1/current.json?key=" + apiKey + "&q=" + city;
        WeatherDto dto = restTemplate.getForObject(url, WeatherDto.class);

        return CatchResponse.WeatherDto.builder()
                .temperatureC(dto.getCurrent().getTempC())
                .pressureMb(dto.getCurrent().getPressureMb())
                .condition(dto.getCurrent().getCondition().getText())
                .clouds(dto.getCurrent().getCloud())
                .windSpeedKph(dto.getCurrent().getWindKph())
                .windDirection(dto.getCurrent().getWindDir())
                .uv(dto.getCurrent().getUv())
                .precipMm(dto.getCurrent().getPrecipMm())
                .humidity(dto.getCurrent().getHumidity())
                .build();
    }

    public String getLastApiCallTime() {
        if (lastApiCallTime == null) return "Never";
        return lastApiCallTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
