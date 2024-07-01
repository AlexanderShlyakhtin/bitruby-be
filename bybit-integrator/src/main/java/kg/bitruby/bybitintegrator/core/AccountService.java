package kg.bitruby.bybitintegrator.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import kg.bitruby.bybitintegrator.api.model.AccountApiKey;
import kg.bitruby.bybitintegrator.common.util.StrongPasswordGenerator;
import kg.bitruby.bybitintegrator.outcomes.postgres.entity.AccountEntity;
import kg.bitruby.bybitintegrator.outcomes.postgres.repository.AccountRepository;
import kg.bitruby.bybitintegrator.outcomes.redis.domain.AccountApiKeyEntity;
import kg.bitruby.bybitintegrator.outcomes.redis.repository.AccountApiKeyRepository;
import kg.bitruby.bybitintegrator.outcomes.rest.bybit.api.ByBitApiClient;
import kg.bitruby.bybitintegrator.outcomes.rest.bybit.service.ByBitMapperService;
import kg.bitruby.commonmodule.dto.events.CreateSubAccountDto;
import kg.bitruby.commonmodule.exceptions.BitrubyRuntimeExpection;
import kg.bitruby.usersapp.client.bybit.api.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

  private final StrongPasswordGenerator strongPasswordGenerator;
  private final AccountRepository accountRepository;
  private final ByBitMapperService byBitMapperService;
  private final ByBitApiClient byBitApiClient;
  private final AccountApiKeyRepository accountApiKeyRepository;
  private final ObjectMapper objectMapper;


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

  public AccountApiKey getApiKeySubAccount(UUID id) {
    Optional<AccountApiKeyEntity> keyEntity = accountApiKeyRepository.findById(id);
    if(keyEntity.isPresent()) {
      return convertObject(keyEntity.get(), AccountApiKey.class);
    } else {
      return createSubAccountApiKey(id);
    }
  }

  private AccountApiKey createSubAccountApiKey(UUID id) {
    return accountRepository.findByUserIdAndIsActiveTrue(id)
        .map(accountEntity -> {
          CreateSubApiRequest createSubApiRequest = new CreateSubApiRequest();
          createSubApiRequest.setReadOnly(CreateSubApiRequest.ReadOnlyEnum.NUMBER_0);
          createSubApiRequest.setSubuid(Integer.parseInt(accountEntity.getBybitUid(), 10));

          CreateSubApiRequestPermissions permissions =
              new CreateSubApiRequestPermissions();
          permissions.addContractTradeItem(CreateSubApiRequestPermissions.ContractTradeEnum.ORDER);
          permissions.addWalletItem(CreateSubApiRequestPermissions.WalletEnum.ACCOUNTTRANSFER);
          createSubApiRequest.setPermissions(permissions);

          CreateSubApiResult subAccountApiKey =
              byBitApiClient.createSubAccountApiKey(createSubApiRequest);

          CreateSubApiResultResult result = subAccountApiKey.getResult();
          AccountApiKeyEntity accountApiKeyEntity = new AccountApiKeyEntity();
          accountApiKeyEntity.setId(id);
          accountApiKeyEntity.setApiKey(result.getApiKey());
          accountApiKeyEntity.setSecret(result.getSecret());
          accountApiKeyEntity.setNote(result.getNote());
          accountApiKeyEntity.setReadOnly(result.getReadOnly());
          accountApiKeyEntity.setPermissions(convertObject(result.getPermissions(),
              CreateSubApiResultResultPermissions.class));
          accountApiKeyRepository.save(accountApiKeyEntity);

          return convertObject(subAccountApiKey, AccountApiKey.class);
        })
        .orElseThrow(() -> new BitrubyRuntimeExpection("Account not found by user"));
  }

  public <T> T convertObject(Object sourceObject, Class<T> targetClass) {
    try {
      return objectMapper.convertValue(sourceObject, targetClass);
    } catch (Exception e) {
      throw new BitrubyRuntimeExpection("Error. Can't convert source file into target class");
    }
  }
}
