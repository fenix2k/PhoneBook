package ru.fenix2k.PhoneBook.Security.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.fenix2k.PhoneBook.Security.Entity.Role;

import java.util.List;
import java.util.Optional;

/**
 * Role JPA repository
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    /**
     * Find role by name
     * @param name role name
     * @return role
     */
    Optional<Role> findByName(String name);

    /**
     * Fine role looks like a name
     * @param name role name
     * @return role list (List<Role>)
     */
    List<Role> findByNameContaining(String name);

    /** Get count of roles by name */
    long countRoleByName(String name);
}
