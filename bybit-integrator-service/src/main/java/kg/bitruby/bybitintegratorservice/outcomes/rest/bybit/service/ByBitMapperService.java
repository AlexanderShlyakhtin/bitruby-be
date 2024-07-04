package kg.bitruby.bybitintegratorservice.outcomes.rest.bybit.service;

import kg.bitruby.bybitintegratorservice.client.bybit.api.model.CreateSubApiRequest;
import kg.bitruby.bybitintegratorservice.client.bybit.api.model.CreateSubMember;
import kg.bitruby.bybitintegratorservice.outcomes.postgres.entity.AccountEntity;
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
