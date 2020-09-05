package com.altruist.account;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
public class AccountService {

  private final AccountRepository accountRepository;

  public Optional<AccountDto> getById(UUID id) {
    return accountRepository.findById(id).map(AccountDto::new);
  }

  public List<AccountDto> getAll() {
    return StreamSupport
            .stream(accountRepository.findAll().spliterator(), false)
            .map(AccountDto::new)
            .collect(Collectors.toList());
  }

  public UUID createAccount(AccountDto accountDto) {

    AccountDto.AddressDto addressDto = accountDto.getAddress();

    AccountEntity account = new AccountEntity(
            accountDto.getUsername(),
            accountDto.getEmail(),
            addressDto == null ? null :
                    new AddressEntity(
                            addressDto.getName(),
                            addressDto.getStreet(),
                            addressDto.getCity(),
                            addressDto.getState(),
                            addressDto.getZipcode()
                    ));

    return accountRepository.save(account).getId();
  }

}
