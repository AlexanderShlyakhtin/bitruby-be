package com.bitruby.authserver.entity;

import lombok.Getter;

@Getter
public enum AuthorityRoleEnum {

  USER("USER"),

  ADMIN("ADMIN");

  private final String value;

  AuthorityRoleEnum(String value) {
    this.value = value;
  }

  public static AuthorityRoleEnum fromValue(String value) {
    for (AuthorityRoleEnum b : AuthorityRoleEnum.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}
