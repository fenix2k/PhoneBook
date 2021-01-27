package ru.fenix2k.PhoneBook.Repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.fenix2k.PhoneBook.Entity.Employee;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Employee repository
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {

  /** Find all employee where externalId is null */
  List<Employee> findAllByExternalIdIsNull();
  /** Find all employee where externalId is not null */
  List<Employee> findAllByExternalIdIsNotNull();
  /** Find all employee by visible or delete attr */
  List<Employee> findAllByIsVisibleEqualsOrIsDeletedEquals(boolean isEnabled, boolean isDeleted, Sort sort);
  /** Find by externalId */
  Optional<Employee> findByExternalId(String externalId);
  /** Update employee and mark as deleted if there was not updated by import */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  @Modifying
  @Query("update Employee e set e.isDeleted = 1, e.updated = :date " +
      "where e.externalId is not null " +
      "and ((e.created < :date and e.updated is null) or (e.updated is not null and e.updated < :date) )")
  void setExternalImportedEmployeeDeletedIfDateExpired(@Param(value = "date") LocalDateTime dateTime);

  /** Update isVisible field by ID */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  @Modifying
  @Query("update Employee e set e.isVisible = :visible where e.id = :id")
  void setVisibleByEmployeeId(@Param(value = "id")Long id, @Param(value = "visible") boolean isVisible);
  /** Update isDeteled field by ID */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  @Modifying
  @Query("update Employee e set e.isDeleted = :deleted where e.id = :id")
  void setDeletedByEmployeeId(@Param(value = "id")Long id, @Param(value = "deleted") boolean isDeleted);
  /** Update displayPriority field by ID */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  @Modifying
  @Query("update Employee e set e.displayPriority = :priority where e.id = :id")
  void setPriorityByEmployeeId(@Param(value = "id")Long id, @Param(value = "priority") long priority);
}
