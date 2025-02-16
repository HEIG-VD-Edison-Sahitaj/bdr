package ch.moneyflow.backend.controller;

import ch.moneyflow.backend.repository.CategoryRepository;
import ch.moneyflow.backend.request.BudgetDto;
import ch.moneyflow.backend.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clients/{clientMail}/accounts/{accountName}/budgets")
public class BudgetController {
    private final BudgetService budgetService;
    private final CategoryRepository categoryRepository;

    @Autowired
    public BudgetController(BudgetService budgetService, CategoryRepository categoryRepository) {
        this.budgetService = budgetService;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public List<BudgetDto> getBudgetsByAccountName(@PathVariable String clientMail, @PathVariable String accountName) {
        return budgetService.getBudgetsByAccountName(accountName, clientMail);
    }

    @PostMapping
    public ResponseEntity<String> createBudget(@PathVariable String clientMail, @PathVariable String accountName, @RequestBody BudgetDto budgetDto) {
        try {
            budgetService.createBudget(clientMail, accountName, budgetDto);
            return ResponseEntity.ok("Budget created successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateBudget(@PathVariable String clientMail, @PathVariable String accountName, @PathVariable Long id, @RequestBody BudgetDto budgetDto) {
        try {
            budgetService.updateBudget(clientMail, accountName, id, budgetDto);
            return ResponseEntity.ok("Budget updated successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBudget(@PathVariable String clientMail, @PathVariable String accountName, @PathVariable Long id) {
        try {
            budgetService.deleteBudget(clientMail, accountName, id);
            return ResponseEntity.ok("Budget deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}
