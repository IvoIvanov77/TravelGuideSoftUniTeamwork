package softuniBlog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import softuniBlog.bindingModel.CategoryBindingModel;
import softuniBlog.entity.Category;
import softuniBlog.entity.User;
import softuniBlog.repository.CategoryRepository;
import softuniBlog.repository.UserRepository;
import softuniBlog.service.NotificationService;
import softuniBlog.utils.Messages;

import java.util.List;


@Controller
public class AdminController {
    private UserRepository userRepository;
    private CategoryRepository categoryRepository;
    private final NotificationService notifyService;

    @Autowired

    public AdminController(UserRepository userRepository, CategoryRepository categoryRepository, NotificationService notifyService) {

        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.notifyService = notifyService;
    }

    @GetMapping("/category/addCategory")
    public String addCategory(Model model) {
        model.addAttribute("view", "admin/addCategory");
        return "base-layout";
    }

    @PostMapping("/category/addCategory")
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
            model.addAttribute("view", "admin/listUsers");
            model.addAttribute("users", allUsers);
            return "base-layout";
        }

        this.notifyService.addInfoMessage(Messages.ERROR);
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

        this.notifyService.addInfoMessage(Messages.ERROR);
        return "redirect:/login";
    }

    @GetMapping("/category/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String editCategory(Model model, @PathVariable Integer id) {

        if (!this.categoryRepository.exists(id)) {
            this.notifyService.addInfoMessage(Messages.ERROR);
            return "redirect:/";
        }

        if (!this.isCurrentUserAdmin()) {
            this.notifyService.addInfoMessage(Messages.ERROR);
            return "redirect:/login";
        }

        Category category = this.categoryRepository.findOne(id);

        model.addAttribute("view", "category/edit")
                .addAttribute("category", category);

        return "base-layout";
    }

    @PostMapping("/category/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String editCategoryAction(CategoryBindingModel categoryBindingModel, @PathVariable Integer id) {

        if (!this.categoryRepository.exists(id)) {
            this.notifyService.addInfoMessage(Messages.ERROR);
            return "redirect:/";
        }

        if (!this.isCurrentUserAdmin()) {
            this.notifyService.addInfoMessage(Messages.ERROR);
            return "redirect:/login";
        }

        Category category = this.categoryRepository.findOne(id);

        category.setName(categoryBindingModel.getName());


        this.categoryRepository.saveAndFlush(category);
        this.notifyService.addInfoMessage(Messages.SUCCESS);
        return "redirect:/all_categories";

    }

    @GetMapping("/category/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String deleteCategory(Model model, @PathVariable Integer id) {

        if (!this.categoryRepository.exists(id)) {
            this.notifyService.addInfoMessage(Messages.ERROR);
            return "redirect:/";
        }
        if (!this.isCurrentUserAdmin()) {
            this.notifyService.addInfoMessage(Messages.ERROR);
            return "redirect:/login";
        }

        Category category = this.categoryRepository.findOne(id);

        model.addAttribute("view", "category/delete")
                .addAttribute("category", category);
        return "base-layout";
    }

    @PostMapping("/category/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String deleteCategoryAction(@PathVariable Integer id) {

        if (!this.categoryRepository.exists(id)) {
            this.notifyService.addInfoMessage(Messages.ERROR);
            return "redirect:/";
        }

        if (!this.isCurrentUserAdmin()) {
            this.notifyService.addInfoMessage(Messages.ERROR);
            return "redirect:/login";
        }

        Category category = this.categoryRepository.findOne(id);


        this.categoryRepository.delete(id);
        this.notifyService.addInfoMessage(Messages.SUCCESS);
        return "redirect:/all_categories";

    }

    private boolean isCurrentUserAdmin() {
        return this.getCurrentUser() != null && this.getCurrentUser().isAdmin();
    }

    //
    private User getCurrentUser() {

        if (!(SecurityContextHolder.getContext().getAuthentication()
                instanceof AnonymousAuthenticationToken)) {
            UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();

            return this.userRepository.findByEmail(principal.getUsername());

        }

        return null;
    }

}
