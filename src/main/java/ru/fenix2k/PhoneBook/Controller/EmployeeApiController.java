package ru.fenix2k.PhoneBook.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.fenix2k.PhoneBook.Component.EntityPropertyMap;
import ru.fenix2k.PhoneBook.Entity.Employee;
import ru.fenix2k.PhoneBook.Entity.HateoasModel.EmployeeModel;
import ru.fenix2k.PhoneBook.Exception.OperationNotPermitedException;
import ru.fenix2k.PhoneBook.Security.Exceptions.EntityValidationException;
import ru.fenix2k.PhoneBook.Service.EmployeeService;

import javax.xml.bind.ValidationException;
import java.util.List;
import java.util.Map;

/**
 * Rest employee controller
 * Allows access with only ADMIN and WRITER roles
 */
@RestController
@RequestMapping("/api/v1/employees")
@Log4j
@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_WRITER')")
public class EmployeeApiController {
  
  @Autowired
  private EmployeeService employeeService;

  /**
   * Create user method by POST HTTP request
   * @param employee Employee to add to the database
   * @return created Employee instance
   * @throws EntityValidationException
   */
  @PostMapping("/")
  public Employee createEmployee(@RequestBody Employee employee)
      throws EntityValidationException {
    log.info("Handling save employee: " + employee);
    return this.employeeService.createEmployeeWithValidate(employee);
  }

  /**
   * Update employee method by PUT HTTP request
   * @param employee Employee to update to the database
   * @return updated Employee instance
   * @throws EntityValidationException
   */
  @PutMapping("/")
  public Employee updateEmployee(@RequestBody Employee employee)
      throws EntityValidationException, OperationNotPermitedException {
    log.info("Handling update employee: " + employee);
    return this.employeeService.updateEmployeeWithValidate(employee);
  }

  /**
   * Find employee by ID
   * @param id employee ID
   * @param principal current user (autowired)
   * @return Employee instance
   */
  @GetMapping("/{id}")
  public ResponseEntity<RepresentationModel<EmployeeModel>> findEmployeeById(
      @PathVariable Long id,
      @AuthenticationPrincipal UserDetails principal) {
    log.info("Handling find by id request: " + id);
    Employee employee = this.employeeService.getEmployeeById(id);
    EmployeeModel model = new EmployeeModel(employee, principal.getUsername());
    model.add(WebMvcLinkBuilder.linkTo(EmployeeApiController.class).slash("/" + id).withSelfRel());
    return ResponseEntity.ok(model);
  }

  /**
   * Detele employee by ID
   * @param id employee ID
   * @return response status 200
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
    log.info("Handling delete employee request: " + id);
    this.employeeService.deleteEmployeeWithValidate(id);
    return ResponseEntity.ok().build();
  }

  /**
   * Provides for search employees by specified fields
   * @param customerSpec specification based on API filtering library specification-arg-resolver
   * @param pageable Pageable variable
   * @return list of employees
   */
  @GetMapping("")
  public ResponseEntity<RepresentationModel<EmployeeModel>> getEmployees(
      @And({
          @Spec(path = "fullName", spec = Like.class),
          @Spec(path = "email", spec = Like.class),
          @Spec(path = "title", spec = Like.class),
          @Spec(path = "department", spec = Like.class),
          @Spec(path = "company", spec = Like.class),
          @Spec(path = "internalPhoneNumber", spec = Like.class),
          @Spec(path = "cityPhoneNumber", spec = Equal.class),
          @Spec(path = "mobilePhoneNumber", spec = Equal.class),
          @Spec(path = "externalId", spec = Equal.class),
          @Spec(path = "isVisible", params = "visible", defaultVal = "true", spec = Equal.class),
          @Spec(path = "isDeleted", params = "deleted", defaultVal = "false", spec = Equal.class),
      }) Specification<Employee> customerSpec,
      Pageable pageable,
      @AuthenticationPrincipal UserDetails principal) {
    log.info("Handling search request");
    Page<Employee> users = this.employeeService.findEmployeesBySpecPaged(customerSpec, pageable);
    EmployeeModel model = new EmployeeModel(users, principal.getUsername());
    return ResponseEntity.ok(model);
  }

  /**
   * Set employee isVisible or isDisabled field
   * @param params map of id and value of visible or disabled
   * @return http response OK
   * @throws ValidationException
   */
  @GetMapping({"setVisible", "setDisabled"})
  public ResponseEntity<Void> setVisibleOrDisabledEmployeeField(@RequestParam Map<String,String> params)
      throws ValidationException {
    log.info("Handling setVisibleOrDisabledEmployeeField method");

    if(!params.containsKey("id")) {
      log.info("Bad request: attempt to modify employee without ID");
      throw new ValidationException("Attempt to modify employee without ID", HttpStatus.BAD_REQUEST.toString(), new Exception());
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

    return ResponseEntity.ok().build();
  }

  /**
   * Mass operation to set priority field of many employees
   * @param priorities json string of key-value pairs
   * @return http response OK
   * @throws JsonProcessingException
   */
  @GetMapping("setPriorities")
  public ResponseEntity<Void> setPrioritiesEmployeeField(@RequestParam String priorities)
      throws JsonProcessingException {
    log.info("Handling setPrioritiesEmployeeField method");

    final ObjectMapper objectMapper = new ObjectMapper();
    List<EntityPropertyMap> priorityList = objectMapper.readValue(priorities, new TypeReference<>(){});

    priorityList.forEach(item -> {
      this.employeeService.setPriorityByEmployeeId(item.getId(), item.getValue());
    });

    return ResponseEntity.ok().build();
  }
}
