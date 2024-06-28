package kg.bitruby.usersapp.outcomes.rest.bybit.service;

import kg.bitruby.usersapp.client.bybit.api.model.CreateSubApiRequest;
import kg.bitruby.usersapp.client.bybit.api.model.CreateSubMember;
import kg.bitruby.usersapp.outcomes.postgres.domain.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ByBitMapperService {

  public CreateSubMember map(UserEntity userEntity) {
    CreateSubMember createSubMember = new CreateSubMember();
    createSubMember.setUsername(userEntity.getId().toString().replace("-", ""));
    return createSubMember;
  }

  public CreateSubApiRequest map(CreateSubMember createSubMember) {
    return new CreateSubApiRequest();
  }
}
