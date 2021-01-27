package ru.fenix2k.PhoneBook.Security.Dto.HateoasModel;

import org.springframework.data.domain.Page;
import org.springframework.hateoas.RepresentationModel;
import ru.fenix2k.PhoneBook.Security.Dto.RoleDto;

/**
 * HATEOAS role representation model class
 */
public class RoleDtoModel extends RepresentationModel<RoleDtoModel> {

  /** Single RoleDto */
  public RoleDto roleDto;
  /** Paged List of RoleDto */
  public Page<RoleDto> roleDtos;
  /** Current username */
  public String currentUser;

  /** Constructor */
  public RoleDtoModel(RoleDto roleDto, String currentUser) {
    this.roleDto = roleDto;
    this.currentUser = currentUser == null ? currentUser : "";
  }
  /** Constructor */
  public RoleDtoModel(Page<RoleDto> roleDtos, String currentUser) {
    this.roleDtos = roleDtos;
    this.currentUser = currentUser == null ? currentUser : "";
  }
}
