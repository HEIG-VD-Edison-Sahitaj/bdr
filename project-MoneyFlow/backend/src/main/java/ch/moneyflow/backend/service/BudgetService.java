package ch.moneyflow.backend.service;

import ch.moneyflow.backend.entity.Budget;
import ch.moneyflow.backend.exception.BudgetAlreadyExistsException;
import ch.moneyflow.backend.exception.BudgetNotFoundException;
import ch.moneyflow.backend.exception.CategoryNotFoundException;
import ch.moneyflow.backend.repository.AccountRepository;
import ch.moneyflow.backend.repository.BudgetRepository;
import ch.moneyflow.backend.repository.CategoryRepository;
import ch.moneyflow.backend.request.BudgetDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BudgetService {
    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public BudgetService(BudgetRepository budgetRepository, CategoryRepository categoryRepository, AccountRepository accountRepository) {
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
        this.accountRepository = accountRepository;
    }

    // Get all budgets by account name
    public List<BudgetDto> getBudgetsByAccountName(String name, String mailClient) {
        List<Object[]> budgets = budgetRepository.findByAccountName(name, mailClient);
        return budgets.stream().map(budget -> new BudgetDto((Long) budget[0], (BigDecimal) budget[2], (BigDecimal) budget[3], ((Date) budget[4]).toLocalDate(), ((Date) budget[5]).toLocalDate(), (String) budget[7])).collect(Collectors.toList());
    }

    // Check if category exists for the account
    public void categoryNotFound(String mailClient, Long idCategory) {
        if (!categoryRepository.existsById(idCategory, mailClient)) {
            throw new CategoryNotFoundException("Category not found");
        }
    }

    // Check if budget category already exists for the account
    public void budgetAlreadyExists(Long idAccount, Long idCategory) {
        if (budgetRepository.existsByIdCategory(idAccount, idCategory)) {
            throw new BudgetAlreadyExistsException("Budget with the same category already exists");
        }
    }

    // Check if budget exists for the account
    public void budgetNotFound(Long id, Long idAccount) {
        if (!budgetRepository.existsById(id, idAccount)) {
            throw new BudgetNotFoundException("Budget with ID " + id + " not found");
        }
    }

    // Create budget
    public void createBudget(String mailClient, String accountName, BudgetDto budgetDto) {
        Long idCategory = categoryRepository.findIdByName(budgetDto.getNameCategory(), mailClient);
        Long idAccount = accountRepository.findIdByName(accountName, mailClient);
        categoryNotFound(mailClient, idCategory);
        budgetAlreadyExists(idAccount, idCategory);
        budgetRepository.createBudget(budgetDto.getMaxAmount(), budgetDto.getCurrentAmount(), idCategory, budgetDto.getFinishDate(), idAccount);
    }

    // Update budget
    public void updateBudget(String mailClient, String accountName, Long id, BudgetDto budgetDto) {
        Long idCategory = categoryRepository.findIdByName(budgetDto.getNameCategory(), mailClient);
        Long idAccount = accountRepository.findIdByName(accountName, mailClient);
        categoryNotFound(mailClient, idCategory);
        budgetNotFound(id, idAccount);
        budgetRepository.updateBudget(id, budgetDto.getMaxAmount(), budgetDto.getCurrentAmount(), idCategory, budgetDto.getStartDate(), budgetDto.getFinishDate());
    }

    // Delete budget
    public void deleteBudget(String mailClient, String accountName, Long id) {
        Long idAccount = accountRepository.findIdByName(accountName, mailClient);
        budgetNotFound(id, idAccount);
        budgetRepository.deleteBudget(id, idAccount);
    }

    // Update budget current amount
    public void updateBudgetCurrentAmount(Long categoryId, BigDecimal amount, String transactionType) {
        Budget budget = budgetRepository.findByCategoryId(categoryId)
                .orElseThrow(() -> new BudgetNotFoundException("Budget not found"));
        if ("EXPENSE".equals(transactionType)) {
            budget.setCurrentAmount(budget.getCurrentAmount().add(amount));
        } else if ("INCOME".equals(transactionType)) {
            budget.setCurrentAmount(budget.getCurrentAmount().subtract(amount));
        }
        budgetRepository.save(budget);
    }

    // Update budget after deletion
    public void updateBudgetAfterDeletion(Long categoryId, BigDecimal amount, String type) {
        Budget budget = budgetRepository.findByCategoryId(categoryId)
                .orElseThrow(() -> new BudgetNotFoundException("Budget not found"));

        if ("EXPENSE".equals(type)) {
            budget.setCurrentAmount(budget.getCurrentAmount().subtract(amount));
        } else if ("INCOME".equals(type)) {
            budget.setCurrentAmount(budget.getCurrentAmount().add(amount));
        }
        budgetRepository.save(budget);
    }
}
