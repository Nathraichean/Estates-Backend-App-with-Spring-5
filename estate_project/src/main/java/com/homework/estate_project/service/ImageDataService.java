package com.homework.estate_project.service;

import com.homework.estate_project.entity.ImageData;
import com.homework.estate_project.exception.NonExistingEntityException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageDataService {

    String uploadImage(MultipartFile file, Long userId, Long agencyId, Long estateId) throws IOException, NonExistingEntityException;

    ImageData getInfoByImageByName(String name);

    byte[] getImageByName(String name);

    String deleteImageById(Long id);

    long getImageCount();
}
