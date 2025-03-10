package com.example.demo.controller;

import com.example.demo.response.AstronomyResponse;
import com.example.demo.response.WeatherResponse;
import com.example.demo.service.AstronomyService;
import com.example.demo.service.WaterService;
import com.example.demo.service.WeatherService;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping(value = "/conditions")
public class ConditionsController {

    private final WaterService waterService;
    private final WeatherService weatherService;
    private final AstronomyService astronomyService;

    @Autowired
    public ConditionsController(WaterService waterService, WeatherService weatherService, AstronomyService astronomyService) {
        this.waterService = waterService;
        this.weatherService = weatherService;
        this.astronomyService = astronomyService;

    }

    @GetMapping()
    public String showConditionsPage(@RequestParam(defaultValue = "Sremska Mitrovica") String city,
                                    Model model) {
        try {
            model.addAttribute("city", city);

            List<Element> waterConditions = waterService.getWaterConditions(city);

            model.addAttribute("level", waterConditions.getFirst().text());
            model.addAttribute("levelChange", waterConditions.get(1).text());
            model.addAttribute("flow", waterConditions.get(2).text());
            model.addAttribute("temp", waterConditions.get(3).text());
            model.addAttribute("futureLevel1", waterConditions.get(4).text());
            model.addAttribute("futureLevel2", waterConditions.get(5).text());
            model.addAttribute("futureLevel3", waterConditions.get(6).text());

            WeatherResponse weatherResponse = weatherService.getWeatherConditions(city);

            model.addAttribute("lastApiCallTime", weatherService.getLastApiCallTime());

            model.addAttribute("airTemp", weatherResponse.getTemperatureC());
            model.addAttribute("pressure", weatherResponse.getPressureMb());
            model.addAttribute("description", weatherResponse.getCondition());
            model.addAttribute("clouds", weatherResponse.getClouds());
            model.addAttribute("uvIndex", weatherResponse.getUv());
            model.addAttribute("windSpeed", weatherResponse.getWindSpeedKph());
            model.addAttribute("windDirection", weatherResponse.getWindDirection());
            model.addAttribute("precip", weatherResponse.getPrecipMm());
            model.addAttribute("humidity", weatherResponse.getHumidity());

            AstronomyResponse astronomyResponse = astronomyService.getAstronomyConditions(city);

            model.addAttribute("sunrise", astronomyResponse.getSunrise());
            model.addAttribute("sunset", astronomyResponse.getSunset());
            model.addAttribute("moonPhase", astronomyResponse.getMoonPhase());
            model.addAttribute("illumination", astronomyResponse.getIllumination());

        } catch (IOException e) {
            model.addAttribute("error", "Failed to fetch data.");
        }

        return "conditions";
    }
}
