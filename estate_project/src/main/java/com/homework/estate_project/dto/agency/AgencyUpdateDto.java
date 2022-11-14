package com.homework.estate_project.dto.agency;

import lombok.NonNull;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public record AgencyUpdateDto (
        @NotNull @Positive @NonNull Long id,
        @NotNull @NonNull String name,
        @NotNull @NonNull String description,
        @NotNull @NonNull Long ownerId,
        Long avatarId
){}
