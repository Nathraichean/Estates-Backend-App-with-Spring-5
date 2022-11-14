package com.homework.estate_project.dto.mapping;

import com.homework.estate_project.dto.agency.AgencyCreateDto;
import com.homework.estate_project.dto.agency.AgencyDetailsDto;
import com.homework.estate_project.dto.agency.AgencyUpdateDto;
import com.homework.estate_project.entity.Agency;
import com.homework.estate_project.entity.ImageData;
import com.homework.estate_project.entity.User;
import org.springframework.beans.BeanUtils;

public class AgencyDtoMapper {

    public static Agency mapAgencyCreateDtoToAgency(AgencyCreateDto source) {
        Agency result = new Agency();
        BeanUtils.copyProperties(source, result);
        return result;
    }

    public static Agency mapAgencyUpdateDtoToAgency(AgencyUpdateDto source) {
        Agency result = new Agency();
        BeanUtils.copyProperties(source, result);
        return result;
    }

    public static AgencyDetailsDto mapAgencyToAgencyDetailDto(Agency source) {
        AgencyDetailsDto result = new AgencyDetailsDto();
        BeanUtils.copyProperties(source, result);
        User owner = source.getOwner();
        ImageData avatar = source.getAvatar();
        result.setOwner(owner != null ?
                owner.getFirstName() + ' ' + owner.getLastName() + ", Username: " + owner.getUsername()
                : "No author"
        );
        if (avatar == null){
            result.setAvatarImageName("noimg");
        }
        else result.setAvatarImageName(avatar.getName());
        return result;
    }
}
