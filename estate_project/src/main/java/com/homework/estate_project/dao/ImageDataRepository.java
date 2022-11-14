package com.homework.estate_project.dao;

import com.homework.estate_project.entity.ImageData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageDataRepository extends JpaRepository<ImageData, Long> {
    Optional<ImageData> findByName(String name);
}
