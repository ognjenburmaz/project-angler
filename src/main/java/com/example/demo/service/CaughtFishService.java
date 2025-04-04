package com.example.demo.service;

import com.example.demo.model.CaughtFish;
import com.example.demo.model.User;
import com.example.demo.repository.CaughtFishRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Service
public class CaughtFishService {
    private static final String IMAGE_DIR = "src/main/resources/static/images/";

    private final CaughtFishRepository caughtFishRepository;

    CaughtFishService(CaughtFishRepository caughtFishRepository) {
        this.caughtFishRepository = caughtFishRepository;
    }

    public Optional<CaughtFish> getCaughtFish(Long id) { return caughtFishRepository.findById(id); }

    public List<CaughtFish> getAllByOwnerId(User owner) { return caughtFishRepository.findAllByOwner(owner); }

    public CaughtFish saveCaughtFish(CaughtFish caughtFish, MultipartFile file) throws IOException {
        if (file != null) {
            File dir = new File(IMAGE_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = Path.of(IMAGE_DIR + fileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            caughtFish.setImagePath(fileName);
        }
        return caughtFishRepository.save(caughtFish); }

    public void deleteCaughtFish(Long id) {
        CaughtFish caughtFish = caughtFishRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CaughtFish not found"));
        String imagePath = caughtFish.getImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            Path pathToFile = Paths.get(IMAGE_DIR + imagePath);

            File imageFile = pathToFile.toFile();
            if (imageFile.exists() && imageFile.isFile()) {
                if (imageFile.delete()) {
                    System.out.println("Image deleted successfully: " + imageFile.getAbsolutePath());
                } else {
                    System.out.println("Failed to delete image: " + imageFile.getAbsolutePath());
                }
            }
        }
        caughtFishRepository.deleteById(id); }
}
