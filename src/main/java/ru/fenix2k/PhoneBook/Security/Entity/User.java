package ru.fenix2k.PhoneBook.Security.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * User entity class. Uses for authentication user of Spring Security
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class User implements Serializable {

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /** Login */
    @Column(length = 50, nullable = false, unique = true)
    private String login;
    /** Email */
    @Column(length = 50, nullable = false, unique = true)
    private String email;
    /** External ID */
    @Column(length = 100, unique = true)
    private String externalId;
    /** Encryted password */
    @Column(length = 128, nullable = false)
    private String encrytedPassword;
    /** Enabled flag */
    @Column(length = 1, nullable = false)
    private Boolean enabled;
    /** Roles set */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
        joinColumns =
        @JoinColumn(name = "user_id", referencedColumnName = "id"),
        inverseJoinColumns =
        @JoinColumn(name = "privilege_id", referencedColumnName = "id"))
    private Set<Role> roles;

}
