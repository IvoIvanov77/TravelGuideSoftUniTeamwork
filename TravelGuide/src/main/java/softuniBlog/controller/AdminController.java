package softuniBlog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import softuniBlog.bindingModel.CategoryBindingModel;
import softuniBlog.entity.Category;
import softuniBlog.entity.User;
import softuniBlog.repository.CategoryRepository;
import softuniBlog.repository.UserRepository;

import java.util.List;

/**
 * Created by George-Lenovo on 6/29/2017.
 */
@Controller
public class AdminController {
    private UserRepository userRepository;
    private CategoryRepository categoryRepository;

    @Autowired
    public AdminController(UserRepository userRepository,CategoryRepository categoryRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/all_users/addCategory")
    public String addCategory(Model model) {
        model.addAttribute("view", "admin/addCategory");
        return "base-layout";
    }

    @PostMapping("/all_users/addCategory")
    public String addCategoryProcess(Model model, CategoryBindingModel categoryBindingModel) {
        this.categoryRepository.saveAndFlush(new Category(categoryBindingModel.getName()));
        return "redirect:/all_categories";
    }

    @GetMapping("/all_users")
    @PreAuthorize("isAuthenticated()")
    public String index(Model model) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        User user = this.userRepository.findByEmail(principal.getUsername());
        model.addAttribute("user", user);
        if (user.isAdmin()) {
            List<User> allUsers = this.userRepository.findAll();
            model.addAttribute("view", "admin/admin");
            model.addAttribute("users", allUsers);
            return "base-layout";
        }

        return "redirect:/login";
    }

    @GetMapping("/all_categories")
    @PreAuthorize("isAuthenticated()")
    public String categories(Model model) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        User user = this.userRepository.findByEmail(principal.getUsername());
        model.addAttribute("user", user);
        if (user.isAdmin()) {
            List<Category> allCategories = this.categoryRepository.findAll();
            model.addAttribute("view", "category/all_categories");
            model.addAttribute("categories", allCategories);
            return "base-layout";
        }

        return "redirect:/login";
    }
}
