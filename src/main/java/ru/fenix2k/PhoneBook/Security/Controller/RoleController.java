package ru.fenix2k.PhoneBook.Security.Controller;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.fenix2k.PhoneBook.Security.Dto.RoleDto;
import ru.fenix2k.PhoneBook.Security.Exceptions.EntityValidationException;
import ru.fenix2k.PhoneBook.Security.Service.RoleService;

import javax.validation.Valid;

/**
 * Non-rest Role controller
 * Allows only for ROLE_ADMIN
 */
@Controller
@RequestMapping("/admin/roles")
@AllArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
@Log4j
public class RoleController {
  /** Path to template folder and controller base URL */
  private final String baseURL = "/admin/roles";
  /** Path to template file and relative url to list view */
  private final String listTemplate = "/list";
  /** Path to template file and relative url to add and edit view */
  private final String createTemplate = "/addOrEditRole";
  /** Path to template file and relative url to create view */
  private final String editTemplate = createTemplate;

  @Autowired
  private final RoleService roleService;

  /**
   * Get roles list page
   * @param principal autowired
   * @param model autowired
   * @return html page
   */
  @GetMapping("/")
  public String roleListPage(@AuthenticationPrincipal UserDetails principal, Model model) {
    log.info("Handling roleListPage method");
    model.addAttribute("pageContent", baseURL + listTemplate);
    model.addAttribute("pageTitle", "Список ролей");
    model.addAttribute("entities", this.roleService.findAllRoles());
    model.addAttribute("principal", principal);
    return "_layonts/default";
  }

  /**
   * Get role create page
   * @param principal autowired
   * @param model autowired
   * @return html page
   */
  @GetMapping("/create")
  public String createRolePage(@AuthenticationPrincipal UserDetails principal, Model model) {
    log.info("Handling createRolePage method");
    model.addAttribute("pageContent", baseURL + createTemplate);
    model.addAttribute("pageTitle", "Создание новой роли");
    model.addAttribute("entity", new RoleDto("ROLE_"));
    model.addAttribute("principal", principal);
    return "_layonts/default";
  }

  /**
   * Get role update page
   * @param id role ID
   * @param principal autowired
   * @param model autowired
   * @return html page
   */
  @GetMapping("/edit/{id}")
  public String editRolePage(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetails principal, Model model) {
    log.info("Handling editRolePage method");
    model.addAttribute("pageContent", baseURL + createTemplate);
    model.addAttribute("pageTitle", "Редактирование роли");
    model.addAttribute("principal", principal);

    RoleDto role = this.roleService.findById(id);
    if(role == null)
      log.info("Bad request: attempt to edit no existing role with ID=" + id);
    model.addAttribute("entity", role);

    return "_layonts/default";
  }

  /**
   * Delete role action
   * @param id role ID
   * @return redirect to page
   */
  @GetMapping("/delete/{id}")
  public String deleteRoleAction(@PathVariable Long id) {
    log.info("Handling deleteRoleAction method");
    boolean check = this.roleService.deleteRoleByIdWithValidate(id);
    if(!check)
      log.info("Bad request: attempt to delete no existing role with ID=" + id);
    return "redirect:" + baseURL + "/";
  }

  /**
   * Create role action
   * @param roleDto new roleDto data
   * @param bindingResult validation results
   * @param principal autowired
   * @param model autowired
   * @return redirect to page
   */
  @PostMapping("/create")
  public String createRoleAction(@Valid @ModelAttribute("entity") RoleDto roleDto,
                                 BindingResult bindingResult,
                                 @AuthenticationPrincipal UserDetails principal,
                                 Model model) {
    log.info("Handling createRoleAction method");

    model.addAttribute("pageTitle", "Создание новой роли");
    model.addAttribute("pageContent", baseURL + createTemplate);
    model.addAttribute("principal", principal);
    // Check validation errors
    if (bindingResult.hasErrors())
      return "_layonts/default";

    try {
      this.roleService.createRoleWithValidate(roleDto);
    } catch (EntityValidationException ex) {
      model.addAttribute("errors", ex.getCustomMessage());
      return "_layonts/default";
    }

    return "redirect:" + baseURL + "/";
  }

  /**
   * Update role action
   * @param roleDto updated role data
   * @param bindingResult validation results
   * @param principal autowired
   * @param model autowired
   * @return redirect to page
   */
  @PostMapping("/edit")
  public String updateRoleAction(@Valid @ModelAttribute("entity") RoleDto roleDto,
                                 BindingResult bindingResult,
                                 @AuthenticationPrincipal UserDetails principal,
                                 Model model) {
    log.info("Handling updateRoleAction method");

    if(roleDto.getId() == null || roleDto.getId() == 0) {
      log.info("Bad request: attempt to update role without ID");
      return "redirect:" + baseURL + "/";
    }

    model.addAttribute("pageTitle", "Редактирование роли");
    model.addAttribute("pageContent", baseURL + editTemplate);
    model.addAttribute("principal", principal);
    // Check validation errors
    if (bindingResult.hasErrors())
      return "_layonts/default";

    try{
      this.roleService.updateRoleWithValidate(roleDto);
    } catch (EntityValidationException ex) {
      model.addAttribute("errors", ex.getCustomMessage());
      return "_layonts/default";
    }

    return "redirect:" + baseURL + "/";
  }

}
