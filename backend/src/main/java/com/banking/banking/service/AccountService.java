package com.banking.banking.service;

import com.banking.banking.dto.AccountDto;
import com.banking.banking.exceptions.custom.AccountAlreadyExistException;
import com.banking.banking.mapper.AccountMapper;
import com.banking.banking.model.Account;
import com.banking.banking.model.User;
import com.banking.banking.repositories.AccountRepository;
import com.banking.banking.request.AccountCreateRequest;
import com.banking.banking.request.AccountSearchRequest;
import com.banking.banking.request.AccountUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.banking.banking.utils.SecurityUtil.getCurrentUser;


@Service
@Slf4j
@Transactional(rollbackFor = {Exception.class})
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public AccountService(AccountRepository accountRepository, AccountMapper accountMapper) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
    }

    public AccountDto create(AccountCreateRequest accountRequest) {
        String accountName = accountRequest.getName();
        User authenticatedUser = getCurrentUser();
        if (accountRepository.existsByNameAndUserId(accountName, authenticatedUser.getId())) {
            log.warn("Account creation failed: username '{}' is already taken.", accountName);
            throw new AccountAlreadyExistException("An account with the given details already exists.");
        }

        Account account = new Account();

        account.setName(accountName);
        account.setBalance(accountRequest.getBalance());
        account.setNumber(UUID.randomUUID().toString());
        account.setUser(authenticatedUser);

        accountRepository.save(account);
        return accountMapper.accountToDto(account);
    }

    public List<AccountDto> searchAccounts(AccountSearchRequest searchRequest) {
        User authenticatedUser = getCurrentUser();

        String number = Optional.ofNullable(searchRequest.getNumber()).orElse("");
        String name = Optional.ofNullable(searchRequest.getName()).orElse("");

        List<Account> accounts = accountRepository.findByUserIdAndNumberContainingAndNameContaining(authenticatedUser.getId(), number, name);

        return accountMapper.accountListToDtoList(accounts);
    }

    public void delete(UUID id) {
        accountRepository.deleteByIdAndUserId(id, getCurrentUser().getId());
    }

    public void update(UUID id, AccountUpdateRequest accountUpdateRequest) {
        UUID userId = getCurrentUser().getId();

        Account account = accountRepository.findByIdAndUserId(id, userId);
        account.setName(accountUpdateRequest.getName());

        accountRepository.save(account);
    }
}
