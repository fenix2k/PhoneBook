package ru.fenix2k.PhoneBook.Service;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.fenix2k.PhoneBook.Entity.Employee;
import ru.fenix2k.PhoneBook.Exception.OperationNotPermitedException;
import ru.fenix2k.PhoneBook.Repository.EmployeeRepository;
import ru.fenix2k.PhoneBook.Security.Exceptions.EntityValidationException;
import ru.fenix2k.PhoneBook.Security.Utils.EntityUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Employee service implementation
 */
@Service
@Log
@AllArgsConstructor
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

  /** Employee repository */
  @Autowired
  private final EmployeeRepository employeeRepository;

  /**
   * Create new employee with properties and duplicate validation
   * @param employee employee
   * @return created employee
   * @throws EntityValidationException if exists validations errors
   */
  @Override
  public Employee createEmployeeWithValidate(Employee employee)
      throws EntityValidationException {
    this.validateUserDto(employee);
    return employeeRepository.saveAndFlush(employee);
  }

  /**
   * Create a lot of employees without validation
   * @param employees list of employees
   * @return created list of employees
   */
  @Override
  public List<Employee> createEmployees(List<Employee> employees) {
    return employeeRepository.saveAll(employees);
  }

  /**
   * Update employee with properties and duplicate validation.
   * Updates only not null properties.
   * @param employee employee
   * @return updated employee
   * @throws EntityValidationException if exists validations errors
   * @throws OperationNotPermitedException update external entity is not permited
   */
  @Override
  public Employee updateEmployeeWithValidate(Employee employee)
      throws EntityValidationException, OperationNotPermitedException {
    Employee dbEmployee = this.validateUserDto(employee);
    if(dbEmployee.getExternalId() != null)
      throw new OperationNotPermitedException("Update external entity is not permitted");
    EntityUtils.copyNonNullProperties(employee, dbEmployee);
    return employeeRepository.saveAndFlush(dbEmployee);
  }

  /**
   * Delete employee by ID with validation
   * @param id employee ID
   * @return result of delete operation as boolean
   */
  @Override
  public boolean deleteEmployeeWithValidate(long id) {
    Employee employee = employeeRepository.findById(id).orElse(null);
    if(employee == null) return false;
    employeeRepository.deleteById(id);
    return true;
  }

  /**
   * Get employee by ID
   * @param id employee ID
   * @return employee if present or NULL
   */
  @Override
  public Employee getEmployeeById(long id) {
    Employee employee = employeeRepository.findById(id).orElse(null);
    return employee;
  }

  /**
   * Get employee by externalId
   * @param externalId employee externalId
   * @return employee
   */
  @Override
  public Optional<Employee> getEmployeeByExternalId(String externalId) {
    return employeeRepository.findByExternalId(externalId);
  }

  /**
   * Get all employees as pageable list
   * @param pageable autowired
   * @return pageable list of employees or empty list
   */
  @Override
  public Page<Employee> getAllEmployees(Pageable pageable) {
    return employeeRepository.findAll(pageable);
  }

  /**
   * Get all employees
   * @return list of employees or empty list
   */
  @Override
  public List<Employee> getAllEmployees() {
    return employeeRepository.findAll();
  }

  /**
   * Find employees by custom specified query
   * @param employeeSpecification query employee specification
   * @param pageable autowired
   * @return pageable list of employees
   */
  @Override
  public Page<Employee> findEmployeesBySpecPaged(Specification<Employee> employeeSpecification, Pageable pageable) {
    Page<Employee> result = employeeRepository.findAll(employeeSpecification, pageable);
    return result;
  }

  /**
   * Find all employee by visible or delete attr
   * @param isVisible visible employee attr (boolean)
   * @param isDeleted deleted employee attr (boolean)
   * @return list of employees or empty list
   */
  @Override
  public List<Employee> getAllByIsVisibleEqualsOrIsDeletedEquals(boolean isVisible, boolean isDeleted, Sort sort) {
    return employeeRepository.findAllByIsVisibleEqualsOrIsDeletedEquals(isVisible, isDeleted, sort);
  }

  /**
   * Get all employees where externalId is null
   * @return list of employees or empty list
   */
  @Override
  public List<Employee> getAllLocalEmployees() {
    return employeeRepository.findAllByExternalIdIsNull();
  }

  /**
   * Get all employees where externalId is not null
   * @return list of employees or empty list
   */
  @Override
  public List<Employee> getAllExternalEmployees() {
    return employeeRepository.findAllByExternalIdIsNotNull();
  }

  /**
   * Set employee as deleted if (created < dateTime and modified == null) or (modified < dateTime)
   * @param dateTime dateTime
   */
  @Override
  public void setExternalImportedEmployeeDeletedIfDateExpired(LocalDateTime dateTime) {
    employeeRepository.setExternalImportedEmployeeDeletedIfDateExpired(dateTime);
  }

  /**
   * Update isVisible field by ID
   * @param id employee ID
   * @param isVisible new value
   */
  @Override
  public void setVisibleByEmployeeId(Long id, boolean isVisible) {
    employeeRepository.setVisibleByEmployeeId(id, isVisible);
  }

  /**
   * Update isDeteled field by ID
   * @param id employee ID
   * @param isDeleted new value
   */
  @Override
  public void setDeletedByEmployeeId(Long id, boolean isDeleted) {
    employeeRepository.setDeletedByEmployeeId(id, isDeleted);
  }

  /**
   * Update displayPriority field by ID
   * @param id employee ID
   * @param priority new value
   */
  @Override
  public void setPriorityByEmployeeId(Long id, long priority) {
    employeeRepository.setPriorityByEmployeeId(id, priority);
  }

  /**
   * Validate Employee properties: fullname, externalId
   * @param employee Employee instance
   * @throws EntityValidationException
   * @return Employee from DB if exists or null
   */
  private Employee validateUserDto(Employee employee) throws EntityValidationException {
    if(employee == null)
      throw new EntityValidationException("Employee cannot be NULL");

    Employee dbEmployee = null;
    if(employee.getId() != 0) {
      Optional<Employee> optionalEmployee = employeeRepository.findById(employee.getId());
      if(!optionalEmployee.isEmpty())
        dbEmployee = optionalEmployee.get();
    }
    List<String> validationErrors = new ArrayList<>();

    // Fullname property validation
    if(employee.getFullName().isEmpty()) // if login empty
      validationErrors.add("Fullname cannot be empty");
    // ExternalId property validation
    if(employee.getExternalId() != null && !employee.getExternalId().isEmpty() // If externalId not empty
        && (dbEmployee == null  || !employee.getExternalId().equals(dbEmployee.getExternalId()))) { // if it is new user OR if login not changed
      if(this.employeeRepository.findByExternalId(employee.getExternalId()).isPresent()) // if in DB exists user with same login
        validationErrors.add("Employee with the same externalId is already exists");
    }

    if(validationErrors.size() > 0)
      throw new EntityValidationException(validationErrors);

    return dbEmployee;
  }

}
