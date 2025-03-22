package com.example.demo.service;

import com.example.demo.dto.AstronomyDto;
import com.example.demo.response.AstronomyResponse;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
public class AstronomyService {

    Dotenv dotenv = Dotenv.load();
    String API_KEY = dotenv.get("WEATHER_API_KEY");
    private static final String ASTRONOMY_URL = "https://api.weatherapi.com/v1/astronomy.json?key={apiKey}&q={city}";

    private final RestTemplate restTemplate;

    public AstronomyService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    @Cacheable(value = "astronomyCache", key = "#city", unless = "#result == null")
    public AstronomyResponse getAstronomyConditions(String city) {
        return getAstronomyData(city);
    }

    public AstronomyResponse getAstronomyData(String city) {
        System.out.println("Calling Astronomy API for city: " + city);

        String url = ASTRONOMY_URL.replace("{city}", city).replace("{apiKey}", API_KEY);
        AstronomyDto astronomyDTO = restTemplate.getForObject(url, AstronomyDto.class);

        AstronomyResponse astronomyResponse = new AstronomyResponse();
        assert astronomyDTO != null;
        astronomyResponse.setSunrise(astronomyDTO.getAstronomy().getAstro().getSunrise());
        astronomyResponse.setSunset(astronomyDTO.getAstronomy().getAstro().getSunset());
        astronomyResponse.setMoonPhase(astronomyDTO.getAstronomy().getAstro().getMoonPhase());
        astronomyResponse.setIllumination(astronomyDTO.getAstronomy().getAstro().getMoonIllumination());

        return astronomyResponse;
    }

    @CacheEvict(value = "astronomyCache", key = "#city")
    public void evictCache(String city) {
        System.out.println("Evicting astronomy cache at " + LocalDateTime.now());
    }
}
