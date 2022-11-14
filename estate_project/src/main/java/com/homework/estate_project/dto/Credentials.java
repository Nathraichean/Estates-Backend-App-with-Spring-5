package com.homework.estate_project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Credentials {
    @Size(min=5, max=25)
    @NonNull
    private String username;
    @NotBlank
    @Pattern(regexp = "^(?=.*\\d)(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{6,20}$",
            message = "The password should contain at least one digit, one uppercase letter, "
                    + "one special symbol and should be between 6 and 20 characters long.")
    private String password;
}
