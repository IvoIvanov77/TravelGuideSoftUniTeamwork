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
    public AdminController(UserRepository userRepository, CategoryRepository categoryRepository,
                           NotificationService notifyService) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.notifyService = notifyService;
    }

    @GetMapping("/category/addCategory")
    public String addCategory(Model model) {
        model.addAttribute("view", "category/add_category");
        return "admin/admin_panel-layout";
    }

    @PostMapping("/category/addCategory")
    public String addCategoryProcess(Model model, CategoryBindingModel categoryBindingModel) {
        this.categoryRepository.saveAndFlush(new Category(categoryBindingModel.getName()));
        this.notifyService.addInfoMessage(Messages.SUCCESSFULLY_CREATED_CATEGORY);
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
            model.addAttribute("view", "user/all_users");
            model.addAttribute("users", allUsers);
            return "admin/admin_panel-layout";
        }

        this.notifyService.addErrorMessage(Messages.YOU_HAVE_NO_PERMISSION);
        return "redirect:/login";
    }


}
