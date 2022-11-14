package com.homework.estate_project.dto.agency;

import com.homework.estate_project.entity.ImageData;
import com.homework.estate_project.entity.User;
import com.homework.estate_project.entity.enums.GenericStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgencyDetailsDto {
    private Long id;
    private String name;
    private String description;
    private String owner;
    private String avatarImageName;
    private GenericStatus status;

}
