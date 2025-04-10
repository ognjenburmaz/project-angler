package com.example.demo.controller;

import com.example.demo.dto.FishDto;
import com.example.demo.model.CaughtFish;
import com.example.demo.model.User;
import com.example.demo.response.AstronomyResponse;
import com.example.demo.response.WeatherResponse;
import com.example.demo.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.jsoup.nodes.Element;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Controller
@RequestMapping(value = "/statistics")
public class StatisticsController {

    private final UserService userService;
    private final CaughtFishService caughtFishService;
    private final WeatherService weatherService;
    private final WaterService waterService;
    private final AstronomyService astronomyService;
    private final LocaleResolver localeResolver;

    public StatisticsController(UserService userService, CaughtFishService caughtFishService, WeatherService weatherService,
                                WaterService waterService, AstronomyService astronomyService, LocaleResolver localeResolver) {
        this.userService = userService;
        this.caughtFishService = caughtFishService;
        this.weatherService = weatherService;
        this.waterService = waterService;
        this.astronomyService = astronomyService;
        this.localeResolver = localeResolver;
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
    public String addNewFish(@ModelAttribute FishDto fishDto,
                             @RequestParam(name = "picture", required = false) MultipartFile file, RedirectAttributes redirectAttributes,
                             Model model) {
        try {
            if (file.getSize() > 5 * 1024 * 1024) {
                throw new MaxUploadSizeExceededException(5 * 1024 * 1024);
            }
            if (!(fishDto.getCity().equals("Sremska Mitrovica") || fishDto.getCity().equals("Novi Sad") || fishDto.getCity().equals("Belgrade"))) {
                model.addAttribute("error", "City picked does not match required format");
                return "addFish";
            }
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
                User user = userService.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("User not found: " + username));
                AstronomyResponse astronomyResponse = astronomyService.getAstronomyConditions(fishDto.getCity());
                WeatherResponse weatherResponse = weatherService.getWeatherConditions(fishDto.getCity());
                List<Element> waterResponse = waterService.getWaterConditions(fishDto.getCity());
                CaughtFish caughtFish = new CaughtFish(fishDto.getType(), fishDto.getWeight(), fishDto.getLength(), fishDto.getNote(),
                        user, weatherResponse.getTemperatureC(), weatherResponse.getPressureMb(),
                        weatherResponse.getCondition(), weatherResponse.getClouds(), weatherResponse.getUv(),
                        weatherResponse.getWindSpeedKph(), weatherResponse.getWindDirection(), weatherResponse.getPrecipMm(), weatherResponse.getHumidity(),
                        waterResponse.get(3).text(), waterResponse.get(2).text(), waterResponse.getFirst().text(),
                        astronomyResponse.getMoonPhase(), astronomyResponse.getIllumination(), fishDto.getLongitude(), fishDto.getLatitude(),
                        fishDto.getCity(), LocalDateTime.now(), null);
                caughtFishService.saveCaughtFish(caughtFish, file);
                user.setTotalCatches(user.getTotalCatches() + 1);
                userService.update(user);
                return "redirect:/statistics/myFish";
        } catch (MaxUploadSizeExceededException e) {
            redirectAttributes.addFlashAttribute("error",
                    "File too large! Maximum 5MB allowed.");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error",
                    "Upload failed: " + e.getMessage());
        }
        model.addAttribute("error", "Unknown error");
        return "errorPage";
    }

    @Transactional(readOnly = true)
    @GetMapping(value = "/myFish")
    public String myFish(Model model, HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return "redirect:/login";

        List<FishDto> fishDTOs = caughtFishService.getAllByOwnerId(
                        userService.findByUsername(auth.getName()).get()
                ).stream()
                .map(fish -> new FishDto(
                        fish.getId(),
                        translateType(fish.getType(), localeResolver.resolveLocale(request)),
                        fish.getWeight(),
                        fish.getLength(),
                        fish.getNote(),
                        fish.getTime()
                ))
                .toList();

        model.addAttribute("allFish", fishDTOs);
        return "allFish";
    }

    @PostMapping(value = "/myFish/delete/{id}")
    public String deleteFish(@PathVariable Long id, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Optional<CaughtFish> fish = caughtFishService.getCaughtFish(id);
            if (fish.isEmpty()) {
                model.addAttribute("fishDto", new FishDto());
                model.addAttribute("error","Fish not found");
                return "errorPage";
            }
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

        if (fish.isEmpty()) {
            model.addAttribute("fishDto", new FishDto());
            model.addAttribute("error","Fish not found");
            return "errorPage";
        }

        DateTimeFormatter displayFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String formattedDate = fish.get().getTime().format(displayFormat);

        model.addAttribute("fish", fish.get());
        model.addAttribute("formattedTime", formattedDate);
            return "viewFish";
        }
        return "redirect:/login";
    }

    private String translateType(String type, Locale locale) {
        if (!"en".equals(locale.getLanguage())) return type;
        return switch (type) {
            case "Smuđ" -> "Zander";
            case "Som" -> "Catfish";
            case "Bandar" -> "Perch";
            case "Bucov" -> "Asp";
            case "Štuka" -> "Pike";
            case "Šaran" -> "Common Carp";
            case "Deverika" -> "Bream";
            case "Babuška" -> "Prussian Carp";
            case "Mrena" -> "Barbel";
            case "Podust" -> "Common Nase";
            default -> type;
        };
    }
}
