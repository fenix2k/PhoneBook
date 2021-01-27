package ru.fenix2k.PhoneBook.Controller;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.fenix2k.PhoneBook.Configuration.AppProperties;

/**
 * Settings controller
 * Allows access with only ADMIN role
 */
@Controller
@RequestMapping("/admin/settings")
@Log4j
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class SettingsController {
  private final String baseURL = "/admin/settings";
  private final String listTemplate = "/list";

  @Autowired
  private AppProperties appProperties;

  /**
   * Get settings page
   * @param model autowired
   * @param principal current user
   * @return html page
   */
  @GetMapping("")
  public String getSettingsPage(Model model, @AuthenticationPrincipal UserDetails principal) {
    log.info("Handling getSettingsPage method");
    model.addAttribute("pageContent", baseURL + listTemplate);
    model.addAttribute("principal", principal);
    try {
      model.addAttribute("properties", appProperties.getPropertyList());
    } catch (IllegalAccessException ex) {
      model.addAttribute("error", "Ошибка чтения настроек");
    }

    return "_layonts/default";
  }

}
