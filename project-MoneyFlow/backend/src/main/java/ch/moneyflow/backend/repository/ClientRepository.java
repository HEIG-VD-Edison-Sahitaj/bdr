package ch.moneyflow.backend.repository;

import ch.moneyflow.backend.entity.Client;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ClientRepository extends CrudRepository<Client, String> {
    // Get client by mail
    @Query(value = "SELECT * FROM Client WHERE mail=:mail", nativeQuery = true)
    Optional<Client> findByMail(@Param("mail") String mail);

    // Check if client exists by mail or pseudo
    @Query(value = "SELECT COUNT(*) > 0 FROM Client WHERE mail = :mail OR pseudo = :pseudo", nativeQuery = true)
    boolean existsByMail(@Param("mail") String mail, @Param("pseudo") String pseudo);

    // Register client
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO Client (mail, pseudo, passwordHash, createAt) VALUES(:mail, :pseudo, :passwordHash, CURRENT_DATE)", nativeQuery = true)
    void register(@Param("mail") String mail, @Param("pseudo") String pseudo, @Param("passwordHash") String passwordHash);
}