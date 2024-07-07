package kg.bitruby.commonmodule.dto.kafkaevents;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import kg.bitruby.commonmodule.exceptions.BitrubyRuntimeExpection;

public enum CommunicationType {

  EMAIL_PASSWORD("email_password"),

  PHONE_PASSWORD("phone_password");

  private String value;

  CommunicationType(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static CommunicationType fromValue(String value) {
    for (CommunicationType b : CommunicationType.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new BitrubyRuntimeExpection("Unexpected value '" + value + "'");
  }
}
