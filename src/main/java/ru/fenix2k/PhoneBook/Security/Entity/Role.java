package ru.fenix2k.PhoneBook.Security.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

/**
 * Role entity class. Uses for authentication role of Spring Security
 */
@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Role implements GrantedAuthority {

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /** Role name */
    @Column(length = 50, nullable = false, unique = true)
    private String name;
    /** Role name */
    @Column
    private String description;

    /** Get authority (GrantedAuthority interface) */
    @Override
    public String getAuthority() {
        return name;
    }
}
