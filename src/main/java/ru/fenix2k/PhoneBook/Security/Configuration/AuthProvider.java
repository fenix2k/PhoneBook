package ru.fenix2k.PhoneBook.Security.Configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.fenix2k.PhoneBook.Security.Service.UserDetailsServiceImpl;

import java.util.Collection;

/**
 * AuthenticationProvider implementation. Uses for configure Spring Security
 */
@Component
public class AuthProvider implements AuthenticationProvider {

  @Autowired
  private UserDetailsServiceImpl userDetailsServiceImpl;

  @Autowired
  private PasswordEncoder passwordEncoder;

  /**
   * Implement authentication logic.
   * Authentication by login or email
   * @param authentication autowired
   * @return Authentication class instance
   * @throws AuthenticationException
   */
  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String login = authentication.getName();
    String password = (String) authentication.getCredentials();

    UserDetailsImpl userDetails = userDetailsServiceImpl.loadUserByUsername(login);

    if(userDetails != null && (userDetails.getUsername().equals(login) || userDetails.getEmail().equals(login)))
    {
      if(!passwordEncoder.matches(password, userDetails.getPassword()))
      {
        throw new BadCredentialsException("Wrong password");
      }

      Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

      return new UsernamePasswordAuthenticationToken(userDetails, password, authorities);
    }
    else
      throw new BadCredentialsException("Username not found");
  }

  @Override
  public boolean supports(Class<?> aClass) {
    return aClass.equals(UsernamePasswordAuthenticationToken.class);
  }
}
