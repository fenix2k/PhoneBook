package ru.fenix2k.PhoneBook.Entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Entity used for import employees from Ldap server
 */
@Data
@NoArgsConstructor
@ToString
public class LdapEmployee {

  /** Ldap attr: userPrincipalName */
  private String userId;

  /** Ldap attr: sAMAccountName */
  private String username;
  /** Ldap attr: mail */
  private String email;
  /** Ldap attr: displayName */
  private String fullName;
  /** Ldap attr: title */
  private String title;
  /** Ldap attr: department */
  private String department;
  /** Ldap attr: company */
  private String company;
  /** Ldap attr: physicalDeliveryOfficeName */
  private String address;
  /** Ldap attr: telephoneNumber */
  private String internalPhoneNumber;
  /** Ldap attr: homeNumber */
  private String cityPhoneNumber;
  /** Ldap attr: mobile */
  private String mobilePhoneNumber;
  /** Ldap attr: description */
  private String description;
  /** Ldap attr: manager */
  private String manager;
  /** Ldap attr: postalCode */
  private String postalCode;
  /** Ldap attr: postOfficeBox */
  private String postOfficeBox;

}
