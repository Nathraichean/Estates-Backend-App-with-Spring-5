package com.homework.estate_project.dao;

import com.homework.estate_project.entity.Agency;
import com.homework.estate_project.entity.ImageData;
import com.homework.estate_project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AgencyRepository extends JpaRepository<Agency, Long> {
    Optional<Agency> findByName(String name);
    Agency findAgencyByOwnerEquals(User user);

    Optional<Agency> findDistinctByAvatarEquals(ImageData imageData);
}
