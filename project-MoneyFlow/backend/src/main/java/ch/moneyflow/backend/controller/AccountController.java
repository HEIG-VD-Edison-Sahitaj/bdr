package ch.moneyflow.backend.controller;

import ch.moneyflow.backend.request.AccountDto;
import ch.moneyflow.backend.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clients/{clientMail}/accounts")
public class AccountController {
    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public List<AccountDto> getAccountsByClientMail(@PathVariable String clientMail) {
        return accountService.getAccountsByClientMail(clientMail);
    }

    @PostMapping
    public ResponseEntity<String> createAccount(@PathVariable String clientMail, @RequestBody AccountDto accountDto) {
        try {
            accountService.createAccount(clientMail, accountDto);
            return ResponseEntity.ok("Account created successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<String> updateAccount(@PathVariable String clientMail, @PathVariable Long accountId, @RequestBody AccountDto accountDto) {
        try {
            accountDto.setId(accountId);
            accountService.updateAccount(clientMail, accountDto);
            return ResponseEntity.ok("Account updated successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<String> deleteAccount(@PathVariable String clientMail, @PathVariable Long accountId) {
        try {
            accountService.deleteAccount(clientMail, accountId);
            return ResponseEntity.ok("Account deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}
