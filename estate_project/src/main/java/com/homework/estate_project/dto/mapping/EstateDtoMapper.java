package com.homework.estate_project.dto.mapping;

import com.homework.estate_project.dto.estate.EstateCreateDto;
import com.homework.estate_project.dto.estate.EstateDetailsDto;
import com.homework.estate_project.dto.estate.EstateUpdateDto;
import com.homework.estate_project.entity.Estate;
import com.homework.estate_project.entity.ImageData;
import com.homework.estate_project.entity.User;
import org.springframework.beans.BeanUtils;

import java.util.List;

public class EstateDtoMapper {

    public static Estate mapEstateCreateDtoToEstate(EstateCreateDto source) {
        Estate result = new Estate();
        BeanUtils.copyProperties(source, result);
        return result;
    }

    public static Estate mapEstateUpdateDtoToEstate(EstateUpdateDto source) {
        Estate result = new Estate();
        BeanUtils.copyProperties(source, result);
        return result;
    }

    public static EstateDetailsDto mapEstateToEstateDetailDto(Estate source) {
        EstateDetailsDto result = new EstateDetailsDto();
        BeanUtils.copyProperties(source, result);
        User owner = source.getOwner();
        List<ImageData> photos = source.getPhotos();
        result.setOwner(owner != null ?
                owner.getFirstName() + ' ' + owner.getLastName() + ", Username: " + owner.getUsername()
                : "No author"
        );
        List<String> photoNames = result.getPhotoNames();
        for (ImageData photo: photos) {
            photoNames.add(photo.getName());
        }
        result.setPhotoNames(photoNames);
        return result;
    }

}
