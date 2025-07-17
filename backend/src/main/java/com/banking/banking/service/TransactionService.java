package com.banking.banking.service;

import com.banking.banking.dto.TransactionDto;
import com.banking.banking.enums.TransactionStatus;
import com.banking.banking.exceptions.custom.AccountNotFoundException;
import com.banking.banking.exceptions.custom.InsufficientBalanceException;
import com.banking.banking.exceptions.custom.TransactionsSecurityException;
import com.banking.banking.mapper.TransactionMapper;
import com.banking.banking.model.Account;
import com.banking.banking.model.Transaction;
import com.banking.banking.model.User;
import com.banking.banking.repositories.AccountRepository;
import com.banking.banking.repositories.TransactionRepository;
import com.banking.banking.request.TransactionRequest;
import com.banking.banking.utils.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    private final TransactionMapper transactionMapper;

    public TransactionService(AccountRepository accountRepository, TransactionRepository transactionRepository, TransactionMapper transactionMapper) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
    }

    @Transactional(rollbackFor = {Exception.class})
    public void transfer(TransactionRequest transactionRequest) {
        Transaction transaction = new Transaction();
        transaction.setAmount(transactionRequest.getAmount());

        try {
            Account sourceAccount = accountRepository.findByNumberAndUserId(
                    transactionRequest.getFrom(),
                            SecurityUtil.getCurrentUser().
                                    getId())

                    .orElseThrow(() -> new AccountNotFoundException("Source account not found"));
            Account targetAccount = accountRepository.findByNumber(transactionRequest.getTo())
                    .orElseThrow(() -> new AccountNotFoundException("Target account not found"));

            if (sourceAccount.getBalance().compareTo(transactionRequest.getAmount()) < 0) {
                throw new InsufficientBalanceException("Insufficient balance");
            }

            sourceAccount.setBalance(sourceAccount.getBalance().subtract(transactionRequest.getAmount()));
            targetAccount.setBalance(targetAccount.getBalance().add(transactionRequest.getAmount()));

            accountRepository.save(sourceAccount);
            accountRepository.save(targetAccount);

            transaction.setFrom(sourceAccount);
            transaction.setTo(targetAccount);
            transaction.setStatus(TransactionStatus.SUCCESS);

        } catch (Exception e) {
            transaction.setStatus(TransactionStatus.FAILED);
            throw e;
        } finally {
            transactionRepository.save(transaction);
        }
    }

    public List<TransactionDto> transactionsByAccountId(String accountId) {
        UUID userID = SecurityUtil.getCurrentUser().getId();
        UUID accountUUID = UUID.fromString(accountId);

        if (!accountRepository.existsByIdAndUserId(accountUUID, userID)) {
            throw new TransactionsSecurityException("You are not authorized to access transactions for this account.");
        }

        List<Transaction> transactions = transactionRepository.findByAccountId(accountUUID);
        return transactionMapper.transactionListToDtoList(transactions);
    }
}
