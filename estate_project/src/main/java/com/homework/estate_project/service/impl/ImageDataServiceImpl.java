package com.homework.estate_project.service.impl;

import com.homework.estate_project.dao.AgencyRepository;
import com.homework.estate_project.dao.EstateRepository;
import com.homework.estate_project.dao.ImageDataRepository;
import com.homework.estate_project.dao.UserRepository;
import com.homework.estate_project.entity.Agency;
import com.homework.estate_project.entity.ImageData;
import com.homework.estate_project.entity.User;
import com.homework.estate_project.exception.DeleteConflictException;
import com.homework.estate_project.exception.NonExistingEntityException;
import com.homework.estate_project.exception.UnautorizedException;
import com.homework.estate_project.service.ImageDataService;
import com.homework.estate_project.utils.ImageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.transaction.TransactionDefinition.ISOLATION_REPEATABLE_READ;

@Service
@Slf4j
public class ImageDataServiceImpl implements ImageDataService {

    private int uniqueImageCounter = 1;
    private ImageDataRepository imageDataRepository;
    private UserRepository userRepo;
    private AgencyRepository agencyRepo;
    private EstateRepository estateRepo;
    private final PlatformTransactionManager transactionManager;

    @Autowired
    public ImageDataServiceImpl(ImageDataRepository imageDataRepository,
                                UserRepository userRepo,
                                AgencyRepository agencyRepo,
                                EstateRepository estateRepo,
                                PlatformTransactionManager transactionManager){
        this.imageDataRepository = imageDataRepository;
        this.userRepo = userRepo;
        this.agencyRepo = agencyRepo;
        this.estateRepo = estateRepo;
        this.transactionManager = transactionManager;
    }

    @Override
    public String uploadImage(MultipartFile file, Long userId, Long agencyId, Long estateId) throws IOException,NonExistingEntityException {

        String modifiedFileName = file.getOriginalFilename();

        if (modifiedFileName!=null && modifiedFileName.contains(".")){
            modifiedFileName = modifiedFileName.split("\\.")[0];
        }

        // Logic for user avatar upload
        if (userId != null && agencyId == null && estateId == null){

            if (userRepo.findById(userId).isPresent()){
                User user = userRepo.findById(userId).get();

                // Check if provided user already has image and remove it from the database.
                if (user.getAvatar() != null){
                    ImageData currentAvatar = getInfoByImageByName(user.getAvatar().getName());
                    user.setAvatar(null);
                    userRepo.save(user);
                    deleteImageById(currentAvatar.getId());
                }

                modifiedFileName = getUniqueImageName(modifiedFileName);

                // Set provided image as avatar
                user.setAvatar(imageDataRepository.save(ImageData.builder()
                        .name(modifiedFileName)
                        .type(file.getContentType())
                        .imageData(ImageUtil.compressImage(file.getBytes())).build()));
                userRepo.save(user);

                return (String.format(
                        "Avatar with name \"%s\" and id: \"%d\" set successfully to user with ID: %d"
                        ,modifiedFileName,imageDataRepository.findByName(modifiedFileName).get().getId(), userId));

            }
            else {
                throw new NonExistingEntityException("No existing user with ID:"+userId);
            }
        }
        // Logic for agency avatar upload
        else if (userId == null && agencyId != null && estateId == null) {

            // Checks if agency with provided id exists
            if (agencyRepo.findById(agencyId).isPresent()){
                // Saves the agency
                Agency agency = agencyRepo.findById(agencyId).get();

                // Check if provided agency already has image and remove it from the database.
                if (agency.getAvatar() != null){
                    ImageData currentAvatar = getInfoByImageByName(agency.getAvatar().getName());
                    agency.setAvatar(null);
                    agencyRepo.save(agency);
                    deleteImageById(currentAvatar.getId());
                }

                modifiedFileName = getUniqueImageName(modifiedFileName);

                // Set provided image as avatar
                agency.setAvatar(imageDataRepository.save(ImageData.builder()
                        .name(modifiedFileName)
                        .type(file.getContentType())
                        .imageData(ImageUtil.compressImage(file.getBytes())).build()));
                agencyRepo.save(agency);

                return (String.format(
                        "Avatar with name \"%s\" and id: \"%d\" set successfully to agency with ID: %d"
                        ,modifiedFileName,imageDataRepository.findByName(modifiedFileName).get().getId(), agencyId));
            }
            else {throw new NonExistingEntityException("No existing agency with ID:"+agencyId);}
        }
        // Logic for estate image upload.
        else if (userId == null && agencyId == null && estateId != null){


        }
        // Ambiguous to the client values for uploading images not linked to an entity through DataInit.
        else if (userId == -10 && agencyId == -15 && estateId == -30){
            imageDataRepository.save(ImageData.builder()
                    .name(modifiedFileName)
                    .type(file.getContentType())
                    .imageData(ImageUtil.compressImage(file.getBytes())).build());
        }
        else {throw new UnautorizedException("You can't post an image to multiple types of entities simultaneously.");}

        return ("Image uploaded successfully: " +
                modifiedFileName);

    }

    @Override
    public String deleteImageById(Long id) throws NonExistingEntityException {
        if (id == 1){
            throw new DeleteConflictException("Deleting image with 1 is forbidden.");
        }
        else if (imageDataRepository.findById(id).isPresent()){

            // Checks if there is a user with the image being deleted and sets their avatar to the default "noimg" one if true
            if (userRepo.findDistinctByAvatarEquals(getInfoByImageByName(imageDataRepository.findById(id).get().getName())).isPresent()){
                User user = userRepo.findDistinctByAvatarEquals(getInfoByImageByName(imageDataRepository.findById(id).get().getName())).get();
                user.setAvatar(imageDataRepository.findById(1L).get());
                userRepo.save(user);
            }
            // Checks if there is an agency with the image being deleted and sets its avatar to the default "noimg" one if true
            if (agencyRepo.findDistinctByAvatarEquals(getInfoByImageByName(imageDataRepository.findById(id).get().getName())).isPresent()){
                Agency agency = agencyRepo.findDistinctByAvatarEquals(getInfoByImageByName(imageDataRepository.findById(id).get().getName())).get();
                agency.setAvatar(imageDataRepository.findById(1L).get());
                agencyRepo.save(agency);
            }

            String imageName = imageDataRepository.findById(id).get().getName();
            imageDataRepository.deleteById(id);
            return String.format("Image with ID: %s has been deleted successfully. Name of the image: %s", id, imageName);
        }
        else {
            throw new NonExistingEntityException(String.format("No image could be found in the database with ID: %d",id));
        }
    }

    @Override
    @Transactional
    public ImageData getInfoByImageByName(String name) throws NonExistingEntityException {

        Optional<ImageData> dbImage = imageDataRepository.findByName(name);

        return ImageData.builder()
                .id(dbImage.get().getId())
                .name(dbImage.get().getName())
                .type(dbImage.get().getType())
                .imageData(ImageUtil.decompressImage(dbImage.get().getImageData())).build();

    }

    @Override
    @Transactional
    public byte[] getImageByName(String name) throws NonExistingEntityException{
        Optional<ImageData> dbImage = imageDataRepository.findByName(name);
        return ImageUtil.decompressImage(dbImage.get().getImageData());
    }

    @Override
    public long getImageCount(){
        return imageDataRepository.count();
    }

    // Checks if image with same name is present and adds unique identifier to name if yes
    private String getUniqueImageName(String string){
        if (imageDataRepository.findByName(string).isPresent()){
            return string + "(" + uniqueImageCounter++ + ")";
        }
        else return string;
    }

}
