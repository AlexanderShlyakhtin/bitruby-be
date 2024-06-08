package com.bitruby.usersapp.core.users;

import com.bitruby.usersapp.api.model.RoleType;
import com.bitruby.usersapp.api.model.User;
import com.bitruby.usersapp.outcomes.postgres.entity.UserEntity;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
    componentModel = MappingConstants.ComponentModel.SPRING)
public interface UsersMapper {
  @Mapping(target = "id", source = "userId")
  UserEntity toEntity(User user);

  @Mapping(target = "userId", source = "id")
  User toDto(UserEntity userEntity);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", source = "userId")
  UserEntity partialUpdate(User user, @MappingTarget UserEntity userEntity);

  default String map(RoleType roleType) {
    return roleType.getValue();
  }

  default RoleType map(String role) {
    return RoleType.fromValue(role);
  }

}
