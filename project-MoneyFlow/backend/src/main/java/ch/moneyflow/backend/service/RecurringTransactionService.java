package ch.moneyflow.backend.service;

import ch.moneyflow.backend.entity.Account;
import ch.moneyflow.backend.entity.RecurringTransaction;
import ch.moneyflow.backend.repository.AccountRepository;
import ch.moneyflow.backend.repository.CategoryRepository;
import ch.moneyflow.backend.repository.RecurringTransactionRepository;
import ch.moneyflow.backend.request.RecurringTransactionDto;
import ch.moneyflow.backend.request.TransactionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecurringTransactionService {
    private final RecurringTransactionRepository recurringTransactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionService transactionService;
    private final BudgetService budgetService;

    public RecurringTransactionService(RecurringTransactionRepository recurringTransactionRepository, AccountRepository accountRepository, CategoryRepository categoryRepository, TransactionService transactionService, BudgetService budgetService) {
        this.recurringTransactionRepository = recurringTransactionRepository;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.transactionService = transactionService;
        this.budgetService = budgetService;
    }

    // Get all recurring transactions of an account
    public List<RecurringTransactionDto> getRecurringTransactionsByAccountId(String accountName, String clientMail) {
        Long accountId = accountRepository.findIdByName(accountName, clientMail);
        List<Object[]> recurringTransactions = recurringTransactionRepository.findAllByAccountId(accountId, clientMail);
        return recurringTransactions.stream().map(recurringTransaction -> new RecurringTransactionDto((Integer) recurringTransaction[0], (BigDecimal) recurringTransaction[1], (String) recurringTransaction[2], ((Timestamp) recurringTransaction[3]).toLocalDateTime(), ((Timestamp) recurringTransaction[4]).toLocalDateTime(), recurringTransaction[5] != null ? ((Timestamp) recurringTransaction[5]).toLocalDateTime() : null, (String) recurringTransaction[6], (String) recurringTransaction[7], (String) recurringTransaction[8], (String) recurringTransaction[9], (String) recurringTransaction[10])).collect(Collectors.toList());
    }

    // Create a new recurring transaction
    public void createRecurringTransaction(String mailClient, String debitedAccountName, RecurringTransactionDto recurringTransactionDto) {
        Long debitedAccountId = accountRepository.findIdByName(debitedAccountName, mailClient);
        Long creditedAccountId = accountRepository.findIdByName(recurringTransactionDto.getCreditedAccount(), mailClient);
        Long categoryId = categoryRepository.findIdByName(recurringTransactionDto.getNameCategory(), mailClient);

        recurringTransactionRepository.createRecurringTransaction(
                recurringTransactionDto.getAmount(),
                recurringTransactionDto.getDescription(),
                recurringTransactionDto.getBeginTransactionDate(),
                recurringTransactionDto.getEndTransactionDate(),
                recurringTransactionDto.getType(),
                debitedAccountId,
                creditedAccountId,
                categoryId,
                recurringTransactionDto.getFrequency()
        );

        if (recurringTransactionDto.getBeginTransactionDate().isBefore(new Timestamp(System.currentTimeMillis()).toLocalDateTime())) {
            TransactionDto transactionDto = new TransactionDto(
                    null,
                    recurringTransactionDto.getAmount(),
                    recurringTransactionDto.getDescription(),
                    recurringTransactionDto.getBeginTransactionDate(),
                    recurringTransactionDto.getType(),
                    recurringTransactionDto.getNameCategory(),
                    recurringTransactionDto.getDebitedAccount(),
                    recurringTransactionDto.getCreditedAccount()
            );
            transactionService.createTransaction(mailClient, debitedAccountName, transactionDto);
        }
    }

    // Delete a recurring transaction
    public void deleteRecurringTransaction(String clientMail, String accountName, Long transactionId) {
        transactionService.deleteTransaction(clientMail, accountName, transactionId);
    }

    // Check begin date of recurring transaction is before now and update the account balance and budget current amount
    @Scheduled(fixedRate = 86400000)
    public void processRecurringTransactions() {
        List<RecurringTransaction> dueTransactions = recurringTransactionRepository.findAllActiveRecurringTransactions();

        for (RecurringTransaction recurringTransaction : dueTransactions) {
            Account debitedAccount = recurringTransaction.getAccountFrom();
            Account creditedAccount = recurringTransaction.getAccountTo();
            if (debitedAccount != null) {
                debitedAccount.setBalance(debitedAccount.getBalance().subtract(recurringTransaction.getAmount()));
                accountRepository.updateAccount(debitedAccount.getId(), debitedAccount.getName(), debitedAccount.getBalance(), debitedAccount.getClient().getMail());
            }
            if (creditedAccount != null) {
                creditedAccount.setBalance(creditedAccount.getBalance().add(recurringTransaction.getAmount()));
                accountRepository.updateAccount(creditedAccount.getId(), creditedAccount.getName(), creditedAccount.getBalance(), creditedAccount.getClient().getMail());
            }
            if (recurringTransaction.getCategory() != null) {
                budgetService.updateBudgetCurrentAmount(recurringTransaction.getCategory().getId(), recurringTransaction.getAmount(), recurringTransaction.getType());
            }

            recurringTransaction.setBeginTransactionDate(calculateNextDate(recurringTransaction.getBeginTransactionDate(), recurringTransaction.getFrequency()));
            recurringTransactionRepository.updateRecurringTransaction(
                    recurringTransaction.getId(),
                    recurringTransaction.getBeginTransactionDate(),
                    recurringTransaction.getEndTransactionDate(),
                    recurringTransaction.getFrequency()
            );
        }
    }

    // Calculate the next date of a recurring transaction
    private LocalDateTime calculateNextDate(LocalDateTime current, String frequency) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Timestamp.valueOf(current));
        switch (frequency.toUpperCase()) {
            case "DAILY":
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                break;
            case "WEEKLY":
                calendar.add(Calendar.WEEK_OF_YEAR, 1);
                break;
            case "MONTHLY":
                calendar.add(Calendar.MONTH, 1);
                break;
            case "YEARLY":
                calendar.add(Calendar.YEAR, 1);
                break;
            default:
                throw new IllegalArgumentException("Invalid frequency");
        }
        return new Timestamp(calendar.getTimeInMillis()).toLocalDateTime();
    }
}
