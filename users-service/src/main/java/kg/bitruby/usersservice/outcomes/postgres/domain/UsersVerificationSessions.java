package kg.bitruby.usersservice.outcomes.postgres.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "users_verification_sessions")
@Setter
@Getter
@NoArgsConstructor
public class UsersVerificationSessions {

  @Id
  private UUID id;

  @Column(name = "session_url", nullable = false)
  private String sessionUrl;

  @Column(name = "active", nullable = false)
  private boolean active;

  @Column(name = "status", nullable = false)
  @Enumerated(EnumType.STRING)
  private VerificationSessionStatus status;

  @Column(name = "created", nullable = false)
  private OffsetDateTime created;
  @Column(name = "updated", nullable = false)
  private OffsetDateTime updated;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id", updatable = false)
  private UserEntity userId;
}
