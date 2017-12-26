package softuniBlog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import softuniBlog.entity.Category;
import softuniBlog.repository.CategoryRepository;

import java.util.List;

/**
 * Created by George-Lenovo on 6/29/2017.
 */
@Controller
public class HomeControllerNew {

    private CategoryRepository categoryRepository;

    @Autowired
    public HomeControllerNew(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/newHome")
    public String newIndex(Model model) {
        List<Category> categories = this.categoryRepository.findAll();
        model.addAttribute("view", "home/newIndex");
        model.addAttribute("categories", categories);
        return "base-layout";
    }
}
