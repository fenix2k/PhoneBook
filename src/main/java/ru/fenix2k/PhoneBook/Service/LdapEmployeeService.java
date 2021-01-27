package ru.fenix2k.PhoneBook.Service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.fenix2k.PhoneBook.Configuration.AppProperties;
import ru.fenix2k.PhoneBook.Entity.LdapEmployee;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import java.util.List;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

/**
 * Ldap class service.
 * Allows to execute query to the Ldap server and map them to LdapEmployee class
 */
@Service
@AllArgsConstructor
public class LdapEmployeeService {
  /** Ldap provider */
  @Autowired
  private LdapTemplate ldapTemplate;
  /** Custom properties class */
  @Autowired
  private AppProperties appProperties;

  /**
   * Find all users on ldap server
   * @return list of LdapEmployee
   */
  public List<LdapEmployee> getAll() {
    SearchControls searchControls = new SearchControls();
    searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
    try {
      List<LdapEmployee> people = ldapTemplate.search(
          query().where("objectclass").is("person"),
          new LdapEmployeeAttributeMapper()
      );
      return people;
    }
    catch (Exception ex) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ldap request error", ex);
    }
  }

  /**
   * Find all enabled users on ldap server
   * @return list of LdapEmployee
   */
  public List<LdapEmployee> getAllEnabled() {
    SearchControls searchControls = new SearchControls();
    searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
    try {
      List<LdapEmployee> people = ldapTemplate.search(
          query().where("objectclass").is("person").and("UserAccountControl").not().is("66050"),
          new LdapEmployeeAttributeMapper()
      );
      return people;
    }
    catch (Exception ex) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ldap request error", ex);
    }
  }

  /**
   * Find all users on ldap server accord the AppProperties query configuration
   * @return list of LdapEmployee
   */
  public List<LdapEmployee> getByAppConfigSettings() {
    String baseDn = appProperties.getLdapSearchRelativeDn();
    String ldapFilter = appProperties.getLdapSearchSearchFilter();
    SearchControls searchControls = new SearchControls();
    searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
    try {
      List<LdapEmployee> people = ldapTemplate.search(baseDn, ldapFilter, new LdapEmployeeAttributeMapper());
      return people;
    }
    catch (Exception ex) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ldap request error", ex);
    }
  }

  /**
   * Find user by name on ldap server
   * @param username username string
   * @return list of LdapEmployee
   */
  public List<LdapEmployee> getByUsername(String username) {
    SearchControls searchControls = new SearchControls();
    searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
    try {
      List<LdapEmployee> people = ldapTemplate.search(
          query().where("objectclass").is("person").and("sAMAccountName").is(username),
          new LdapEmployeeAttributeMapper()
      );
      return people;
    }
    catch (Exception ex) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ldap request error", ex);
    }
  }

  /**
   * Execute a custom query to the ldap server
   * @param baseDn relative base DN
   * @param ldapFilter ldap filter
   * @return list of LdapEmployee
   */
  public List<LdapEmployee> getByQuery(String baseDn, String ldapFilter) {
    SearchControls searchControls = new SearchControls();
    searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
    try {
      List<LdapEmployee> people = ldapTemplate.search(baseDn, ldapFilter, new LdapEmployeeAttributeMapper());
      return people;
    }
    catch (Exception ex) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ldap request error", ex);
    }
  }

  /**
   * Support class. Maps ldap response to the LdapEmployee
   */
  private static class LdapEmployeeAttributeMapper implements AttributesMapper<LdapEmployee> {

    @Override
    public LdapEmployee mapFromAttributes(Attributes attributes) throws NamingException {
      LdapEmployee person = new LdapEmployee();

      person.setUserId(null != attributes.get("userPrincipalName") ? attributes.get("userPrincipalName").get().toString() : null);
      person.setFullName(null != attributes.get("displayName") ? attributes.get("displayName").get().toString() : null);
      person.setUsername(null != attributes.get("sAMAccountName") ? attributes.get("sAMAccountName").get().toString() : null);
      person.setEmail(null != attributes.get("mail") ? attributes.get("mail").get().toString() : null);
      person.setTitle(null != attributes.get("title") ? attributes.get("title").get().toString() : null);
      person.setDepartment(null != attributes.get("department") ? attributes.get("department").get().toString() : null);
      person.setCompany(null != attributes.get("company") ? attributes.get("company").get().toString() : null);
      person.setAddress(null != attributes.get("physicalDeliveryOfficeName") ? attributes.get("physicalDeliveryOfficeName").get().toString() : null);
      person.setInternalPhoneNumber(null != attributes.get("telephoneNumber") ? attributes.get("telephoneNumber").get().toString() : null);
      person.setCityPhoneNumber(null != attributes.get("homePhone") ? attributes.get("homePhone").get().toString() : null);
      person.setMobilePhoneNumber(null != attributes.get("mobile") ? attributes.get("mobile").get().toString() : null);
      person.setDescription(null != attributes.get("description") ? attributes.get("description").get().toString() : null);
      person.setManager(null != attributes.get("manager") ? attributes.get("manager").get().toString() : null);
      person.setPostalCode(null != attributes.get("postalCode") ? attributes.get("postalCode").get().toString() : null);
      person.setPostOfficeBox(null != attributes.get("postOfficeBox") ? attributes.get("postOfficeBox").get().toString() : null);

      return person;
    }
  }
}
