package com.homework.estate_project.dto.estate;

import com.homework.estate_project.entity.enums.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstateDetailsDto {
    private Long id;
    private String owner;
    private List<String> photoNames = new ArrayList<>();
    private String description;
    private Location location;
    private String streetName;
    private int streetNumber;
    private float price;
    private int totalArea;
    private int usableArea;
    private int sharedArea;
    private int floor;
    private EstateType type;
    private EstateFloorType floorType;
    private int buildYear;
    private EstateBuildType buildType;
    private List<EstateFeature> features;
}
