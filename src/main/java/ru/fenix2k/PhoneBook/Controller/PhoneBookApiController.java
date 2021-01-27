package ru.fenix2k.PhoneBook.Controller;

import lombok.extern.log4j.Log4j;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.fenix2k.PhoneBook.Entity.Employee;
import ru.fenix2k.PhoneBook.Service.EmployeeService;

/**
 * PhoneBook REST API controller
 */
@RestController
@RequestMapping("/api/v1/phonebook")
@Log4j
public class PhoneBookApiController {

  @Autowired
  private EmployeeService employeeService;

  /**
   * Get Employee by ID
   * @param id employee id
   * @return employee in hateoas format
   */
  @GetMapping("/{id}")
  public EntityModel<Employee> findUserById(@PathVariable Long id) {
    log.info("Handling find by id request: " + id);
    EntityModel<Employee> userDtoEntityModel = EntityModel.of(this.employeeService.getEmployeeById(id));
    return userDtoEntityModel.add(WebMvcLinkBuilder.linkTo(PhoneBookApiController.class).slash("/" + id).withSelfRel());
  }

  /**
   * Provides for search employees by specified fields
   * @param employeeSpec specification based on API filtering library specification-arg-resolver
   * @param pageable Pageable variable
   * @return list of employees
   */
  @GetMapping("")
  public ResponseEntity<PagedModel<EntityModel<Employee>>> getEmployees(
        @And({
          @Spec(path = "externalId", spec = Like.class),
          @Spec(path = "fullname", spec = Like.class),
          @Spec(path = "email", spec = Like.class),
          @Spec(path = "isVisible", params = "visible", defaultVal = "true", spec = Equal.class),
          @Spec(path = "isDeleted", params = "deleted", defaultVal = "false", spec = Equal.class),
          @Spec(path = "department", spec = Like.class),
      }) Specification<Employee> employeeSpec, Pageable pageable, PagedResourcesAssembler<Employee> assembler) {
    log.info("Handling search request");
    Page<Employee> employees = this.employeeService.findEmployeesBySpecPaged(employeeSpec, pageable);
    return ResponseEntity.ok(assembler.toModel(employees));
  }

}
