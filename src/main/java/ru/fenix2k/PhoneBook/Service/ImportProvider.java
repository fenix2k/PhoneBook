package ru.fenix2k.PhoneBook.Service;


import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.fenix2k.PhoneBook.Entity.Employee;
import ru.fenix2k.PhoneBook.Entity.LdapEmployee;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Class provide import features from ldap server
 */
@Service
@NoArgsConstructor
public class ImportProvider {

  @Autowired
  private EmployeeService employeeService;
  @Autowired
  private LdapEmployeeService ldapEmployeeService;

  /**
   * Method imported employees from external DB (ldap server).
   * Existing in DB employees thoes were not included in ldap answer will be marked as deleted.
   * The rest will be created or updated.
   * @return list of new created employees
   */
  public List<Employee> importFromExternalProvider() {
    // Set current datetime
    LocalDateTime currentDateTime = LocalDateTime.now();
    // Fetch employes from ldap server
    List<Employee> employeeADList = ldapEmployeeService.getByAppConfigSettings()
        .stream()
        .map(ImportProvider::mapToEmployee)
        .collect(Collectors.toList());

    List<Employee> employeeDBList = employeeService.getAllExternalEmployees();

    // Search existed in DB employees and set them ID for update operation
    employeeADList.parallelStream().forEach((employeeAD) -> {
      Employee person = employeeDBList.parallelStream()
          .filter(o -> o.getExternalId().equals(employeeAD.getExternalId())).findFirst().orElse(null);
      if(person != null)
        employeeAD.setId(person.getId());
    });

    // Set as deleted Employees those were not included into ldap answer
    employeeService.setExternalImportedEmployeeDeletedIfDateExpired(currentDateTime);

    return employeeService.createEmployees(employeeADList);
  }

  /**
   * Static method provide to map LdapEmployee to Employee
   * @param ldapEmployee an instance of LdapEmployee
   * @return as instance of Employee.class
   */
  private static Employee mapToEmployee(LdapEmployee ldapEmployee) {
    Employee employee = new Employee();

    employee.setFullName(ldapEmployee.getFullName());
    employee.setEmail(ldapEmployee.getEmail());
    employee.setTitle(ldapEmployee.getTitle());
    employee.setDepartment(ldapEmployee.getDepartment());
    employee.setCompany(ldapEmployee.getCompany());
    employee.setInternalPhoneNumber(ldapEmployee.getInternalPhoneNumber());
    employee.setCityPhoneNumber(ldapEmployee.getCityPhoneNumber());
    employee.setMobilePhoneNumber(ldapEmployee.getMobilePhoneNumber());
    employee.setAddress(ldapEmployee.getAddress());
    employee.setManager(ldapEmployee.getManager());
    employee.setExternalId(ldapEmployee.getUserId());
    employee.setDisplayClassStyle(ldapEmployee.getPostOfficeBox());
    try {
      long displayPriority = Long.valueOf(ldapEmployee.getPostalCode());
      employee.setDisplayPriority(displayPriority);
    } catch (NumberFormatException ex) {
      employee.setDisplayPriority(0);
    }

    return employee;
  }

}