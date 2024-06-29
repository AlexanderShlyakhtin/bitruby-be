package kg.bitruby.commonmodule.dto.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum VerificationEventAction {

  STARTED("started"),

  SUBMITTED("submitted"),

  WAITING_COMPLETE("waiting_complete"),

  WAITING_CONTINUED("waiting_continued"),

  FLOW_FINISHED("flow_finished"),

  FLOW_CANCELLED("flow_cancelled");

  private final String value;

  VerificationEventAction(String value) {
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
  public static VerificationEventAction fromValue(String value) {
    for (VerificationEventAction b : VerificationEventAction.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}
