package kg.bitruby.commonmodule.domain;

import kg.bitruby.commonmodule.exceptions.BitrubyRuntimeExpection;

public enum AccountStatus {

  REGISTRATION_STAGE("REGISTRATION_STAGE"),
  CREDENTIAL_EXPIRED("CREDENTIAL_EXPIRED"),

  ACCOUNT_LOCK("ACCOUNT_LOCK"),
  NOT_VERIFIED("NOT_VERIFIED"),
  ACCOUNT_NOT_CREATED("ACCOUNT_NOT_CREATED"),
  OK("OK");

  private final String value;

  AccountStatus(String value) {
    this.value = value;
  }

  public static AccountStatus fromValue(String value) {
    for (AccountStatus b : AccountStatus.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new BitrubyRuntimeExpection("Unexpected value '" + value + "'");
  }

}
