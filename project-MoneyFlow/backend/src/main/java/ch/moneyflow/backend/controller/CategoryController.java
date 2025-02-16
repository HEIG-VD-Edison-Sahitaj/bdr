package ch.moneyflow.backend.controller;

import ch.moneyflow.backend.entity.Category;
import ch.moneyflow.backend.request.CategoryDto;
import ch.moneyflow.backend.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clients/{clientMail}/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryDto> getCategoriesByClientMail(@PathVariable String clientMail) {
        return categoryService.getCategoriesByClientMail(clientMail);
    }

    @PostMapping
    public ResponseEntity<String> createCategory(@PathVariable String clientMail, @RequestParam String name) {
        try {
            categoryService.createCategory(clientMail, name);
            return ResponseEntity.ok("Category created successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<String> updateCategory(@PathVariable String clientMail, @PathVariable Long categoryId, @RequestParam String name) {
        try {
            categoryService.updateCategory(clientMail, categoryId, name);
            return ResponseEntity.ok("Category updated successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable String clientMail, @PathVariable Long categoryId) {
        try {
            categoryService.deleteCategory(clientMail, categoryId);
            return ResponseEntity.ok("Category deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}
