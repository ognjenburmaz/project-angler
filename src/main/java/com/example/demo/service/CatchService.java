package com.example.demo.service;

import com.example.demo.dto.CatchRequest;
import com.example.demo.dto.CatchResponse;
import com.example.demo.model.*;
import com.example.demo.repository.CatchRepository;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CatchService {
    private final CatchRepository catchRepository;
    private final UserRepository userRepository;
    private final WeatherService weatherService;
    private final WaterService waterService;
    private final AstronomyService astronomyService;

    private static final String IMAGE_DIR =  "user-photos";

    @Transactional
    public void createCatch(CatchRequest request, MultipartFile file, String username) throws IOException {
        User user = userRepository.findByUsername(username).orElseThrow();

        var weatherDto = weatherService.getWeatherConditions(request.getCity());
        List<Element> waterElements = waterService.getWaterConditions(request.getCity());
        var waterDto = waterService.mapToDto(waterElements);
        var astroDto = astronomyService.getAstronomyConditions(request.getCity());

        FishDetails fish = FishDetails.builder()
                .type(request.getType()).weight(request.getWeight()).length(request.getLength()).build();

        Location loc = Location.builder()
                .latitude(request.getLatitude()).longitude(request.getLongitude()).city(request.getCity()).build();

        WeatherSnapshot weather = WeatherSnapshot.builder()
                .airTemperature(weatherDto.getTemperatureC())
                .airPressure(weatherDto.getPressureMb())
                .weatherCondition(weatherDto.getCondition())
                .cloudCover(weatherDto.getClouds())
                .uvIndex(weatherDto.getUv())
                .windSpeed(weatherDto.getWindSpeedKph())
                .windDirection(weatherDto.getWindDirection())
                .rainPrecipitation(weatherDto.getPrecipMm())
                .humidity(weatherDto.getHumidity())
                .build();

        WaterCondition water = WaterCondition.builder()
                .waterHeight(waterDto.getHeight()).waterFlow(waterDto.getFlow()).waterTemperature(waterDto.getTemperature()).build();

        Astronomy astro = Astronomy.builder()
                .moonPhase(astroDto.getMoonPhase()).illumination(astroDto.getIllumination()).build();

        Catch newCatch = Catch.builder()
                .time(LocalDateTime.now())
                .note(request.getNote())
                .user(user)
                .fishDetails(fish)
                .location(loc)
                .weather(weather)
                .waterCondition(water)
                .astronomy(astro)
                .imagePath(saveImage(file))
                .build();

        catchRepository.save(newCatch);

        user.setTotalCatches(user.getTotalCatches() + 1);
        userRepository.save(user);
    }

    private String saveImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;

        Path uploadPath = Paths.get(IMAGE_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }

    public void deleteCatch(Long id) {
        Catch c = catchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catch not found"));

        if (c.getImagePath() != null) {
            try {
                Files.deleteIfExists(Paths.get(IMAGE_DIR).resolve(c.getImagePath()));
            } catch (IOException e) {
                System.err.println("Failed to delete image: " + e.getMessage());
            }
        }

        catchRepository.delete(c);
    }

    public CatchResponse getCatchDetails(Long id) {
        return catchRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Catch not found with ID: " + id));
    }

    public List<CatchResponse> getAllUserCatches(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        return catchRepository.findAllByUser(user).stream()
                .map(this::mapToResponse)
                .toList();
    }

    public CatchResponse getLastUserCatch(User user) {
        return catchRepository.findFirstByUserOrderByIdDesc(user)
                .map(this::mapToResponse)
                .orElse(null);
    }

    private CatchResponse mapToResponse(Catch c) {
        return CatchResponse.builder()
                .id(c.getId())
                .time(c.getTime())
                .note(c.getNote())
                .imagePath(c.getImagePath())

                .fish(CatchResponse.FishDetailsDto.builder()
                        .type(c.getFishDetails().getType())
                        .weight(c.getFishDetails().getWeight())
                        .length(c.getFishDetails().getLength())
                        .build())

                .location(CatchResponse.LocationDto.builder()
                        .latitude(c.getLocation().getLatitude())
                        .longitude(c.getLocation().getLongitude())
                        .city(c.getLocation().getCity())
                        .build())

                .weather(CatchResponse.WeatherDto.builder()
                        .temperatureC(c.getWeather().getAirTemperature())
                        .pressureMb(c.getWeather().getAirPressure())
                        .condition(c.getWeather().getWeatherCondition())
                        .clouds(c.getWeather().getCloudCover())
                        .uv(c.getWeather().getUvIndex())
                        .precipMm(c.getWeather().getRainPrecipitation())
                        .humidity(c.getWeather().getHumidity())
                        .windSpeedKph(c.getWeather().getWindSpeed())
                        .windDirection(c.getWeather().getWindDirection())
                        .build())

                .water(CatchResponse.WaterDto.builder()
                        .temperature(c.getWaterCondition().getWaterTemperature())
                        .flow(c.getWaterCondition().getWaterFlow())
                        .height(c.getWaterCondition().getWaterHeight())
                        .build())

                .astronomy(CatchResponse.AstronomyDto.builder()
                        .moonPhase(c.getAstronomy().getMoonPhase())
                        .illumination(c.getAstronomy().getIllumination())
                        .build())
                .build();
    }
}