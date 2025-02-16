package ch.moneyflow.backend.service;

import ch.moneyflow.backend.exception.CategoryAlreadyExistsException;
import ch.moneyflow.backend.exception.CategoryNotFoundException;
import ch.moneyflow.backend.repository.CategoryRepository;
import ch.moneyflow.backend.request.CategoryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // Get all categories by client mail
    public List<CategoryDto> getCategoriesByClientMail(String mailClient) {
        List<Object[]> categories = categoryRepository.findByClientMail(mailClient);
        return categories.stream().map(category -> new CategoryDto((Long) category[0], (String) category[1])).collect(Collectors.toList());
    }

    // Check if category exists by ID and client mail
    public void categoryNotFound(String mailClient, Long categoryId) {
        if (!categoryRepository.existsById(categoryId, mailClient)) {
            throw new CategoryNotFoundException("Category with ID " + categoryId + " not found");
        }
    }

    // Check if category name already exists
    public void categoryAlreadyExists(String mailClient, String name) {
        if (categoryRepository.existsByName(name, mailClient)) {
            throw new CategoryAlreadyExistsException("Category with name " + name + " already exists");
        }
    }

    // Create category
    public void createCategory(String mailClient, String name) {
        categoryAlreadyExists(mailClient, name);
        categoryRepository.createCategory(name, mailClient);
    }

    // Update category
    public void updateCategory(String mailClient, Long id, String name) {
        categoryAlreadyExists(mailClient, name);
        categoryNotFound(mailClient, id);
        categoryRepository.updateCategory(id, name, mailClient);
    }

    // Delete category
    public void deleteCategory(String mailClient, Long id) {
        categoryNotFound(mailClient, id);
        categoryRepository.deleteCategory(id, mailClient);
    }
}
