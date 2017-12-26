package softuniBlog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import softuniBlog.entity.Category;
import softuniBlog.repository.CategoryRepository;

import java.util.Arrays;

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

    @GetMapping("/newHome/category/{id}")
    public String details(Model model, @PathVariable Integer id) {
        Category category = this.categoryRepository.findOne(id);
        model.addAttribute("view", "category/details");
        model.addAttribute("category", category);
        return "base-layout";
    }

    void initializeData() {
        Category coastal = new Category("Coastal Tourism");
        Category urban = new Category("Urban Tourism");
        Category rural = new Category("Rural Tourism");
        this.categoryRepository.save(Arrays.asList(new Category[]{coastal, urban, rural}));
    }
}
