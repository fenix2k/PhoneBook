package ru.fenix2k.PhoneBook.Security.Controller;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.fenix2k.PhoneBook.Security.Dto.RoleDto;
import ru.fenix2k.PhoneBook.Security.Exceptions.EntityValidationException;
import ru.fenix2k.PhoneBook.Security.Service.RoleService;

import java.util.List;

/**
 * Rest Api Role controller
 * Allows only for ROLE_ADMIN
 */
@RestController
@RequestMapping("/api/v1/roles")
@AllArgsConstructor
@Log
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class RoleApiController {

    /** Role service */
    @Autowired
    private final RoleService roleService;

    /**
     * Create role method by POST HTTP request
     * @param roleDto Role to add to the database
     * @return created RoleDto instance
     * @throws EntityValidationException
     */
    @PostMapping("/save")
    public RoleDto saveRole(@RequestBody RoleDto roleDto) throws EntityValidationException {
        log.info("Handling save roles: " + roleDto);
        return this.roleService.createRoleWithValidate(roleDto);
    }

    /**
     * Update role method by PUT HTTP request
     * @param roleDto Role to update to the database
     * @return updated RoleDto instance
     * @throws EntityValidationException
     */
    @PostMapping("/update")
    public RoleDto updateRole(@RequestBody RoleDto roleDto) throws EntityValidationException {
        log.info("Handling update roles: " + roleDto);
        return this.roleService.updateRoleWithValidate(roleDto);
    }

    /**
     * Find all roles
     * @return RoleDto list
     */
    @GetMapping("")
    public List<RoleDto> findAllRoles() {
        log.info("Handling find all roles request");
        return this.roleService.findAllRoles();
    }

    /**
     * Find role by name
     * @param name role name
     * @return RoleDto instance
     */
    @GetMapping("/findByRoleName")
    public RoleDto findByRoleName(@RequestParam String name) {
        log.info("Handling find by rolename request: " + name);
        return this.roleService.findByRoleName(name);
    }

    /**
     *
     * @param roleName
     * @return
     */
    @GetMapping("/findLikeRoleName")
    public List<RoleDto> findLikeRoleName(@RequestParam String roleName) {
        log.info("Handling find like rolename request: " + roleName);
        return this.roleService.findLikeRoleName(roleName);
    }

    /**
     * Find role by ID
     * @param id role ID
     * @return RoleDto instance
     */
    @GetMapping("/{id}")
    public RoleDto findById(@PathVariable Long id) {
        log.info("Handling find by id request: " + id);
        return this.roleService.findById(id);
    }

    /**
     * Delete role by ID
     * @param id role ID
     * @return response status 200
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        log.info("Handling delete role request: " + id);
        this.roleService.deleteRoleByIdWithValidate(id);
        return ResponseEntity.ok().build();
    }

}
