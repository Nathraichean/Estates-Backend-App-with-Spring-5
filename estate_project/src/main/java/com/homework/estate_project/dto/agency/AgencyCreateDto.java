package com.homework.estate_project.dto.agency;

import com.homework.estate_project.entity.enums.*;
import lombok.NonNull;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;



public record AgencyCreateDto (
    @NotNull @NonNull String name,
    @NotNull @NonNull String description
){}
