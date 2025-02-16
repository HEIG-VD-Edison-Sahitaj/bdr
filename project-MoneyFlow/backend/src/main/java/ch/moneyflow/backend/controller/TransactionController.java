package ch.moneyflow.backend.controller;

import ch.moneyflow.backend.request.TransactionDto;
import ch.moneyflow.backend.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/clients/{clientMail}/accounts/{accountName}/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public List<TransactionDto> findAllByAccountId(@PathVariable String clientMail, @PathVariable String accountName) {
        return transactionService.findAllByAccountId(accountName, clientMail);
    }

    @PostMapping
    public ResponseEntity<String> createTransaction(@PathVariable String clientMail, @PathVariable String accountName, @RequestBody TransactionDto transactionDto) {
        try {
            transactionService.createTransaction(clientMail, accountName, transactionDto);
            return ResponseEntity.ok("Transaction created successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTransaction(@PathVariable String clientMail, @PathVariable String accountName, @PathVariable Long id) {
        try {
            transactionService.deleteTransaction(clientMail, accountName, id);
            return ResponseEntity.ok("Transaction deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}
