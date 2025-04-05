package com.example.demo.repository;

import com.example.demo.model.CaughtFish;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CaughtFishRepository extends JpaRepository<CaughtFish, Long> {
    List<CaughtFish> findAllByOwner(User owner);
    CaughtFish findFirstByOwnerOrderByIdDesc(User owner);
}