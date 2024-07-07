package kg.bitruby.commonmodule.domain;

import kg.bitruby.commonmodule.exceptions.BitrubyRuntimeExpection;

public enum EventType {

  CREDENTIAL_EXPIRED("CREDENTIAL_EXPIRED"),
  LOCK_ACCOUNT("LOCK_ACCOUNT"),
  ACCOUNT_VERIFIED("ACCOUNT_VERIFIED"),
  ACCOUNT_NOT_VERIFIED("ACCOUNT_NOT_VERIFIED"),
  BYBIT_ACCOUNT_CREATED("BYBIT_ACCOUNT_CREATED"),
  BYBIT_ACCOUNT_NOT_CREATED("BYBIT_ACCOUNT_NOT_CREATED"),
  DELETE_ACCOUNT("DELETE_ACCOUNT");

  private final String value;

  EventType(String value) {
    this.value = value;
  }

  public static EventType fromValue(String value) {
    for (EventType b : EventType.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new BitrubyRuntimeExpection("Unexpected value '" + value + "'");
  }

}
