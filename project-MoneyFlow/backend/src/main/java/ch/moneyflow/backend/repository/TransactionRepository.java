package ch.moneyflow.backend.repository;

import ch.moneyflow.backend.entity.Transaction;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {
    // Get all transactions of an account
    @Query("SELECT t.id AS transactionId, t.amount AS amount, t.description AS description, " +
            "t.date AS date, t.type AS type, c.name AS categoryName, " +
            "fromAccount.name AS debitedAccountName, toAccount.name AS creditedAccountName " +
            "FROM Transaction t " +
            "INNER JOIN t.accountFrom fromAccount " +
            "LEFT JOIN t.accountTo toAccount " +
            "INNER JOIN fromAccount.client client " +
            "LEFT JOIN t.category c " +
            "WHERE client.mail = :clientMail " +
            "AND (fromAccount.id = :accountId OR toAccount.id = :accountId) " +
            "ORDER BY t.date DESC")
    List<Object[]> findAllByAccountId(@Param("accountId") Long accountId, @Param("clientMail") String clientMail);

    // Get transaction by ID
    @Query("SELECT t.id AS transactionId, t.amount AS amount, t.description AS description, " +
            "t.date AS date, t.type AS type, c.name AS categoryName, " +
            "fromAccount.name AS debitedAccountName, toAccount.name AS creditedAccountName " +
            "FROM Transaction t " +
            "INNER JOIN t.accountFrom fromAccount " +
            "LEFT JOIN t.accountTo toAccount " +
            "INNER JOIN fromAccount.client client " +
            "LEFT JOIN t.category c " +
            "WHERE client.mail = :clientMail " +
            "AND (fromAccount.id = :accountId OR toAccount.id = :accountId) " +
            "AND t.id = :transactionId")
    Optional<Object> findById(@Param("transactionId") Long transactionId, @Param("accountId") Long accountId, @Param("clientMail") String clientMail);

    // Check if transaction exists
    @Query(value = "SELECT EXISTS (SELECT 1 FROM Transaction WHERE id = :transactionId AND (idaccountfrom = :accountId OR idaccountto = :accountId) AND EXISTS (SELECT 1 FROM account WHERE id = :accountId AND mailclient = :clientMail))", nativeQuery = true)
    boolean existsByIdAndAccountId(@Param("transactionId") Long transactionId, @Param("accountId") Long accountId, @Param("clientMail") String clientMail);

    // Create a transaction
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO Transaction (amount, description, date, type, idcategory, idaccountfrom, idaccountto) " +
                    "VALUES (:amount, :description, :date, :type, :categoryId, :debitedAccountId, :creditedAccountId)", nativeQuery = true)
    void createTransaction(@Param("amount") BigDecimal amount, @Param("description") String description, @Param("date") LocalDateTime date, @Param("type") String type, @Param("categoryId") Long categoryId, @Param("debitedAccountId") Long debitedAccountId, @Param("creditedAccountId") Long creditedAccountId);
    // Delete a transaction
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM transaction " +
            "WHERE id = :transactionId " +
            "AND (idaccountfrom = :accountId OR idaccountto = :accountId) " +
            "AND EXISTS (SELECT 1 FROM account WHERE id = :accountId AND mailclient = :clientMail)",
            nativeQuery = true)
    void deleteTransactionById(@Param("transactionId") Long transactionId, @Param("accountId") Long accountId, @Param("clientMail") String clientMail);

}
