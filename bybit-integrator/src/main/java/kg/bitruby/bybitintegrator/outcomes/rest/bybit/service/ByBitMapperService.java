package kg.bitruby.bybitintegrator.outcomes.rest.bybit.service;

import kg.bitruby.bybitintegrator.outcomes.postgres.entity.AccountEntity;
import kg.bitruby.usersapp.client.bybit.api.model.CreateSubApiRequest;
import kg.bitruby.usersapp.client.bybit.api.model.CreateSubMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ByBitMapperService {

  public CreateSubMember map(AccountEntity accountEntity) {
    CreateSubMember createSubMember = new CreateSubMember();
    createSubMember.setUsername(accountEntity.getUsername());
    createSubMember.setPassword(accountEntity.getPassword());
    createSubMember.setMemberType(
        CreateSubMember.MemberTypeEnum.fromValue(accountEntity.getMemberType()));
    createSubMember.setSwitch(CreateSubMember.SwitchEnum.fromValue(accountEntity.getSwitchValue()));
    createSubMember.setIsUta(accountEntity.getIsUta());
    createSubMember.setNote(accountEntity.getUserId().toString());
    return createSubMember;
  }

  public CreateSubApiRequest map(CreateSubMember createSubMember) {
    return new CreateSubApiRequest();
  }
}
