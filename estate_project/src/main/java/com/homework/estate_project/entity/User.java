package com.homework.estate_project.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.homework.estate_project.entity.enums.UserRole;
import com.homework.estate_project.entity.enums.UserStatus;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.validator.constraints.URL;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static javax.persistence.EnumType.STRING;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotNull
    @Size(min=2, max=15, message = "Your first name should be between 2 and 15 characters long.")
    @NonNull
    private String firstName;

    @NotNull
    @Size(min=2, max=15, message = "Your last name should be between 2 and 15 characters long.")
    @NonNull
    private String lastName;

    @NotNull
    @Size(min=5, max=25, message = "Your username should be between 5 and 25 characters long.")
    @Column(unique = true)
    @NonNull
    private String username;

    @NotNull
    @Email(message = "Please enter a valid email (example@do.main).")
    @Column(unique = true)
    @NonNull
    private String email;

    @NotNull
    @NonNull
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Enumerated(STRING)
    private UserRole userRole = UserRole.STANDARD;
    @Enumerated(STRING)
    private UserStatus userStatus = UserStatus.ACTIVE;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    private ImageData avatar;

    @PastOrPresent
    private LocalDateTime created = LocalDateTime.now();

    @PastOrPresent
    private LocalDateTime modified = LocalDateTime.now();

    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER, cascade=CascadeType.ALL)
    private List<Estate> publishedEstates = new ArrayList<>();

//    @OneToOne(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonIgnore
//    @Transient
//    private Agency ownedAgency = null;

    @ManyToOne
    @JoinColumn(name = "agency_id")
    private Agency agency = null;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;

        if (this.getId() == user.getId()){
            return true;
        }
        else return false;
        // return id != null && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public User(@NonNull String firstName, @NonNull String lastName, @NonNull String username,@NonNull String email, @NonNull String password, @NonNull UserRole userRole) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.userRole = userRole;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + userRole));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
