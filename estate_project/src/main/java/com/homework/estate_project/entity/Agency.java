package com.homework.estate_project.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.homework.estate_project.entity.enums.GenericStatus;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.validator.constraints.UniqueElements;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static javax.persistence.EnumType.STRING;


@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class Agency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    //@NotNull
    //@NonNull
    @OneToOne(fetch = FetchType.EAGER,cascade=CascadeType.DETACH)
    @JoinColumn(name = "avatar_id")
    private ImageData avatar;

    @NonNull
    @OneToOne(fetch = FetchType.EAGER, cascade=CascadeType.DETACH)
    @ToString.Exclude
    @JoinColumn(nullable = false, referencedColumnName = "id")
    private User owner;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Transient
    private Long ownerId;

    @NotNull
    @NonNull
    private String name;

    @NotNull
    @NonNull
    private String description;

    @Enumerated(STRING)
    private GenericStatus status = GenericStatus.PENDING;

    @PastOrPresent
    private LocalDateTime created = LocalDateTime.now();

    @PastOrPresent
    private LocalDateTime modified = LocalDateTime.now();

    @ToString.Exclude
    @OneToMany(mappedBy = "agency")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<User> usersCollection = new ArrayList<>();

    @ToString.Exclude
    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    private Collection<Long> estatesIdCollection = new ArrayList<>();

    public Agency(User owner, String name, String description, GenericStatus status) {
        this.owner = owner;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Agency agency = (Agency) o;
        if (this.getId() == agency.getId()){return true;}
        else return false;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
