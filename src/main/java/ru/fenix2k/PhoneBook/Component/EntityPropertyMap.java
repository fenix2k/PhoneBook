package ru.fenix2k.PhoneBook.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Needs to map request params from JSON to List */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntityPropertyMap {
  private long id;
  private long value;
}
