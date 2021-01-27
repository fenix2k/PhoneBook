package ru.fenix2k.PhoneBook.Security.Service;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.fenix2k.PhoneBook.Security.Configuration.UserDetailsImpl;
import ru.fenix2k.PhoneBook.Security.Repository.UserRepository;

/**
 * Class uses for authenticate users by Spring Security
 */
@Service
@Log
public class UserDetailsServiceImpl implements UserDetailsService {

  @Autowired
  private UserRepository userRepository;

  /**
   * Get UserDetailsImpl class instance by specified username
   * @param s username string
   * @return UserDetailsImpl object includes a User instance
   * @throws UsernameNotFoundException
   */
  @Override
  public UserDetailsImpl loadUserByUsername(String s) throws UsernameNotFoundException {
    return new UserDetailsImpl(userRepository.findByLoginOrEmail(s, s).orElse(null));
  }

}
