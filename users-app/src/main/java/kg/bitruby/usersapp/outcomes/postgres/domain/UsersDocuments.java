package kg.bitruby.usersapp.outcomes.postgres.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "users_documents")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsersDocuments {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @Column(name = "payload", nullable = false)
  private byte[] payload;

  @Column(name = "document_type", nullable = false, length = 255)
  private String documentType;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", insertable = false, updatable = false)
  private UserEntity userId;

}
