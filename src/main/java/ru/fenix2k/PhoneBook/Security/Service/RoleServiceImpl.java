package ru.fenix2k.PhoneBook.Security.Service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.fenix2k.PhoneBook.Security.Dto.RoleDto;
import ru.fenix2k.PhoneBook.Security.Entity.Role;
import ru.fenix2k.PhoneBook.Security.Entity.User;
import ru.fenix2k.PhoneBook.Security.Exceptions.EntityValidationException;
import ru.fenix2k.PhoneBook.Security.Repository.RoleRepository;
import ru.fenix2k.PhoneBook.Security.Utils.EntityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Role service implementation class
 */
@Service
@AllArgsConstructor
@Transactional
public class RoleServiceImpl implements RoleService {

    /** Role repository */
    @Autowired
    private final RoleRepository roleRepository;

    /**
     * Create role in the DB
     * @param roleDto Role to add to the database
     * @return created RoleDto instance
     * @throws EntityValidationException
     */
    @Override
    public RoleDto createRoleWithValidate(RoleDto roleDto) throws EntityValidationException {
        validateAppRoleDto(roleDto);
        Role savedRole = roleRepository.save(RoleService.fromRoleDtoToRole(roleDto));
        return RoleService.fromRoleToRoleDto(savedRole);
    }

    /**
     * Update role in the DB
     * @param roleDto Role to update to the database
     * @return updated RoleDto instance
     * @throws EntityValidationException
     */
    @Override
    public RoleDto updateRoleWithValidate(RoleDto roleDto) throws EntityValidationException {
        Role dbRole = validateAppRoleDto(roleDto);
        EntityUtils.copyNonNullProperties(RoleService.fromRoleDtoToRole(roleDto), dbRole);
        Role savedRole = roleRepository.save(dbRole);
        return RoleService.fromRoleToRoleDto(savedRole);
    }

    /**
     * Delete role from DB
     * @param id role ID
     */
    @Override
    public boolean deleteRoleByIdWithValidate(Long id) {
        Role role = roleRepository.findById(id).orElse(null);
        if(role == null) return false;
        this.roleRepository.deleteById(id);
        return true;
    }

    /**
     * Find all roles and convert to RoleDto
     * @return role list (RoleDto class)
     */
    @Override
    public List<RoleDto> findAllRoles() {
        return this.roleRepository.findAll()
                .stream()
                .map(RoleService::fromRoleToRoleDto)
                .collect(Collectors.toList());
    }

    /**
     * Find role by name and convert to RoleDto
     * @param roleName role name
     * @return RoleDto instance
     */
    @Override
    public RoleDto findByRoleName(String roleName) {
        Role role = roleRepository.findByName(roleName).orElse(null);
        if(role == null) return null;
        return RoleService.fromRoleToRoleDto(role);
    }

    /**
     * Find role looks like a name and convert to RoleDto
     * @param roleName role name
     * @return RoleDto instance
     */
    @Override
    public List<RoleDto> findLikeRoleName(String roleName) {
        return this.roleRepository.findByNameContaining(roleName)
                .stream()
                .map(RoleService::fromRoleToRoleDto)
                .collect(Collectors.toList());
    }

    /**
     * Find role by ID and convert to RoleDto
     * @param id role ID
     * @return RoleDto instance
     */
    @Override
    public RoleDto findById(Long id) {
        Role role = roleRepository.findById(id).orElse(null);
        if(role == null) return null;
        return RoleService.fromRoleToRoleDto(role);
    }

    /**
     * Validate role
     * @param roleDto RoleDto instance
     * @throws EntityValidationException
     */
    private Role validateAppRoleDto(RoleDto roleDto) throws EntityValidationException {
        if(roleDto == null)
            throw new EntityValidationException("Role cannot be NULL");

        Role dbRole = null;
        if(roleDto.getId() != null && roleDto.getId() != 0) {
            Optional<Role> optionalRole = roleRepository.findById(roleDto.getId());
            if(!optionalRole.isEmpty())
                dbRole = optionalRole.get();
        }
        List<String> validationErrors = new ArrayList<>();
        if(roleDto.getName().isEmpty()) // name is empty
            validationErrors.add("Name cannot be empty");
        else if(dbRole == null || !roleDto.getName().equals(dbRole.getName())) { // if it is new user OR if login not changed
            if(this.roleRepository.countRoleByName(roleDto.getName()) > 0) // if in DB exists user with same login
                validationErrors.add("Role with the same name is already exists");
        }

        return dbRole;
    }

}
