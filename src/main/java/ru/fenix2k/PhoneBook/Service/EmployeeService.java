package ru.fenix2k.PhoneBook.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.fenix2k.PhoneBook.Entity.Employee;
import ru.fenix2k.PhoneBook.Exception.OperationNotPermitedException;
import ru.fenix2k.PhoneBook.Security.Exceptions.EntityValidationException;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service Employee interface
 */
public interface EmployeeService {
  /** Get employee by ID */
  Employee getEmployeeById(long id);
  /** Get employee by externalId */
  Optional<Employee> getEmployeeByExternalId(String externalId);
  /** Get all employees as Page<> */
  Page<Employee> getAllEmployees(Pageable pageable);
  /** Get all employees */
  List<Employee> getAllEmployees();
  /** Get all employees there externalId is not null */
  List<Employee> getAllExternalEmployees();
  /** Get all employees there externalId is null */
  List<Employee> getAllLocalEmployees();
  /** Get all employee by visible or delete attr */
  List<Employee> getAllByIsVisibleEqualsOrIsDeletedEquals(boolean isVisible, boolean isDeleted, Sort sort);
  /** Get pageable employee list by custom specified query */
  Page<Employee> findEmployeesBySpecPaged(Specification<Employee> employeeSpecification, Pageable pageable);
  /** Create employee */
  Employee createEmployeeWithValidate(Employee employee) throws EntityValidationException;
  /** Create list of employees */
  List<Employee> createEmployees(List<Employee> employees);
  /** Update employee */
  Employee updateEmployeeWithValidate(Employee employee) throws EntityValidationException, OperationNotPermitedException;
  /** Delete employee by ID */
  boolean deleteEmployeeWithValidate(long id);
  /** Update employee and mark as deleted if there was not updated by import */
  void setExternalImportedEmployeeDeletedIfDateExpired(LocalDateTime dateTime);
  /** Update isVisible field by ID */
  void setVisibleByEmployeeId(Long id, boolean isVisible);
  /** Update isDeteled field by ID */
  void setDeletedByEmployeeId(Long id, boolean isDeleted);
  /** Update displayPriority field by ID */
  void setPriorityByEmployeeId(Long id, long priority);

}
