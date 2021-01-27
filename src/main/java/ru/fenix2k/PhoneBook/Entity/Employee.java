package ru.fenix2k.PhoneBook.Entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Employee Entity class
 */
@Data
@Entity
@NoArgsConstructor
public class Employee {
  /** ID  */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  /** Fullname  */
  @Column(nullable = false)
  private String fullName;
  /** Email  */
  @Column(length = 100)
  @Email
  private String email;
  /** Title  */
  @Column(length = 100)
  private String title;
  /** Department  */
  @Column(length = 100)
  private String department;
  /** Manager of employee  */
  @Column
  private String manager;
  /** Company  */
  @Column(length = 100)
  private String company;
  /** Address  */
  @Column
  private String address;
  /** Internal phone number  */
  @Column(length = 30)
  private String internalPhoneNumber;
  /** External city phone number  */
  @Column(length = 30)
  private String cityPhoneNumber;
  /** Mobile phone number  */
  @Column(length = 30)
  private String mobilePhoneNumber;
  /** Defines the sort order of records */
  @Column
  private long displayPriority;
  /** Defines display class style of records */
  @Column
  private String displayClassStyle;

  /** External DB identifier. Uses if employee was imported from another system  */
  @Column(unique = true)
  private String externalId = null;
  /** Create date  */
  @Column(insertable = true, updatable = false)
  private LocalDateTime created = LocalDateTime.now(ZoneId.systemDefault());
  /** Update date  */
  @Column(insertable = false, updatable = true)
  private LocalDateTime updated = LocalDateTime.now(ZoneId.systemDefault());
  /** Visible flag  */
  @Column(nullable = false)
  private boolean isVisible = true;
  /** Delete flag  */
  @Column(nullable = false)
  private boolean isDeleted = false;

  /** Constructor  */
  public Employee(String fullName, String email, String title, String department,
                  String company, String internalPhoneNumber, String cityPhoneNumber,
                  String mobilePhoneNumber, String address) {
    this.fullName = fullName;
    this.email = email;
    this.title = title;
    this.department = department;
    this.company = company;
    this.internalPhoneNumber = internalPhoneNumber;
    this.cityPhoneNumber = cityPhoneNumber;
    this.mobilePhoneNumber = mobilePhoneNumber;
    this.address = address;
  }

  /**
   * Additional getter
   * @return isVisible param value
   */
  public boolean getIsVisible() {
    return this.isVisible;
  }

  /**
   * Additional setter
   * @param isVisible new isVisible param value
   */
  public void setIsVisible(boolean isVisible) {
    this.isVisible = isVisible;
  }

}
