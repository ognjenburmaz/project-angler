package com.example.demo.repository;

import com.example.demo.model.Catch;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CatchRepository extends JpaRepository<Catch, Long> {

    Optional<Catch> findFirstByUserOrderByIdDesc(User user);

    List<Catch> findAllByUser(User user);
}