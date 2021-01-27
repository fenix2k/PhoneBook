package ru.fenix2k.PhoneBook.Component.Formatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;
import ru.fenix2k.PhoneBook.Security.Dto.RoleDto;
import ru.fenix2k.PhoneBook.Security.Service.RoleService;

import java.text.ParseException;
import java.util.Locale;

/**
 * Formatter class. Convert request param with role ID string ro RoleDto object instance
 */
@Component
public class RoleDtoFormatter implements Formatter<RoleDto> {

  @Autowired
  private RoleService roleService;

  @Override
  public RoleDto parse(String s, Locale locale) throws ParseException {
    Long roleId = Long.parseLong(s);
    RoleDto roleDto = this.roleService.findById(roleId);
    return roleDto;
  }

  @Override
  public String print(RoleDto roleDto, Locale locale) {
    return roleDto.getName();
  }

}
