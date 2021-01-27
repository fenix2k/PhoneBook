package ru.fenix2k.PhoneBook.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.fenix2k.PhoneBook.Configuration.AppProperties;

import java.util.Map;

/**
 * Settings rest controller
 * Allows access with only ADMIN role
 */
@RestController
@RequestMapping("/api/v1/settings")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class SettingsApiController {

  @Autowired
  private AppProperties appProperties;

  /**
   * Get all application properties
   * @param principal current user
   * @return Map of properties
   */
  @GetMapping("")
  public ResponseEntity<Map<String, String>> getAllProperties(@AuthenticationPrincipal UserDetails principal) {
    try {
      Map<String, String> properties = appProperties.getPropertyList();
      return ResponseEntity.ok(properties);
    } catch (IllegalAccessException ex) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка чтения настроек", ex);
    }
  }

  /**
   * Force read properties from application.properties
   * @param principal current user
   * @return HTTP status OK
   */
  @PutMapping("")
  public ResponseEntity<String> updatePropertiesFromConfFile(@AuthenticationPrincipal UserDetails principal) {
    appProperties.readProperties();
    return ResponseEntity.ok().body("success");
  }

}
