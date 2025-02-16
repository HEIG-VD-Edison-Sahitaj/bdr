package ch.moneyflow.backend.service;

import ch.moneyflow.backend.entity.Account;
import ch.moneyflow.backend.exception.AccountNotFoundException;
import ch.moneyflow.backend.repository.AccountRepository;
import ch.moneyflow.backend.request.AccountDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {
    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    // Get all accounts by client mail
    public List<AccountDto> getAccountsByClientMail(String clientMail) {
        List<Object[]> accounts = accountRepository.findByClientMail(clientMail);
        return accounts.stream().map(account -> new AccountDto((Long) account[0], (String) account[1], (BigDecimal) account[2], ((Date) account[3]).toLocalDate())).collect(Collectors.toList());
    }

    // Check if account exists by id and mail client
    public void accountNotFound(String clientMail, Long accountId) {
       if (!accountRepository.existsById(accountId, clientMail)) {
           throw new AccountNotFoundException("Account with ID " + accountId + " not found");
       }
    }

    // Create account
    public void createAccount(String clientMail, AccountDto accountDto) {
        accountRepository.createAccount(accountDto.getName(), accountDto.getBalance(), clientMail);
    }

    // Update account
    public void updateAccount(String clientMail, AccountDto accountDto) {
        accountNotFound(clientMail, accountDto.getId());
        accountRepository.updateAccount(accountDto.getId(), accountDto.getName(), accountDto.getBalance(), clientMail);
    }

    // Delete account
    public void deleteAccount(String clientMail, Long accountId) {
        accountNotFound(clientMail, accountId);
        accountRepository.deleteAccount(accountId, clientMail);
    }
}
