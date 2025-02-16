package ch.moneyflow.backend.repository;

import ch.moneyflow.backend.entity.RecurringTransaction;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface RecurringTransactionRepository extends CrudRepository<RecurringTransaction, Long> {
    // Get all recurring transactions of an account
    @Query(value = "SELECT rt.idTransaction, t.amount, t.description, t.date, rt.begintransactiondate, rt.endtransactiondate, t.type, rt.frequency, c.name, af.name, at.name " +
            "FROM Transaction t " +
            "INNER JOIN RecurringTransaction rt ON t.id = rt.idTransaction " +
            "INNER JOIN Account af ON t.idAccountFrom = af.id " +
            "LEFT JOIN Account at ON t.idAccountTo = at.id " +
            "INNER JOIN Client cl ON af.mailClient = cl.mail " +
            "INNER JOIN Category c ON t.idCategory = c.id " +
            "WHERE cl.mail = :clientMail AND (af.id = :accountId OR at.id = :accountId)", nativeQuery = true)
    List<Object[]> findAllByAccountId(@Param("accountId") Long accountId, @Param("clientMail") String clientMail);

    // Get all active recurring transactions
    @Query(value = "SELECT *" +
            "FROM RecurringTransaction rt " +
            "INNER JOIN Transaction t ON rt.idTransaction = t.id " +
            "WHERE rt.beginTransactionDate <= NOW() " +
            "AND (rt.endTransactionDate IS NULL OR rt.endTransactionDate >= NOW())", nativeQuery = true)
    List<RecurringTransaction> findAllActiveRecurringTransactions();

    // Create a new recurring transaction
    @Modifying
    @Transactional
    @Query(value = "WITH inserted_transaction AS ( " +
            "INSERT INTO Transaction (amount, description, date, type, idAccountFrom, idAccountTo, idCategory) " +
            "VALUES (:amount, :description, :beginTransactionDate, :type, :idAccountFrom, :idAccountTo, :idCategory) " +
            "RETURNING id " +
            ") " +
            "INSERT INTO RecurringTransaction (idTransaction, frequency, begintransactiondate, endtransactiondate) " +
            "VALUES ((SELECT id FROM inserted_transaction), :frequency, :beginTransactionDate, :endTransactionDate)", nativeQuery = true)
    void createRecurringTransaction(@Param("amount") BigDecimal amount, @Param("description") String description, @Param("beginTransactionDate") LocalDateTime beginTransactionDate, @Param("endTransactionDate") LocalDateTime endTransactionDate, @Param("type") String type, @Param("idAccountFrom") Long idAccountFrom, @Param("idAccountTo") Long idAccountTo, @Param("idCategory") Long idCategory, @Param("frequency") String frequency);

    // Update a recurring transaction
    @Modifying
    @Transactional
    @Query(value = "UPDATE RecurringTransaction SET frequency = :frequency, beginTransactionDate = :beginTransactionDate, endTransactionDate = :endTransactionDate WHERE idTransaction = :id", nativeQuery = true)
    void updateRecurringTransaction(@Param("id") Long id,
                                    @Param("beginTransactionDate") LocalDateTime beginTransactionDate,
                                    @Param("endTransactionDate") LocalDateTime endTransactionDate,
                                    @Param("frequency") String frequency);
}
