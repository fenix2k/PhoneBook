package ru.fenix2k.PhoneBook.Security.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.fenix2k.PhoneBook.Security.Dto.UserDto;
import ru.fenix2k.PhoneBook.Security.Entity.User;
import ru.fenix2k.PhoneBook.Security.Exceptions.EntityValidationException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * User service interface
 */
public interface UserService {
    /** Create user  */
    UserDto createUserWithValidate(UserDto userDto) throws EntityValidationException;
    /** Update user  */
    UserDto updateUserWithValidate(UserDto userDto) throws EntityValidationException;
    /** Delete user  */
    boolean deleteUserByIdWithValidate(Long id);
    /** Find all user  */
    List<UserDto> findAllUsers();
    /** Find user by ID  */
    UserDto findUserById(Long id);
    /** Find user by custom specified query  */
    Page<UserDto> findUsersBySpecPaged(Specification<User> userSpecification, Pageable pageable);

    /** Convert from UserDto to User classes */
    static User fromUserDtoToUser(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setLogin(userDto.getLogin());
        user.setEmail(userDto.getEmail());
        user.setEnabled(userDto.isEnabled());

        if(userDto.getExternalId() != null && userDto.getExternalId().length() > 0)
            user.setExternalId(userDto.getExternalId());
        if(userDto.getPassword().length() > 0)
            user.setEncrytedPassword((new BCryptPasswordEncoder()).encode(userDto.getPassword()));
        else
            user.setEncrytedPassword(null);
        if(userDto.getRoles() != null)
            user.setRoles(userDto.getRoles().stream().map(RoleService::fromRoleDtoToRole).collect(Collectors.toSet()));

        return user;
    }

    /** Convert from User to UserDto classes */
    static UserDto fromUserToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setLogin(user.getLogin());
        userDto.setEmail(user.getEmail());
        userDto.setExternalId(user.getExternalId());
        userDto.setEnabled(user.getEnabled());
        if(user.getRoles() != null)
            userDto.setRoles(user.getRoles().stream().map(RoleService::fromRoleToRoleDto).collect(Collectors.toSet()));

        return userDto;
    }
}
