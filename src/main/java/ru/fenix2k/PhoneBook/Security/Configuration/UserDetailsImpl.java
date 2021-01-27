package ru.fenix2k.PhoneBook.Security.Configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.fenix2k.PhoneBook.Security.Entity.Role;
import ru.fenix2k.PhoneBook.Security.Entity.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * UserDetails implementation. Uses for configure Spring Security
 */
public class UserDetailsImpl implements UserDetails {

  @Autowired
  private User user;

  public UserDetailsImpl(User user) {
    this.user = user;
  }

  public String getEmail() {
    return user.getEmail();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    List<GrantedAuthority> authorities = new ArrayList<>();
    for (Role role : user.getRoles()) {
      authorities.add(new SimpleGrantedAuthority(role.getName()));
    }
    return authorities;
  }

  @Override
  public String getPassword() {
    return user.getEncrytedPassword();
  }

  @Override
  public String getUsername() {
    return user.getLogin();
  }

  @Override
  public boolean isAccountNonExpired() {
    return user.getEnabled();
  }

  @Override
  public boolean isAccountNonLocked() {
    return user.getEnabled();
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return user.getEnabled();
  }

  @Override
  public boolean isEnabled() {
    return user.getEnabled();
  }
}
