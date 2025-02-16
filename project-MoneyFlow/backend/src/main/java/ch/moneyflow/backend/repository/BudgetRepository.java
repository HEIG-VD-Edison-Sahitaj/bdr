package ch.moneyflow.backend.repository;

import ch.moneyflow.backend.entity.Budget;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends CrudRepository<Budget, Long> {
    // Get all budgets by account name
    @Query(value = "SELECT b.id, b.idAccount, b.maxAmount, b.currentAmount, b.startDate, b.finishDate, b.idCategory, c.name FROM Budget  b INNER JOIN Category c ON b.idCategory = c.id WHERE B.idAccount = (SELECT id FROM Account WHERE name = :name AND mailClient = :mailClient)", nativeQuery = true)
    List<Object[]> findByAccountName(@Param("name") String name, @Param("mailClient") String mailClient);

    // Get budget by category id
    @Query("SELECT b FROM Budget b WHERE b.category.id = :categoryId")
    Optional<Budget> findByCategoryId(@Param("categoryId") Long categoryId);

    // Check if budget exists by account name and category name
    @Query(value = "SELECT EXISTS (SELECT 1 FROM BUDGET WHERE idAccount = :idAccount AND idCategory = :idCategory)", nativeQuery = true)
    boolean existsByIdCategory(@Param("idAccount") Long idAccount, @Param("idCategory") Long idCategory);

    // Check if budget exists by id and account id
    @Query(value = "SELECT EXISTS (SELECT 1 FROM BUDGET WHERE id = :id AND idAccount = :idAccount)", nativeQuery = true)
    boolean existsById(@Param("id") Long id, @Param("idAccount") Long idAccount);

    // Create new budget
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO Budget (maxAmount, currentAmount, startDate, finishDate, idCategory, idAccount) VALUES (:maxAmount, :currentAmount, CURRENT_DATE, :finishDate, :idCategory, :idAccount)", nativeQuery = true)
    void createBudget(@Param("maxAmount") BigDecimal maxAmount, @Param("currentAmount") BigDecimal currentAmount, @Param("idCategory") Long idCategory, @Param("finishDate") LocalDate finishDate, @Param("idAccount") Long idAccount);

    // Update budget
    @Modifying
    @Transactional
    @Query(value = "UPDATE Budget  SET maxAmount = :maxAmount, currentAmount = :currentAmount, idCategory = :idCategory, startDate = :startDate, finishDate = :finishDate WHERE id = :id", nativeQuery = true)
    void updateBudget(@Param("id") Long id, @Param("maxAmount") BigDecimal maxAmount, @Param("currentAmount") BigDecimal currentAmount, @Param("idCategory") Long idCategory, @Param("startDate") LocalDate startDate, @Param("finishDate") LocalDate finishDate);

    // Delete budget
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM Budget WHERE id = :id AND idAccount = :idAccount", nativeQuery = true)
    void deleteBudget(@Param("id") Long id, @Param("idAccount") Long idAccount);
}
