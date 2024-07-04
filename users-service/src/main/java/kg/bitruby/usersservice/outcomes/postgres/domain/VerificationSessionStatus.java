package kg.bitruby.usersservice.outcomes.postgres.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum VerificationSessionStatus {

  WAITING_FOR_START("waiting_for_start"),
  STARTED("started"),

  WAITING_FOR_REVIEW("waiting_for_review"),

  WAITING_FOR_RESUBMISSION("waiting_for_resubmission"),

  SUCCESS("flow_finished_success"),

  REJECTED("flow_finished_rejected"),

  ERROR("flow_finished_error");

  private final String value;

  VerificationSessionStatus(String value) {
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
  public static VerificationSessionStatus fromValue(String value) {
    for (VerificationSessionStatus b : VerificationSessionStatus.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}
