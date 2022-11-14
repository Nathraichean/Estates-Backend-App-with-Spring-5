package com.homework.estate_project.web;

import com.homework.estate_project.entity.ImageData;
import com.homework.estate_project.exception.NonExistingEntityException;
import com.homework.estate_project.service.ImageDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/image")
public class ImageDataRestController {

    @Autowired
    private ImageDataService imageDataService;

    @PostMapping
    public ResponseEntity<?> uploadImage(@RequestParam(value = "image") MultipartFile file,
                                         @RequestParam(value = "userId", required = false) Long userId,
                                         @RequestParam(value = "agencyId", required = false) Long agencyId,
                                         @RequestParam(value = "estateId", required = false) Long estateId){

        if (file == null){
            return ResponseEntity.badRequest().body("No image was attached.");
        }

        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(imageDataService.uploadImage(file, userId, agencyId, estateId));
        }
        catch (NonExistingEntityException ex){
            return ResponseEntity.badRequest().body("The provided ID for the requested entity does not exist.");
        }
        catch (IOException e ){
            return ResponseEntity.badRequest().body("Could not upload provided image.");
        }
    }

    @GetMapping("/info/{name}")
    public ResponseEntity<?>  getImageInfoByName(@PathVariable("name") String name){
        ImageData image = imageDataService.getInfoByImageByName(name);

        return ResponseEntity.status(HttpStatus.OK)
                .body(image);
    }

    @GetMapping("/{name}")
    public ResponseEntity<?>  getImageByName(@PathVariable("name") String name){
        byte[] image = imageDataService.getImageByName(name);

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("image/png"))
                .body(image);
    }

    @DeleteMapping("/{id}")
    public String deleteUserById(@PathVariable("id") Long id) {
        return imageDataService.deleteImageById(id);
    }


}
