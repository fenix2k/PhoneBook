package ru.fenix2k.PhoneBook.Security.Dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Role DTO class.
 * Uses for send and receive role data to/from client.
 * See Role class
 */
@Data
@NoArgsConstructor
public class RoleDto {

    private Long id;
    @Size(min=6, max = 50, message = "Name must be between 5 and 50 character length")
    @NotNull
    @Pattern(regexp = "^ROLE_.*", message = "Role name must start with \"ROLE_\"")
    private String name;
    private String description;

    public RoleDto(@Size(min = 6, max = 50, message = "Name must be between 5 and 50 character length")
                   @NotNull
                   @Pattern(regexp = "^ROLE_.*", message = "Role name must start with \"ROLE_\"") String name) {
        this.name = name;
    }
}
