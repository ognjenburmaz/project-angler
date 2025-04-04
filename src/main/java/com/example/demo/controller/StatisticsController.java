package com.example.demo.controller;

import com.example.demo.dto.FishDto;
import com.example.demo.model.CaughtFish;
import com.example.demo.model.User;
import com.example.demo.response.AstronomyResponse;
import com.example.demo.response.WeatherResponse;
import com.example.demo.service.*;
import org.jsoup.nodes.Element;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(value = "/statistics")
public class StatisticsController {

    private final UserService userService;
    private final CaughtFishService caughtFishService;
    private final WeatherService weatherService;
    private final WaterService waterService;
    private final AstronomyService astronomyService;

    public StatisticsController(UserService userService, CaughtFishService caughtFishService, WeatherService weatherService, WaterService waterService, AstronomyService astronomyService) {
        this.userService = userService;
        this.caughtFishService = caughtFishService;
        this.weatherService = weatherService;
        this.waterService = waterService;
        this.astronomyService = astronomyService;
    }

    @GetMapping(value = "/addFish")
    public String addFish(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            model.addAttribute("username", username);
            model.addAttribute("fishDto", new FishDto());
            return "addFish";
        }
        return "redirect:/login";
    }

    @PostMapping(value = "/addFish")
    public String addNewFish(@ModelAttribute FishDto fishDto, @RequestParam(name = "picture", required = false) MultipartFile file) throws IOException {
        if (file.getSize() > 5 * 1024 * 1024) {
            // Handle size error
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Optional<User> user = userService.findByUsername(authentication.getName());
            AstronomyResponse astronomyResponse = astronomyService.getAstronomyConditions(fishDto.getCity());
            WeatherResponse weatherResponse = weatherService.getWeatherConditions(fishDto.getCity());
            List<Element> waterResponse = waterService.getWaterData(fishDto.getCity());
            CaughtFish caughtFish = new CaughtFish(fishDto.getType(), fishDto.getWeight(), fishDto.getLength(), fishDto.getNote(),
                    user.get(), weatherResponse.getTemperatureC(), weatherResponse.getPressureMb(),
                    weatherResponse.getCondition(), weatherResponse.getClouds(), weatherResponse.getUv(),
                    weatherResponse.getWindSpeedKph(), weatherResponse.getWindDirection(), weatherResponse.getPrecipMm(), weatherResponse.getHumidity(),
                    waterResponse.get(3).text(), waterResponse.get(2).text(), waterResponse.getFirst().text(),
                    astronomyResponse.getMoonPhase(), astronomyResponse.getIllumination(), fishDto.getLongitude(), fishDto.getLatitude(),
                    fishDto.getCity(), LocalDateTime.now(), null);
            caughtFishService.saveCaughtFish(caughtFish, file);
        return "redirect:/statistics/myFish";
        }
        return "redirect:/login";
    }

    @GetMapping(value = "/myFish")
    public String myFish(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            List<CaughtFish> allFish = caughtFishService.getAllByOwnerId(userService.findByUsername(username).get());
            model.addAttribute("allFish", allFish);
            return "allFish";
        }
        return "redirect:/login";
    }

    @PostMapping(value = "/myFish/delete/{id}")
    public String deleteFish(@ModelAttribute FishDto fishDto, @PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            caughtFishService.deleteCaughtFish(id);
            return "redirect:/statistics/myFish";
        }
        return "redirect:/login";
    }

    @GetMapping(value = "/myFish/view/{id}")
    public String viewFish(Model model, @PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
        Optional<CaughtFish> fish = caughtFishService.getCaughtFish(id);

        DateTimeFormatter displayFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String formattedDate = fish.get().getTime().format(displayFormat);

        model.addAttribute("fish", fish.get());
        model.addAttribute("formattedTime", formattedDate);
            return "viewFish";
        }
        return "redirect:/login";
    }
}
