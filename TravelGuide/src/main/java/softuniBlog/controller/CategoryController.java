package softuniBlog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import softuniBlog.entity.Category;
import softuniBlog.repository.CategoryRepository;

import java.util.List;

/**
 * Created by George-Lenovo on 6/29/2017.
 */
@Controller
public class CategoryController {

    private CategoryRepository categoryRepository;

    @Autowired
    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/category/{id}")
    public String details(Model model, @PathVariable Integer id) {
        Category category = this.categoryRepository.findOne(id);
        List<Category> categories = this.categoryRepository.findAll();
        model.addAttribute("view", "home/index");
        model.addAttribute("categories", categories);
        model.addAttribute("destinations", category.getDestinations());
        return "base-layout";
    }
}
