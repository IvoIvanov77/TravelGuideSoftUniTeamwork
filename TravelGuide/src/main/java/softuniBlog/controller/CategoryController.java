package softuniBlog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import softuniBlog.entity.Category;
import softuniBlog.repository.CategoryRepository;

/**
 * Created by George-Lenovo on 6/29/2017.
 */
public class CategoryController {

    private CategoryRepository categoryRepository;

    @Autowired
    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/newHome/category/{id}")
    public String details(Model model, @PathVariable Integer id) {
        Category category = this.categoryRepository.findOne(id);
        model.addAttribute("view", "category/details");
        model.addAttribute("category", category);
        return "base-layout";
    }
}
