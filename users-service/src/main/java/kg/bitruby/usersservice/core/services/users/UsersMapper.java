package kg.bitruby.usersservice.core.services.users;

import kg.bitruby.commonmodule.domain.AccountStatus;
import kg.bitruby.usersservice.api.model.NewUser;
import kg.bitruby.usersservice.api.model.RoleType;
import kg.bitruby.usersservice.api.model.User;
import kg.bitruby.usersservice.outcomes.postgres.domain.UserEntity;
import kg.bitruby.usersservice.outcomes.redis.domain.PreUserRegistration;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
    componentModel = MappingConstants.ComponentModel.SPRING)
public interface UsersMapper {
  @Mapping(target = "id", ignore = true)
  UserEntity toEntity(PreUserRegistration preUserRegistration);

  @Mapping(target = "userId", source = "id")
  User toDto(UserEntity userEntity);


  @Mapping(target = "uuid", expression = "java(generateUUID())")
  @Mapping(target = "phone", source = "user.phone")
  @Mapping(target = "password", source = "password")
  @Mapping(target = "email", source = "user.email")
  @Mapping(target = "enabled", constant = "false")
  @Mapping(target = "emailConfirmed", constant = "false")
  @Mapping(target = "phoneConfirmed", constant = "false")
  @Mapping(target = "accountStatus", expression = "java(setAccountStatus())")
  @Mapping(target = "role", expression = "java(AuthorityRoleEnum.USER)")
  @Mapping(target = "country", ignore = true)
  @Mapping(target = "address", ignore = true)
  @Mapping(target = "firstName", ignore = true)
  @Mapping(target = "lastName", ignore = true)
  PreUserRegistration map(NewUser user, String password);

//  AccountStatus.REGISTRATION_STAGE
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", source = "userId")
  UserEntity partialUpdate(User user, @MappingTarget UserEntity userEntity);

  default String map(RoleType roleType) {
    return roleType.getValue();
  }

  default RoleType map(String role) {
    return RoleType.fromValue(role);
  }

  default String generateUUID() { return UUID.randomUUID().toString(); }
  default AccountStatus setAccountStatus() {
    return AccountStatus.REGISTRATION_STAGE;
  }

}
