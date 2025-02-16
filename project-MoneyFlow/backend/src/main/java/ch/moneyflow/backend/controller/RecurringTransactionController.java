package ch.moneyflow.backend.controller;

import ch.moneyflow.backend.request.RecurringTransactionDto;
import ch.moneyflow.backend.service.RecurringTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clients/{clientMail}/accounts/{accountName}/recurring-transactions")
public class RecurringTransactionController {
    private final RecurringTransactionService recurringTransactionService;

    @Autowired
    public RecurringTransactionController(RecurringTransactionService recurringTransactionService) {
        this.recurringTransactionService = recurringTransactionService;
    }

    @GetMapping
    public List<RecurringTransactionDto> getRecurringTransactionsByAccountId(@PathVariable String clientMail, @PathVariable String accountName) {
        return recurringTransactionService.getRecurringTransactionsByAccountId(accountName, clientMail);
    }

    @PostMapping
    public ResponseEntity<String> createRecurringTransaction(@PathVariable String clientMail, @PathVariable String accountName, @RequestBody RecurringTransactionDto recurringTransactionDto) {
        try {
            recurringTransactionService.createRecurringTransaction(clientMail, accountName, recurringTransactionDto);
            return ResponseEntity.ok("Recurring transaction created successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRecurringTransaction(@PathVariable String clientMail, @PathVariable String accountName, @PathVariable Long id) {
        try {
            recurringTransactionService.deleteRecurringTransaction(clientMail, accountName, id);
            return ResponseEntity.ok("Recurring transaction deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}
