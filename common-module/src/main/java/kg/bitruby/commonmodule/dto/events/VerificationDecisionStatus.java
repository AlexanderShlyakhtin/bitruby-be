package kg.bitruby.commonmodule.dto.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum VerificationDecisionStatus {

  APPROVED("approved"),

  RESUBMISSION_REQUESTED("resubmission_requested"),

  REVIEW("review"),

  DECLINED("declined"),

  EXPIRED("expired"),

  ABANDONED("abandoned");

  private final String value;

  VerificationDecisionStatus(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @JsonCreator
  public static VerificationDecisionStatus fromValue(String value) {
    for (VerificationDecisionStatus b : VerificationDecisionStatus.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}
