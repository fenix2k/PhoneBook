package ru.fenix2k.PhoneBook.Configuration;

import lombok.Getter;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Singleton class that contains program settings
 */
@Component
@Scope("singleton")
@PropertySource("classpath:application.properties")
@Getter
@Log4j
public final class AppProperties {

  @Autowired
  private Environment env;

  /** Ldap server urls */
  @Value("${spring.ldap.urls:}")
  private String ldapServers;
  /** Ldap server base DN */
  @Value("${spring.ldap.base:}")
  private String ldapSearchBaseDn;
  /** Ldap server username */
  @Value("${spring.ldap.username:}")
  private String ldapUsername;
  /** Ldap server password */
  @Value("${spring.ldap.password:}")
  private String ldapUserPassword;

  /** Relative Ldap base DN. Used by fetch items from ldap server  */
  @Value("${spring.ldap.query.relativeDn:}")
  private String ldapSearchRelativeDn;

  /** Ldap search filter. Used by fetch items from ldap server */
  @Value("${spring.ldap.query.search-filter:(&(objectCategory=user)(objectClass=person)(!(UserAccountControl=66050)))}")
  private String ldapSearchSearchFilter;

  /** Array of employee class properties that must be hidden by output */
  @Value("${phonebook.employee.collumns.hide:}")
  private List<String> hideCollumns;

  /** Sort by field */
  @Value("${phonebook.employee.sort-by:fullName}")
  private String employeeSortBy;
  /** Sort direction */
  @Value("${phonebook.employee.sort-direction:ASC}")
  private String employeeSortDirection;

  /** Write to console params loaded from application.properties */
  @PostConstruct
  public void writeParamsToConsole() throws IllegalAccessException {
    Field[] fields = this.getClass().getDeclaredFields();
    System.out.printf("Loaded properties:\n");
    for (Field field: fields) {
      System.out.printf("  " + field.getName() + " = " + field.get(this) + "\n");
    }
  }

  /**
   * Return properties as Map<key,value>
   * @return Return properties as Map<key,value>
   * @throws IllegalAccessException
   */
  public Map<String, String> getPropertyList() throws IllegalAccessException {
    Map<String, String> properties = new TreeMap<>();
    Field[] fields = this.getClass().getDeclaredFields();
    for (Field field: fields) {
      if(field.getName().equals("env") || field.getName().equals("log"))
        continue;
      properties.put(field.getName(), field.get(this).toString());
    }

    return properties;
  }

  /**
   * Force read properties from application.properties
   */
  public void readProperties() {
    ldapServers = env.getProperty("spring.ldap.urls", String.class, "");
    ldapSearchSearchFilter = env.getProperty("spring.ldap.base", String.class, "");
    ldapUsername = env.getProperty("spring.ldap.username", String.class, "");
    ldapUserPassword = env.getProperty("spring.ldap.password", String.class, "");

    ldapSearchRelativeDn = env.getProperty("spring.ldap.query.relativeDn", String.class, "");
    ldapSearchSearchFilter = env.getProperty("spring.ldap.query.search-filter", String.class, "(&(objectCategory=user)(objectClass=person)(!(UserAccountControl=66050)))");

    hideCollumns = List.of(env.getProperty("phonebook.employee.collumns.hide", String.class, "").split(","));
    employeeSortBy = env.getProperty("phonebook.employee.sort-by", String.class, "fullName");
    employeeSortDirection = env.getProperty("phonebook.employee.sort-direction", String.class, "ASC");
  }

}
