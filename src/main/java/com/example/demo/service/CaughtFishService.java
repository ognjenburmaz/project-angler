package com.example.demo.service;

import com.example.demo.model.CaughtFish;
import com.example.demo.model.User;
import com.example.demo.repository.CaughtFishRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CaughtFishService {
    private final CaughtFishRepository caughtFishRepository;

    CaughtFishService(CaughtFishRepository caughtFishRepository) {
        this.caughtFishRepository = caughtFishRepository;
    }

    public Optional<CaughtFish> getCaughtFish(Long id) { return caughtFishRepository.findById(id); }

    public List<CaughtFish> getAllByOwnerId(User owner) { return caughtFishRepository.findAllByOwner(owner); }

    public CaughtFish saveCaughtFish(CaughtFish caughtFish) { return caughtFishRepository.save(caughtFish); }

    public void deleteCaughtFish(Long id) { caughtFishRepository.deleteById(id); }
}
