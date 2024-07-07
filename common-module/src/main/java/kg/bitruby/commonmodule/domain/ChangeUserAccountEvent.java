package kg.bitruby.commonmodule.domain;

import kg.bitruby.commonmodule.exceptions.BitrubyRuntimeExpection;

public enum ChangeUserAccountEvent {

  CREDENTIAL_EXPIRED("CREDENTIAL_EXPIRED"),
  LOCK_ACCOUNT("LOCK_ACCOUNT"),
  ACCOUNT_VERIFIED("ACCOUNT_VERIFIED"),
  BYBIT_ACCOUNT_CREATED("BYBIT_ACCOUNT_CREATED"),
  DELETE_ACCOUNT("DELETE_ACCOUNT");

  private final String value;

  ChangeUserAccountEvent(String value) {
    this.value = value;
  }

  public static ChangeUserAccountEvent fromValue(String value) {
    for (ChangeUserAccountEvent b : ChangeUserAccountEvent.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new BitrubyRuntimeExpection("Unexpected value '" + value + "'");
  }

}
