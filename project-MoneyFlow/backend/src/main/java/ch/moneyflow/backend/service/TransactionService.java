package ch.moneyflow.backend.service;

import ch.moneyflow.backend.entity.Account;
import ch.moneyflow.backend.exception.AccountNotFoundException;
import ch.moneyflow.backend.exception.TransactionNotFoundException;
import ch.moneyflow.backend.repository.AccountRepository;
import ch.moneyflow.backend.repository.CategoryRepository;
import ch.moneyflow.backend.repository.TransactionRepository;
import ch.moneyflow.backend.request.TransactionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final BudgetService budgetService;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository, CategoryRepository categoryRepository, BudgetService budgetService) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.budgetService = budgetService;
    }

    // Check if transaction exists
    public void transactionNotFound(Long transactionId, Long accountId, String clientMail) {
        if (!transactionRepository.existsByIdAndAccountId(transactionId, accountId, clientMail)) {
            throw new IllegalArgumentException("Transaction with ID " + transactionId + " not found");
        }
    }

    // Get all transactions for a specific account
    public List<TransactionDto> findAllByAccountId(String accountName, String clientMail) {
        Long accountId = accountRepository.findIdByName(accountName, clientMail);
        List<Object[]> transactions = transactionRepository.findAllByAccountId(accountId, clientMail);
        return transactions.stream().map(transaction -> new TransactionDto((Long) transaction[0], (BigDecimal) transaction[1], (String) transaction[2], (LocalDateTime) transaction[3], (String) transaction[4], (String) transaction[5], (String) transaction[6], (String) transaction[7])).collect(Collectors.toList());
    }

    // Create a new transaction and update the account balance and budget current amount
    public void createTransaction(String mailClient, String debitedAccountName, TransactionDto transactionDto) {
        Long debitedAccountId = accountRepository.findIdByName(debitedAccountName, mailClient);
        Account debitedAccount = accountRepository.findAccountByIdAndClientMail(debitedAccountId, mailClient)
                .orElseThrow(() -> new AccountNotFoundException("Debited account not found"));
        Account creditedAccount = null;
        if (transactionDto.getNameAccountTo() != null) {
            Long creditedAccountId = accountRepository.findIdByName(transactionDto.getNameAccountTo(), mailClient);
            creditedAccount = accountRepository.findAccountByIdAndClientMail(creditedAccountId, mailClient)
                    .orElseThrow(() -> new AccountNotFoundException("Credited account not found"));
        }

        Long idCategory = categoryRepository.findIdByName(transactionDto.getNameCategory(), mailClient);
        transactionRepository.createTransaction(
                transactionDto.getAmount(),
                transactionDto.getDescription(),
                transactionDto.getDate(),
                transactionDto.getType().toUpperCase(),
                idCategory,
                debitedAccountId,
                creditedAccount != null ? creditedAccount.getId() : null);

        BigDecimal amount = transactionDto.getAmount();
        String type = transactionDto.getType().toUpperCase();

        if ("EXPENSE".equals(type)) {
            if (debitedAccount.getBalance().compareTo(amount) < 0) {
                throw new IllegalArgumentException("Insufficient balance in account " + debitedAccount.getName());
            }
            debitedAccount.setBalance(debitedAccount.getBalance().subtract(amount));
        } else if ("INCOME".equals(type)) {
            debitedAccount.setBalance(debitedAccount.getBalance().add(amount));
        }

        if (creditedAccount != null) {
            if ("EXPENSE".equals(type)) {
                creditedAccount.setBalance(creditedAccount.getBalance().add(amount));
            } else {
                throw new IllegalArgumentException("Transfers can only be performed for 'EXPENSE' transactions");
            }
        }

        budgetService.updateBudgetCurrentAmount(idCategory, amount, type);

        accountRepository.save(debitedAccount);
        if (creditedAccount != null) {
            accountRepository.save(creditedAccount);
        }
    }

    // Delete a transaction and update the account balance and budget current amount
    public void deleteTransaction(String mailClient, String accountName, Long transactionId) {
        Long accountId = accountRepository.findIdByName(accountName, mailClient);
        Account debitedAccount = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        Object transaction = transactionRepository.findById(transactionId, accountId, mailClient)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));

        BigDecimal amount = (BigDecimal) ((Object[]) transaction)[1];
        String type = (String) ((Object[]) transaction)[4];
        String creditedAccountName = (String) ((Object[]) transaction)[7];
        Long idCategory = categoryRepository.findIdByName((String) ((Object[]) transaction)[5], mailClient);
        Account creditedAccount = accountRepository.findAccountByIdAndClientMail(accountRepository.findIdByName(creditedAccountName, mailClient), mailClient).orElse(null);

        if ("EXPENSE".equals(type)) {
            debitedAccount.setBalance(debitedAccount.getBalance().add(amount));
            if (creditedAccount != null) {
                creditedAccount.setBalance(creditedAccount.getBalance().subtract(amount));
            }
        } else if ("INCOME".equals(type)) {
            debitedAccount.setBalance(debitedAccount.getBalance().subtract(amount));
        }

        budgetService.updateBudgetAfterDeletion(idCategory, amount, type);

        accountRepository.save(debitedAccount);
        if (creditedAccount != null) {
            accountRepository.save(creditedAccount);
        }
        transactionRepository.deleteTransactionById(transactionId, accountId, mailClient);
    }
}