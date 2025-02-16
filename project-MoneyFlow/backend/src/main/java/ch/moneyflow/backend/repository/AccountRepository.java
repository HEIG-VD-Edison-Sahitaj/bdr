package ch.moneyflow.backend.repository;

import ch.moneyflow.backend.entity.Account;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountRepository extends CrudRepository<Account, Long> {
    // Get all accounts by client mail
    @Query(value = "SELECT a.id, a.name, a.balance, a.createat FROM Account a INNER JOIN Client c ON a.mailclient = c.mail WHERE c.mail = :mailclient", nativeQuery = true)
    List<Object[]> findByClientMail(@Param("mailclient") String mailclient);

    // Get account by id and mail client
    @Query(value = "SELECT * FROM Account WHERE id = :id AND mailclient = :mailclient", nativeQuery = true)
    Optional<Account> findAccountByIdAndClientMail(@Param("id") Long id, @Param("mailclient") String mailclient);

    // Get account id by name and mail client
    @Query(value = "SELECT id FROM Account WHERE name = :name AND mailclient = :mailclient", nativeQuery = true)
    Long findIdByName(@Param("name") String name, @Param("mailclient") String mailclient);

    // Check if account exists by id and mail client
    @Query(value = "SELECT EXISTS (SELECT 1 FROM Account WHERE id = :id AND mailclient = :mailclient)", nativeQuery = true)
    boolean existsById(@Param("id") Long id, @Param("mailclient") String mailclient);

    // Create account
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO Account (name, balance, createat, mailClient) VALUES(:name, :balance, CURRENT_DATE, :clientMail)", nativeQuery = true)
    void createAccount(@Param("name") String name, @Param("balance") BigDecimal balance, @Param("clientMail") String clientMail);

    // Update account
    @Modifying
    @Transactional
    @Query(value = "UPDATE Account SET name = :name, balance = :balance WHERE id = :id AND mailClient = :clientMail", nativeQuery = true)
    void updateAccount(@Param("id") Long id, @Param("name") String name, @Param("balance") BigDecimal balance, @Param("clientMail") String clientMail);

    // Delete account
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM Account  WHERE ID = :id AND mailClient = :clientMail", nativeQuery = true)
    void deleteAccount(@Param("id") Long id, @Param("clientMail") String clientMail);
}
