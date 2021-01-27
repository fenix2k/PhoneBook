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
import ru.fenix2k.PhoneBook.Security.Dto.UserDto;
import ru.fenix2k.PhoneBook.Security.Exceptions.EntityValidationException;
import ru.fenix2k.PhoneBook.Security.Service.RoleService;
import ru.fenix2k.PhoneBook.Security.Service.UserService;

import javax.validation.Valid;

/**
 * Non-rest User controller
 * Allows only for ROLE_ADMIN
 */
@Controller
@RequestMapping("/admin/users")
@AllArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
@Log4j
public class UserController {
  /** Path to template folder and controller base URL */
  private final String baseURL = "/admin/users";
  /** Path to template file and relative url to list view */
  private final String listTemplate = "/list";
  /** Path to template file and relative url to add and edit view */
  private final String createTemplate = "/addOrEditUser";
  /** Path to template file and relative url to create view */
  private final String editTemplate = createTemplate;

  @Autowired
  private final UserService userService;
  @Autowired
  private final RoleService roleService;

  /**
   * Get users list page
   * @param principal autowired
   * @param model autowired
   * @return html page
   */
  @GetMapping("/")
  public String userListPage(@AuthenticationPrincipal UserDetails principal,
                             Model model) {
    log.info("Handling userListPage method");
    model.addAttribute("pageContent", baseURL + listTemplate);
    model.addAttribute("pageTitle", "Список пользольвателей");
    model.addAttribute("entities", this.userService.findAllUsers());
    model.addAttribute("principal", principal);
    return "_layonts/default";
  }

  /**
   * Get user create page
   * @param principal autowired
   * @param model autowired
   * @return html page
   */
  @GetMapping("/create")
  public String createUserPage(@AuthenticationPrincipal UserDetails principal,
                               Model model) {
    log.info("Handling createUserPage method");
    model.addAttribute("pageContent", baseURL + createTemplate);
    model.addAttribute("pageTitle", "Создание нового пользователя");
    model.addAttribute("entity", new UserDto());
    model.addAttribute("roles", this.roleService.findAllRoles());
    model.addAttribute("principal", principal);
    return "_layonts/default";
  }

  /**
   * Get user update page
   * @param id user ID
   * @param principal autowired
   * @param model autowired
   * @return html page
   */
  @GetMapping("/edit/{id}")
  public String editUserPage(@PathVariable("id") Long id,
                             @AuthenticationPrincipal UserDetails principal,
                             Model model) {
    log.info("Handling editUserPage method");
    model.addAttribute("pageContent", baseURL + createTemplate);
    model.addAttribute("pageTitle", "Редактирование пользольвателя");
    model.addAttribute("roles", this.roleService.findAllRoles());
    model.addAttribute("principal", principal);

    UserDto user = this.userService.findUserById(id);
    if(user == null)
      log.info("Bad request: attempt to edit no existing user with ID=" + id);
    model.addAttribute("entity", user);

    return "_layonts/default";
  }

  /**
   * Delete user action
   * @param id user ID
   * @return redirect to page
   */
  @GetMapping("/delete/{id}")
  public String deleteUserAction(@PathVariable Long id) {
    log.info("Handling deleteUserAction method");
    boolean check = this.userService.deleteUserByIdWithValidate(id);
    if(!check)
      log.info("Bad request: attempt to delete no existing user with ID=" + id);
    return "redirect:" + baseURL + "/";
  }

  /**
   * Create user action
   * @param userDto new UserDto data
   * @param bindingResult validation results
   * @param principal autowired
   * @param model autowired
   * @return redirect to page
   */
  @PostMapping("/create")
  public String createUserAction(@Valid @ModelAttribute("entity") UserDto userDto,
                                 BindingResult bindingResult,
                                 @AuthenticationPrincipal UserDetails principal,
                                 Model model) {
    log.info("Handling createUserAction method");

    model.addAttribute("pageTitle", "Создание нового пользователя");
    model.addAttribute("pageContent", baseURL + createTemplate);
    model.addAttribute("principal", principal);
    model.addAttribute("roles", this.roleService.findAllRoles());
    // Check validation errors
    if (bindingResult.hasErrors())
      return "_layonts/default";

    try {
      this.userService.createUserWithValidate(userDto);
    } catch (EntityValidationException ex) {
      model.addAttribute("errors", ex.getCustomMessage());
      return "_layonts/default";
    }

    return "redirect:" + baseURL + "/";
  }

  /**
   * Update user action
   * @param userDto updated userDto data
   * @param bindingResult validation results
   * @param principal autowired
   * @param model autowired
   * @return redirect to page
   */
  @PostMapping("/edit")
  public String updateUserAction(@Valid @ModelAttribute("entity") UserDto userDto,
                                 BindingResult bindingResult,
                                 @AuthenticationPrincipal UserDetails principal,
                                 Model model) {
    log.info("Handling updateUserAction method");

    if(userDto.getId() == null || userDto.getId() == 0) {
      log.info("Bad request: attempt to update user without ID");
      return "redirect:" + baseURL + "/";
    }

    model.addAttribute("pageTitle", "Редактирование пользователя");
    model.addAttribute("pageContent", baseURL + editTemplate);
    model.addAttribute("principal", principal);
    model.addAttribute("roles", this.roleService.findAllRoles());
    // Check validation errors
    if (bindingResult.hasErrors())
      return "_layonts/default";

    try{
      this.userService.updateUserWithValidate(userDto);
    } catch (EntityValidationException ex) {
      model.addAttribute("errors", ex.getCustomMessage());
      return "_layonts/default";
    }

    return "redirect:" + baseURL + "/";
  }

}
