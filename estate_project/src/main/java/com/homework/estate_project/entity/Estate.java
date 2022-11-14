package com.homework.estate_project.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.homework.estate_project.entity.enums.*;
import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.EnumType.STRING;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class Estate {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @EqualsAndHashCode.Include
    private Long id;

    @NonNull
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(nullable = false)
    @JsonIgnore
    private User owner;

    @Transient
    private Long ownerId = null;

    @NonNull @NotNull
    private String description;

    @NonNull @NotNull @Enumerated(STRING)
    private Location location;

    @NonNull @NotNull
    private String streetName;

    @Min(0) @Max(1000)
    private int streetNumber;

    @NonNull @NotNull @Min(0) @Max(10000000)
    private float price;

    @NonNull @NotNull @Min(0) @Positive
    private int totalArea;

    @NonNull @NotNull @Min(0) @Positive
    private int usableArea;

    @NonNull @NotNull @Min(0) @PositiveOrZero
    private int sharedArea;

    @NonNull @NotNull @Min(0) @Max(50)
    private int floor;

    @NotNull @NonNull @Enumerated(STRING)
    private EstateType type;

    @NotNull @NonNull @Enumerated(STRING)
    private EstateFloorType floorType;

    @NotNull @NonNull @Enumerated(STRING)
    private GenericStatus status = GenericStatus.PENDING;

    @NotNull @NonNull
    private int buildYear;

    @NotNull @NonNull @Enumerated(STRING)
    private EstateBuildType buildType;

    @PastOrPresent
    private LocalDateTime created = LocalDateTime.now();
    private LocalDate createdDateOnly = LocalDate.now();

    @PastOrPresent
    private LocalDateTime modified;

    @NonNull @NotNull
    private int views = 0;

    //@NonNull @NotNull
    @ManyToMany(fetch = FetchType.EAGER,cascade = CascadeType.DETACH)
    private List<ImageData> photos;

    @Enumerated(STRING) @ElementCollection(targetClass = EstateFeature.class)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<EstateFeature> features = new ArrayList<>();

    public Estate(@NonNull User owner,
                  @NonNull String description,
                  @NonNull Location location,
                  @NonNull String streetName,
                  int streetNumber,
                  @NonNull float price,
                  @NonNull int totalArea,
                  @NonNull int usableArea,
                  @NonNull int sharedArea,
                  @NonNull int floor,
                  @NonNull EstateType type,
                  @NonNull EstateFloorType floorType,
                  @NonNull GenericStatus status,
                  @NonNull int buildYear,
                  @NonNull EstateBuildType buildType,
                  ArrayList<EstateFeature> features) {
        this.owner = owner;
        this.description = description;
        this.location = location;
        this.streetName = streetName;
        this.streetNumber = streetNumber;
        this.price = price;
        this.totalArea = totalArea;
        this.usableArea = usableArea;
        this.sharedArea = sharedArea;
        this.floor = floor;
        this.type = type;
        this.floorType = floorType;
        this.status = status;
        this.buildYear = buildYear;
        this.buildType = buildType;
        this.features = features;
    }
}
