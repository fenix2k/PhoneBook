package ru.fenix2k.PhoneBook.Security.Controller;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Join;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.fenix2k.PhoneBook.Security.Dto.HateoasModel.UserDtoModel;
import ru.fenix2k.PhoneBook.Security.Dto.UserDto;
import ru.fenix2k.PhoneBook.Security.Entity.User;
import ru.fenix2k.PhoneBook.Security.Exceptions.EntityValidationException;
import ru.fenix2k.PhoneBook.Security.Service.UserService;

/**
 * Rest Api User controller
 * Allows only for ROLE_ADMIN
 */
@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
@Log
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class UserApiController {

    /** User service */
    @Autowired
    private final UserService userService;

    /**
     * Create user method by POST HTTP request
     * @param userDto User to add to the database
     * @return created UserDto instance
     * @throws EntityValidationException
     */
    @PostMapping("/")
    public UserDto createUser(@RequestBody UserDto userDto)
        throws EntityValidationException {
        log.info("Handling save users: " + userDto);
        return this.userService.createUserWithValidate(userDto);
    }

    /**
     * Update user method by PUT HTTP request
     * @param userDto User to update to the database
     * @return updated UserDto instance
     * @throws EntityValidationException
     */
    @PutMapping("/")
    public UserDto updateUser(@RequestBody UserDto userDto)
            throws EntityValidationException {
        log.info("Handling update users: " + userDto);
        return this.userService.updateUserWithValidate(userDto);
    }

    /**
     * Find user by ID
     * @param id user ID
     * @param principal current user (autowired)
     * @return UserDto instance
     */
    @GetMapping("/{id}")
    public ResponseEntity<RepresentationModel<UserDtoModel>> findUserById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails principal) {
        log.info("Handling find by id request: " + id);
        UserDto userDto = this.userService.findUserById(id);
        UserDtoModel model = new UserDtoModel(userDto, principal.getUsername());
        model.add(WebMvcLinkBuilder.linkTo(UserApiController.class).slash("/" + id).withSelfRel());
        return ResponseEntity.ok(model);
    }

    /**
     * Detele user by ID
     * @param id user ID
     * @return response status 200
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("Handling delete user request: " + id);
        this.userService.deleteUserByIdWithValidate(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Provides for search users by specified fields
     * @param customerSpec specification based on API filtering library specification-arg-resolver
     * @param pageable Pageable variable
     * @return list of users
     */
    @GetMapping("")
    public ResponseEntity<RepresentationModel<UserDtoModel>> getUsers(
            @Join(path = "roles", alias = "r")
            @And({
                @Spec(path = "login", spec = Equal.class),
                @Spec(path = "email", spec = Equal.class),
                @Spec(path = "enabled", spec = Equal.class),
                @Spec(path = "r.name", params = "role", spec = Equal.class)
            }) Specification<User> customerSpec,
            Pageable pageable,
            @AuthenticationPrincipal UserDetails principal) {
        log.info("Handling search request");
        Page<UserDto> users = this.userService.findUsersBySpecPaged(customerSpec, pageable);
        UserDtoModel model = new UserDtoModel(users, principal.getUsername());
        return ResponseEntity.ok(model);
    }

}
