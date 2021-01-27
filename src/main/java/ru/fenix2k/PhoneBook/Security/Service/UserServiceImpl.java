package ru.fenix2k.PhoneBook.Security.Service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.fenix2k.PhoneBook.Security.Dto.UserDto;
import ru.fenix2k.PhoneBook.Security.Entity.User;
import ru.fenix2k.PhoneBook.Security.Exceptions.EntityValidationException;
import ru.fenix2k.PhoneBook.Security.Repository.UserRepository;
import ru.fenix2k.PhoneBook.Security.Utils.EntityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * User service implementation class
 */
@Service
@AllArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    /** User repository */
    @Autowired
    private final UserRepository userRepository;

    /**
     * Create user in the DB with properties and duplicate validation.
     * @param userDto User to add to the database
     * @return created UserDto instance
     * @throws EntityValidationException
     */
    @Override
    public UserDto createUserWithValidate(UserDto userDto)
            throws EntityValidationException {
        validateUserDto(userDto);
        User savedUser = userRepository.saveAndFlush(UserService.fromUserDtoToUser(userDto));
        return UserService.fromUserToUserDto(savedUser);
    }

    /**
     * Update user with properties and duplicate validation.
     * Updates only not null properties.
     * @param userDto User to update to the database
     * @return updated UserDto instance
     * @throws EntityValidationException
     */
    @Override
    public UserDto updateUserWithValidate(UserDto userDto)
            throws EntityValidationException {
        User dbUser = validateUserDto(userDto);
        EntityUtils.copyNonNullProperties(UserService.fromUserDtoToUser(userDto), dbUser);
        User savedUser = userRepository.saveAndFlush(dbUser);
        return UserService.fromUserToUserDto(savedUser);
    }

    /**
     * Delete user from DB with validation
     * @param id user ID
     */
    @Override
    public boolean deleteUserByIdWithValidate(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if(user == null) return false;
        this.userRepository.deleteById(id);
        return true;
    }

    /**
     * Find all users in the DB and map to the UserDto class
     * @return list of users (UserDto class)
     */
    @Override
    public List<UserDto> findAllUsers() {
        return this.userRepository.findAll()
            .stream()
            .map(UserService::fromUserToUserDto)
            .collect(Collectors.toList());
    }

    /**
     * Extended user search method by all properties
     * @param userSpecification DB query specification
     * @param pageable page variable
     * @return paged list of users (UserDto class)
     */
    @Override
    public Page<UserDto> findUsersBySpecPaged(Specification<User> userSpecification, Pageable pageable) {
        Page<User> result = userRepository.findAll(userSpecification, pageable);
        return result.map(UserService::fromUserToUserDto);
    }

    /**
     * Find user by ID in the DB
     * @param id user id
     * @return UserDto instance
     */
    @Override
    public UserDto findUserById(Long id) throws ResponseStatusException {
        User user = userRepository.findById(id).orElse(null);
        if(user == null)
            return null;
        return UserService.fromUserToUserDto(user);
    }

    /**
     * Validate UserDto properties: login, email, externalId, password
     * @param userDto UserDto instance
     * @throws EntityValidationException
     * @return User from DB if exists
     */
    private User validateUserDto(UserDto userDto) throws EntityValidationException {
        if(userDto == null)
            throw new EntityValidationException("User cannot be NULL");

        User dbUser = null;
        if(userDto.getId() != null && userDto.getId() != 0) {
            Optional<User> optionalUser = userRepository.findById(userDto.getId());
            if(!optionalUser.isEmpty())
                dbUser = optionalUser.get();
        }
        List<String> validationErrors = new ArrayList<>();

        // Login property validation
        if(userDto.getLogin().isEmpty()) // if login empty
            validationErrors.add("Login cannot be empty");
        else if(dbUser == null || !userDto.getLogin().equals(dbUser.getLogin())) { // if it is new user OR if login not changed
            if(this.userRepository.countUserByLogin(userDto.getLogin()) > 0) // if in DB exists user with same login
                validationErrors.add("User with the same login is already exists");
        }
        // Email property validation
        if(userDto.getEmail().isEmpty()) // if email empty
            validationErrors.add("Email cannot be empty");
        else if(dbUser == null
            || !userDto.getEmail().equals(dbUser.getEmail())) { // if it is new user OR if email not changed
            if(this.userRepository.countUserByEmail(userDto.getEmail()) > 0) // if in DB exists user with same email
                validationErrors.add("User with the same email is already exists");
        }
        // ExternalID property validation
        if((userDto.getExternalId() != null // if externalId not NULL and length between 0 and 2
            && (!userDto.getExternalId().isEmpty() && userDto.getExternalId().length() < 2)))
            validationErrors.add("If External ID is present it must consist of at least 2 characters");
        else if(dbUser == null // if new user OR externalId not null and if externalId not changed
            || (userDto.getExternalId() != null // not null
                && !userDto.getExternalId().isEmpty() // and length > 0
                && !userDto.getExternalId().equals(dbUser.getExternalId()))) { // and externalId changed
            if(this.userRepository.countUserByExternalId(userDto.getExternalId()) > 0) // if in DB exists user with same externalId
                validationErrors.add("User with the same ExternalID is already exists");
        }
        // Password property validation
        if((dbUser == null && userDto.getPassword().length() < 5) // if new user password length must more as 5 characters
            || (dbUser != null && !userDto.getPassword().isEmpty() && userDto.getPassword().length() < 5)) { // if existing user password not changed OR must be more as 5 characters
            validationErrors.add("Password must be present. Length must be minimum 5 characters");
        }

        if(validationErrors.size() > 0)
            throw new EntityValidationException(validationErrors);

        return dbUser;
    }

}
