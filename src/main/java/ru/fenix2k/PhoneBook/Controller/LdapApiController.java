package ru.fenix2k.PhoneBook.Controller;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.fenix2k.PhoneBook.Entity.Employee;
import ru.fenix2k.PhoneBook.Entity.LdapEmployee;
import ru.fenix2k.PhoneBook.Service.ImportProvider;
import ru.fenix2k.PhoneBook.Service.LdapEmployeeService;

import java.util.List;

/**
 * Ldap operations controller
 * Allows access with only ADMIN and WRITER roles
 */
@RestController
@RequestMapping("/api/v1/ldap")
@Log4j
@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_WRITER')")
public class LdapApiController {

  @Autowired
  private LdapEmployeeService ldapEmployeeService;
  @Autowired
  private ImportProvider importProvider;


  /**
   * Import new employees, update existed and mark as deleter each other
   * @return List of new imported from ldap server employees
   */
  @GetMapping("import")
  public List<Employee> importTest() {
    List<Employee> users = importProvider.importFromExternalProvider();
    return users;
  }

  /**
   * Get from ldap server all employees
   * @return List of employees
   */
  @GetMapping("getall")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public List<LdapEmployee> getADAll()  {
    List<LdapEmployee> users = ldapEmployeeService.getAll();
    return users;
  }

  /**
   * Get from ldap server all enabled employees
   * @return List of employees
   */
  @GetMapping("getallenabled")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public List<LdapEmployee> getADAllEnabled()  {
    List<LdapEmployee> users = ldapEmployeeService.getAllEnabled();
    return users;
  }

  /**
   * Get from ldap server employee with specified username
   * @param username ldap username of employee
   * @return employees
   */
  @GetMapping("getbyusername")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public List<LdapEmployee> getADByUsername(@RequestParam String username)  {
    List<LdapEmployee> users = ldapEmployeeService.getByUsername(username);
    return users;
  }

}
