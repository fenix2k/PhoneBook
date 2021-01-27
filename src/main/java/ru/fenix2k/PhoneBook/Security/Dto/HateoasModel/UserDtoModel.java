package ru.fenix2k.PhoneBook.Security.Dto.HateoasModel;

import org.springframework.data.domain.Page;
import org.springframework.hateoas.RepresentationModel;
import ru.fenix2k.PhoneBook.Security.Dto.UserDto;

/**
 * HATEOAS user representation model class
 */
public class UserDtoModel extends RepresentationModel<UserDtoModel> {

  /** Single UserDto */
  public UserDto userDto;
  /** Paged List of UserDto */
  public Page<UserDto> userDtos;
  /** Current username */
  public String currentUser;

  /** Constructor */
  public UserDtoModel(Page<UserDto> userDtos, String currentUser) {
    this.userDtos = userDtos;
    this.currentUser = currentUser != null ? currentUser : "";
  }
  /** Constructor */
  public UserDtoModel(UserDto userDto, String currentUser) {
    this.userDto = userDto;
    this.currentUser = currentUser != null ? currentUser : "";
  }

}
