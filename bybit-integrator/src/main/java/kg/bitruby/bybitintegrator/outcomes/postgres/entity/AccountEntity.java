package kg.bitruby.bybitintegrator.outcomes.postgres.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "accounts")
@Setter
@Getter
public class AccountEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "user_id", unique = true, nullable = false)
  private UUID userId;

  @Column(name = "bybit_uid", unique = true, nullable = false)
  private String bybitUid;

  @Column(name = "username", unique = true, nullable = false, length = 16)
  private String username;

  @Column(name = "password", length = 30)
  private String password;

  @Column(name = "memberType", nullable = false)
  private Integer memberType;

  @Column(name = "switch", nullable = false)
  private Integer switchValue;

  @Column(name = "is_Uta", nullable = false)
  private Boolean isUta;

  @Column(name = "is_active", nullable = false)
  private Boolean isActive;
}
