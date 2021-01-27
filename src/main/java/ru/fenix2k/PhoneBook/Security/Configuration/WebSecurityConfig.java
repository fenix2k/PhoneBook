package ru.fenix2k.PhoneBook.Security.Configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

/**
 * WebSecurityConfigurerAdapter implementation. Uses for configure Spring Security
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private AuthProvider authProvider;
  @Autowired
  private DataSource dataSource;

  /** Define password encoder */
  @Bean
  PasswordEncoder passwordEncoder()
  {
    return new BCryptPasswordEncoder();
  }

  /** Define custom authentication provider */
  @Override
  protected void configure(AuthenticationManagerBuilder auth) {
    auth.authenticationProvider(authProvider);
  }

  /** Configure http path security */
  @Override
  protected void configure(HttpSecurity http) throws Exception
  {
    http
        .csrf().disable()
        .authorizeRequests()

        .antMatchers(
            "/",
            "**/favicon.ico",
            "/login**",
            "/error/**",
            "/webjars/**",
            "/img/**",
            "/css/**",
            "/js/**").permitAll();
        //.antMatchers("/employees/**").access("hasRole('WRITER')")
        //.antMatchers("/**").access("hasRole('ADMIN')")
        //.and().exceptionHandling().accessDeniedPage("/error/403");
    http
        .authorizeRequests()
        .anyRequest()
        .authenticated();
    http
        .authorizeRequests()
        .and().formLogin()
        //.loginProcessingUrl("/j_spring_security_check")
        //.loginPage("/login")
        .defaultSuccessUrl("/")
        //.failureUrl("/login?error=true")
        .usernameParameter("username").passwordParameter("password")
        .and().logout().logoutUrl("/logout").logoutSuccessUrl("/");

    // Config Remember Me.
    http.authorizeRequests().and()
        .rememberMe()
        .tokenRepository(this.persistentTokenRepository())
        .tokenValiditySeconds(1 * 24 * 60 * 60); // 24h
  }

  /** Configure Remember Me feature */
  @Bean
  public PersistentTokenRepository persistentTokenRepository() {
    JdbcTokenRepositoryImpl db = new JdbcTokenRepositoryImpl();
    db.setDataSource(dataSource);
    return db;
  }
}
