package ru.fenix2k.PhoneBook.Security.Service;

import ru.fenix2k.PhoneBook.Security.Dto.RoleDto;
import ru.fenix2k.PhoneBook.Security.Entity.Role;
import ru.fenix2k.PhoneBook.Security.Exceptions.EntityValidationException;

import java.util.List;

public interface RoleService {

    /** Create role */
    RoleDto createRoleWithValidate(RoleDto roleDto) throws EntityValidationException;
    /** Update role */
    RoleDto updateRoleWithValidate(RoleDto roleDto) throws EntityValidationException;
    /** Delete role */
    boolean deleteRoleByIdWithValidate(Long id);
    /** Find all roles */
    List<RoleDto> findAllRoles();
    /** Find role by name */
    RoleDto findByRoleName(String roleName);
    /** Find role looks like a name */
    List<RoleDto> findLikeRoleName(String roleName);
    /** Find role by ID */
    RoleDto findById(Long id);

    /** Convert from RoleDto to Role classes */
    static Role fromRoleDtoToRole(RoleDto roleDto) {
        Role role = new Role();
        role.setId(roleDto.getId());
        role.setName(roleDto.getName());
        role.setDescription(roleDto.getDescription());
        return role;
    }

    /** Convert from Role to RoleDto classes */
    static RoleDto fromRoleToRoleDto(Role role) {
        RoleDto roleDto = new RoleDto();
        roleDto.setId(role.getId());
        roleDto.setName(role.getName());
        roleDto.setDescription(role.getDescription());
        return roleDto;
    }
}
