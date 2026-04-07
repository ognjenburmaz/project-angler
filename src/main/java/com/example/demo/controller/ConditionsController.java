package com.example.demo.controller;

import com.example.demo.dto.CatchResponse;
import com.example.demo.service.AstronomyService;
import com.example.demo.service.WaterService;
import com.example.demo.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/conditions")
@RequiredArgsConstructor
public class ConditionsController {

    private final WaterService waterService;
    private final WeatherService weatherService;
    private final AstronomyService astronomyService;

    @GetMapping
    public String showConditionsPage(@RequestParam(defaultValue = "Sremska Mitrovica") String city, Model model) {
        try {
            List<Element> waterElements = waterService.getWaterConditions(city);

            CatchResponse.WeatherDto weather = weatherService.getWeatherConditions(city);
            CatchResponse.AstronomyDto astronomy = astronomyService.getAstronomyConditions(city);

            CatchResponse.WaterDto water = CatchResponse.WaterDto.builder()
                    .height(waterElements.get(0).text())
                    .flow(waterElements.get(2).text())
                    .temperature(waterElements.get(3).text())
                    .build();

            model.addAttribute("city", city);
            model.addAttribute("weather", weather);
            model.addAttribute("astronomy", astronomy);
            model.addAttribute("water", water);
            model.addAttribute("waterElements", waterElements);
            model.addAttribute("lastApiCall", weatherService.getLastApiCallTime());

        } catch (IOException e) {
            model.addAttribute("error", "Failed to fetch environmental data for " + city);
        }

        return "conditions";
    }
}