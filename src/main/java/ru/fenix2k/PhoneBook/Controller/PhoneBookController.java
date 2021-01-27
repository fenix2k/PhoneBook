package ru.fenix2k.PhoneBook.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import ru.fenix2k.PhoneBook.Configuration.AppProperties;
import ru.fenix2k.PhoneBook.Service.EmployeeService;

import java.util.List;

/**
 * Controller class witch give away phonebook HTML page. Uses Thymeleaf template framework.
 */
@Controller
@RequestMapping("/")
public class PhoneBookController {

  @Autowired
  private EmployeeService employeeService;
  @Autowired
  private AppProperties appProperties;

  /**
   * Get a complete phonebook HTML page with all employees
   * @param principal logged user
   * @return phonebook HTML page
   */
  @GetMapping("all")
  @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_WRITER')")
  public ModelAndView getPhoneBookPage(@AuthenticationPrincipal UserDetails principal) {
    ModelAndView modelAndView = new ModelAndView("_layonts/default");
    modelAndView.addObject("pageContent", "index");
    modelAndView.addObject("pageTitle", "Телефонный справочник сотрудников (включая невидимых)");
    modelAndView.addObject("employees", this.employeeService.getAllEmployees());
    modelAndView.addObject("hiddenColumns", List.of());
    modelAndView.addObject("principal", principal);
    return modelAndView;
  }

  /**
   * Get a complete phonebook HTML page with all enabled and not deleted employees
   * @param principal logged user
   * @return phonebook HTML page
   */
  @GetMapping("")
  public ModelAndView getPhoneBookPageWhereAllActive(@AuthenticationPrincipal UserDetails principal) {
    ModelAndView modelAndView = new ModelAndView("_layonts/default");
    modelAndView.addObject("pageContent", "index");
    modelAndView.addObject("pageTitle", "Телефонный справочник сотрудников");

    Sort sort = Sort.by(Sort.Direction.fromString(appProperties.getEmployeeSortDirection()), appProperties.getEmployeeSortBy());
    modelAndView.addObject("employees",
        this.employeeService.getAllByIsVisibleEqualsOrIsDeletedEquals(true, false, sort));
    modelAndView.addObject("hiddenColumns", appProperties.getHideCollumns());
    modelAndView.addObject("principal", principal);

    return modelAndView;
  }

  @GetMapping("favicon.ico")
  @ResponseBody
  void returnNoFavicon() {
  }

}
