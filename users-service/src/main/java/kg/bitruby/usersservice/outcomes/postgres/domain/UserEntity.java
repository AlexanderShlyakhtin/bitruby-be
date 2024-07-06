package kg.bitruby.usersservice.outcomes.postgres.domain;

import jakarta.persistence.*;
import kg.bitruby.commonmodule.domain.UserEntityBase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users", schema = "users")
public class UserEntity extends UserEntityBase {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "user_id")
  private UUID id;

}
