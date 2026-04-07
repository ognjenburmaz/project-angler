package com.example.demo.service;

import com.example.demo.dto.AstronomyDto;
import com.example.demo.dto.CatchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AstronomyService {
    @Value("${weather.api.key}")
    private String apiKey;
    private final RestTemplate restTemplate;

    @Cacheable(value = "astronomyCache", key = "#city")
    public CatchResponse.AstronomyDto getAstronomyConditions(String city) {
        String url = "https://api.weatherapi.com/v1/astronomy.json?key=" + apiKey + "&q=" + city;
        AstronomyDto dto = restTemplate.getForObject(url, AstronomyDto.class);

        assert dto != null;
        return CatchResponse.AstronomyDto.builder()
                .moonPhase(dto.getAstronomy().getAstro().getMoonPhase())
                .illumination((double) dto.getAstronomy().getAstro().getMoonIllumination())
                .build();
    }
}
