package ru.fenix2k.PhoneBook.Configuration;

import net.kaczmarzyk.spring.data.jpa.web.SpecificationArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * WebMvcConfigurer implementation class
 */
@Configuration
public class AppWebMvcConfigurer implements WebMvcConfigurer {

  /**
   * Needs for work Specification API data filtering net.kaczmarzyk.specification-arg-resolver
   * @param argumentResolvers
   */
  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
    argumentResolvers.add(new SpecificationArgumentResolver());
  }

}
