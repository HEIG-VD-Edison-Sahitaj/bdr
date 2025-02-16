package ch.moneyflow.backend.repository;

import ch.moneyflow.backend.entity.Category;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends CrudRepository<Category, String> {
    // Get all categories of a client
    @Query(value = "SELECT * FROM Category c WHERE c.mailClient = :mailClient", nativeQuery = true)
    List<Object[]> findByClientMail(@Param("mailClient") String mailClient);

    // Get category id by name and mailClient
    @Query(value = "SELECT id FROM Category WHERE name = :name AND mailClient = :mailClient", nativeQuery = true)
    Long findIdByName(@Param("name") String name, @Param("mailClient") String mailClient);

    // Check if a category exists by id and mailClient
    @Query(value = "SELECT EXISTS (SELECT 1 FROM Category WHERE id = :id AND mailClient = :mailClient)", nativeQuery = true)
    boolean existsById(Long id, String mailClient);

    // Check if a category exists by name and mailClient
    @Query(value = "SELECT EXISTS (SELECT 1 FROM Category WHERE name = :name AND mailClient = :mailClient)", nativeQuery = true)
    boolean existsByName(String name, String mailClient);

    // Create a new category
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO Category (name, mailClient) VALUES(:name, :mailClient)", nativeQuery = true)
    void createCategory(@Param("name") String name, @Param("mailClient") String mailClient);

    // Update a category
    @Modifying
    @Transactional
    @Query(value = "UPDATE Category SET name = :name WHERE id = :id AND mailClient = :mailClient", nativeQuery = true)
    void updateCategory(@Param("id") Long id, @Param("name") String name, @Param("mailClient") String mailClient);

    // Delete a category
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM Category WHERE id = :id AND mailClient = :mailClient", nativeQuery = true)
    void deleteCategory(@Param("id") Long id, @Param("mailClient") String mailClient);
}
