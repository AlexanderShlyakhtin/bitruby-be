package kg.bitruby.bybitintegrator.core;

import kg.bitruby.bybitintegrator.common.util.StrongPasswordGenerator;
import kg.bitruby.bybitintegrator.outcomes.postgres.entity.AccountEntity;
import kg.bitruby.bybitintegrator.outcomes.postgres.repository.AccountRepository;
import kg.bitruby.bybitintegrator.outcomes.rest.bybit.api.ByBitApiClient;
import kg.bitruby.bybitintegrator.outcomes.rest.bybit.service.ByBitMapperService;
import kg.bitruby.commonmodule.dto.eventDto.CreateSubAccountDto;
import kg.bitruby.usersapp.client.bybit.api.model.CreateSubAccountResult;
import kg.bitruby.usersapp.client.bybit.api.model.CreateSubMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class AccountService {

  private final StrongPasswordGenerator strongPasswordGenerator;
  private final AccountRepository accountRepository;
  private final ByBitMapperService byBitMapperService;
  private final ByBitApiClient byBitApiClient;


  public void handleCreateSubAccountEvent(CreateSubAccountDto event) {
    AccountEntity accountEntity = new AccountEntity();
    accountEntity.setUserId(event.getUserId());
    accountEntity.setUsername("" + OffsetDateTime.now().toInstant().getEpochSecond() );
    accountEntity.setMemberType(CreateSubMember.MemberTypeEnum.NUMBER_1.getValue());
    accountEntity.setSwitchValue(CreateSubMember.SwitchEnum.NUMBER_0.getValue());
    accountEntity.setIsUta(true);
    accountEntity.setPassword(strongPasswordGenerator.generateStrongPassword(30));

    CreateSubAccountResult subAccount =
        byBitApiClient.createSubAccount(byBitMapperService.map(accountEntity));
    if(subAccount.getResult() != null) {
      accountEntity.setBybitUid(subAccount.getResult().getUid());
      accountEntity.setIsActive(true);
    }

    accountRepository.save(accountEntity);
  }
}
