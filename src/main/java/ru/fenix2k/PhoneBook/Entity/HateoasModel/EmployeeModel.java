package ru.fenix2k.PhoneBook.Entity.HateoasModel;

import org.springframework.data.domain.Page;
import org.springframework.hateoas.RepresentationModel;
import ru.fenix2k.PhoneBook.Entity.Employee;

/**
 * HATEOAS employee representation model class
 */
public class EmployeeModel extends RepresentationModel<EmployeeModel> {

  /** Single Employee */
  public Employee employee;
  /** Paged List of Employees */
  public Page<Employee> employees;
  /** Current username */
  public String currentUser;

  /** Constructor */
  public EmployeeModel(Page<Employee> employees, String currentUser) {
    this.employees = employees;
    this.currentUser = currentUser != null ? currentUser : "";
  }
  /** Constructor */
  public EmployeeModel(Employee employee, String currentUser) {
    this.employee = employee;
    this.currentUser = currentUser != null ? currentUser : "";
  }

}
