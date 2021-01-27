package ru.fenix2k.PhoneBook.Security.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * Entity used for automation DB table creation.
 * Allows user Remember Me option
 */
@Entity
@Table(name = "persistent_logins")
public class PersistentLogins {

  @Id
  @Column(length = 64, nullable = false)
  private String series;
  @Column(length = 64, nullable = false)
  private String username;
  @Column(length = 64, nullable = false)
  private String token;
  @Column(nullable = false)
  private Timestamp last_used;

}
