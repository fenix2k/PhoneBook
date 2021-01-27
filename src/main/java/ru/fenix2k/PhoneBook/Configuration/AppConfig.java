package ru.fenix2k.PhoneBook.Configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

/**
 * Configure params to connect to ldap server
 */
@Configuration
@PropertySource("classpath:application.properties")
//@Profile("default")
//@EnableLdapRepositories
public class AppConfig {

  @Autowired
  private Environment env;

  /**
   * Define bean that configure params to connect to ldap server
   */
  @Bean
  public LdapContextSource contextSource() {
    LdapContextSource contextSource = new LdapContextSource();
    contextSource.setUrl(env.getRequiredProperty("spring.ldap.urls"));
    contextSource.setBase(env.getRequiredProperty("spring.ldap.base"));
    contextSource.setUserDn(env.getRequiredProperty("spring.ldap.username"));
    contextSource.setPassword(env.getRequiredProperty("spring.ldap.password"));
    return contextSource;
  }

  /**
   * Define bean of LdapTemplate
   * @return
   */
  @Bean
  public LdapTemplate ldapTemplate() {
    return new LdapTemplate(contextSource());
  }

}
