package ru.fenix2k.PhoneBook.Security.Dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

/**
 * User DTO class.
 * Uses for send and receive user data to/from client.
 * See User class
 */
@Data
public class UserDto {

    private Long id;
    @Size(min = 2, max = 50, message = "must be between 2 and 50 characters")
    private String login;
    @Email
    @Size(min = 3, max = 50, message = "must be between 3 and 50 characters")
    private String email;
    @Size(max = 100, message = "must be between 2 and 100 characters")
    private String externalId;
    @NotNull(message = "cannot be empty")
    private boolean enabled;
    @Size(max = 50, message = "must be max 50 characters")
    private String password;
    private Set<RoleDto> roles;

    public UserDto() {
    }

    public UserDto(String login, String email, String externalId, boolean enabled, String password) {
        this.login = login;
        this.email = email;
        this.externalId = externalId;
        this.enabled = enabled;
        this.password = password;
    }

    public UserDto(Long id, String login, String email, String externalId, boolean enabled, String password) {
        this.id = id;
        this.login = login;
        this.email = email;
        this.externalId = externalId;
        this.enabled = enabled;
        this.password = password;
    }
}
