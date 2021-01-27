package ru.fenix2k.PhoneBook.Security.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.fenix2k.PhoneBook.Security.Entity.User;


import java.util.Optional;

/**
 * User JPA repository
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    /**
     * Find user by login or email. Uses for Spring Security Auth
     * @param login user login
     * @param email user email
     * @return User list (Optional<User>)
     */
    Optional<User> findByLoginOrEmail(String login, String email);

    /** Get count of users by login */
    long countUserByLogin(String login);
    /** Get count of users by email */
    long countUserByEmail(String email);
    /** Get count of users by externalId */
    long countUserByExternalId(String externalId);
}
