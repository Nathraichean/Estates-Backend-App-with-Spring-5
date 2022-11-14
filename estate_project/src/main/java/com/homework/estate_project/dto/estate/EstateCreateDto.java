package com.homework.estate_project.dto.estate;

import com.homework.estate_project.entity.enums.*;
import lombok.NonNull;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

public record EstateCreateDto (
        @NotNull @NonNull String description,
        @NotNull @NonNull Location location,
        @NotNull @NonNull String streetName,
        @NotNull @NonNull @Min(0) @Max(1000) int streetNumber,
        @NotNull @NonNull @Min(0) @Max(10000000) float price,
        @NotNull @NonNull @Min(0) int totalArea,
        @NotNull @NonNull @Min(0) int usableArea,
        @NotNull @NonNull @Min(0) int sharedArea,
        @NotNull @NonNull @Min(0) @Max(50) int floor,
        @NotNull @NonNull EstateType type,
        @NotNull @NonNull EstateFloorType floorType,
        @NotNull @NonNull int buildYear,
        @NotNull @NonNull EstateBuildType buildType,
        List<EstateFeature> features
){}
