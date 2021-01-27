package ru.fenix2k.PhoneBook.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.fenix2k.PhoneBook.Component.EntityPropertyMap;
import ru.fenix2k.PhoneBook.Entity.Employee;
import ru.fenix2k.PhoneBook.Exception.OperationNotPermitedException;
import ru.fenix2k.PhoneBook.Security.Exceptions.EntityValidationException;
import ru.fenix2k.PhoneBook.Security.Exceptions.UniversalResponse;
import ru.fenix2k.PhoneBook.Service.EmployeeService;
import ru.fenix2k.PhoneBook.Service.ImportProvider;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * Non-rest employee controller
 * Allows access with only ADMIN and WRITER roles
 */
@Controller
@RequestMapping("/employees")
@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_WRITER')")
@Log4j
public class EmployeeController {
  /** Path to template folder and controller base URL */
  private final String baseURL = "/employees";
  /** Path to template file and relative url to list view */
  private final String listTemplate = "/list";
  /** Path to template file and relative url to add and edit view */
  private final String createTemplate = "/addOrEditEmployee";
  /** Path to template file and relative url to create view */
  private final String editTemplate = createTemplate;

  @Autowired
  private EmployeeService employeeService;
  @Autowired
  private ImportProvider importProvider;

  /**
   * Get employees list page
   * @param params type of view ("","local", "ldap")
   * @param principal autowired
   * @param model autowired
   * @return html page
   */
  @GetMapping("")
  public String employeeListPage(@RequestParam Map<String,String> params,
                                 @AuthenticationPrincipal UserDetails principal,
                                 Model model) {
    log.info("Handling employeeListPage method");
    model.addAttribute("pageContent", baseURL + listTemplate);
    model.addAttribute("principal", principal);

    if(params.containsKey("type")) {
      if (params.get("type").equals("local")) {
        model.addAttribute("pageTitle", "Список локальных записей");
        model.addAttribute("type", "local");
        model.addAttribute("entities", this.employeeService.getAllLocalEmployees());
      } else if (params.get("type").equals("ldap")) {
        model.addAttribute("pageTitle", "Список импортированных записей");
        model.addAttribute("type", "ldap");
        model.addAttribute("entities", this.employeeService.getAllExternalEmployees());
      }
    }
    if(!params.containsKey("type")
        || (!params.get("type").equals("local") && !params.get("type").equals("ldap"))) {
      model.addAttribute("pageTitle", "Все записи");
      model.addAttribute("entities", this.employeeService.getAllEmployees());
    }

    if(params.containsKey("message"))
      model.addAttribute("message", params.get("message"));

    return "_layonts/default";
  }

  /**
   * Get create employee page
   * @param principal autowired
   * @param model autowired
   * @return html page
   */
  @GetMapping("/create")
  public String createEmployeePage(@AuthenticationPrincipal UserDetails principal,
                                   Model model) {
    log.info("Handling createEmployeePage method");
    model.addAttribute("pageContent", baseURL + createTemplate);
    model.addAttribute("entity", new Employee());
    model.addAttribute("principal", principal);
    return "_layonts/default";
  }

  /**
   * Get edit employee page
   * @param id employee ID
   * @param principal autowired
   * @param model autowired
   * @return html page
   */
  @GetMapping("/edit/{id}")
  public String editEmployeePage(@PathVariable("id") Long id,
                                 @AuthenticationPrincipal UserDetails principal,
                                 Model model) {
    log.info("Handling editEmployeePage method");
    model.addAttribute("pageContent", baseURL + createTemplate);
    model.addAttribute("principal", principal);

    Employee employee = this.employeeService.getEmployeeById(id);
    if(employee == null)
      log.info("Bad request: attempt to edit no existing employee with ID=" + id);
    model.addAttribute("entity", employee);

    return "_layonts/default";
  }

  /**
   * Delete employee from DB by ID
   * @param id employee ID
   * @return redirect URL
   */
  @GetMapping("/delete/{id}")
  public String deleteEmployeeAction(@PathVariable Long id) {
    log.info("Handling deleteEmployeeAction method");
    boolean check = this.employeeService.deleteEmployeeWithValidate(id);
    if(!check)
      log.info("Bad request: attempt to delete no existing employee with ID=" + id);
    return "redirect:" + baseURL + "?type=local";
  }

  /**
   * Create new employee action
   * @param employee new employee data
   * @param bindingResult validations result
   * @param principal autowired
   * @param model autowired
   * @return redirect URL or create page if has validations errors
   */
  @PostMapping("/create")
  public String createEmployeeAction(@Valid @ModelAttribute("entity") Employee employee,
                                     BindingResult bindingResult,
                                     @AuthenticationPrincipal UserDetails principal,
                                     Model model) {
    log.info("Handling createEmployeeAction method");
    model.addAttribute("pageContent", baseURL + createTemplate);
    model.addAttribute("principal", principal);
    // Check validation errors
    if (bindingResult.hasErrors())
      return "_layonts/default";

    try {
      this.employeeService.createEmployeeWithValidate(employee);
    } catch (EntityValidationException ex) {
      model.addAttribute("errors", ex.getCustomMessage());
      return "_layonts/default";
    }
    if(employee.getExternalId() == null)
      return "redirect:" + baseURL + "?type=local";
    else
      return "redirect:" + baseURL + "?type=ldap";
  }

  /**
   * Update employee action
   * @param employee updated employee data
   * @param bindingResult validations result
   * @param principal autowired
   * @param model autowired
   * @return redirect URL or create page if has validations errors
   */
  @PostMapping("/edit")
  public String updateEmployeeAction(@Valid @ModelAttribute("entity") Employee employee,
                                     BindingResult bindingResult,
                                     @AuthenticationPrincipal UserDetails principal,
                                     Model model) {
    log.info("Handling updateEmployeeAction method");

    if(employee.getId() == 0) {
      log.info("Bad request: attempt to update employee without ID");
      return "redirect:" + baseURL + "/";
    }
    model.addAttribute("pageContent", baseURL + editTemplate);
    model.addAttribute("principal", principal);
    // Check validation errors
    if (bindingResult.hasErrors())
      return "_layonts/default";

    try{
      this.employeeService.updateEmployeeWithValidate(employee);
    } catch (EntityValidationException ex) {
      model.addAttribute("errors", ex.getCustomMessage());
      return "_layonts/default";
    } catch (OperationNotPermitedException ex) {
      model.addAttribute("errors", ex.getMessage());
      return "_layonts/default";
    }

    return "redirect:" + baseURL + "?type=local";
  }

  /**
   * Import new employees, update existed and mark as deleter each other
   * @return List of new imported from ldap server employees
   */
  @GetMapping("ldap/import")
  public String importEmployeesFromLdap() {
    log.info("Handling importEmployeesFromLdap method");
    String message = "";
    List<Employee> users = null;
    try {
      users = importProvider.importFromExternalProvider();
      if(users != null)
        message = "Import from AD successful. Imported or updated records: " + users.size();
    } catch (Exception ex) {
      message = "Import failed. Check ldap server availability";
    }

    return "redirect:" + baseURL + "?type=ldap&message=" + message;
  }

  /**
   * Set employee isVisible or isDisabled field
   * @param params map of id and value of visible or disabled
   * @param request current HttpRequest instance
   * @return http response OK
   */
  @GetMapping({"setVisible", "setDisabled"})
  public String setVisibleOrDisabledEmployeeField(@RequestParam Map<String,String> params,
                                                  HttpServletRequest request) {
    log.info("Handling setVisibleOrDisabledEmployeeField method");

    if(!params.containsKey("id")) {
      log.info("Bad request: attempt to modify employee without ID");
      return "redirect:" + baseURL + "/";
    }

    try {
      long id = Long.parseLong(params.get("id"));
      boolean bool;
      if (params.containsKey("isVisible")) {
        if(params.get("isVisible").equals("true"))
          bool = true;
        else
          bool = false;
        employeeService.setVisibleByEmployeeId(id, bool);
      }
      if (params.containsKey("isDeleted")) {
        if(params.get("isDeleted").equals("true"))
          bool = true;
        else
          bool = false;
        employeeService.setDeletedByEmployeeId(id, bool);
      }
    } catch (NumberFormatException ex) {}

    String referer = request.getHeader(HttpHeaders.REFERER);
    if(referer.length() > 0)
      return "redirect:" + request.getHeader(HttpHeaders.REFERER);
    return "redirect:" + baseURL + "/";
  }

  /**
   * Mass operation to set priority field of many employees
   * @param priorities json string of key-value pairs
   * @param request current HttpRequest instance
   * @return http response OK
   * @throws JsonProcessingException
   */
  @GetMapping("setPriorities")
  @ResponseBody
  public ResponseEntity<UniversalResponse> setPrioritiesEmployeeField(@RequestParam String priorities,
                                                                      HttpServletRequest request)
      throws JsonProcessingException {
    log.info("Handling setPrioritiesEmployeeField method");

    final ObjectMapper objectMapper = new ObjectMapper();
    List<EntityPropertyMap> priorityList = objectMapper.readValue(priorities, new TypeReference<>(){});
    
    priorityList.forEach(item -> {
      this.employeeService.setPriorityByEmployeeId(item.getId(), item.getValue());
    });

    String referer = request.getHeader(HttpHeaders.REFERER);
    if(referer.length() > 0)
      return ResponseEntity.ok(new UniversalResponse(HttpStatus.OK.value(), request.getHeader(HttpHeaders.REFERER)));
    return ResponseEntity.ok(new UniversalResponse(HttpStatus.OK.value(), baseURL + "/"));
  }

}
