package travelGuide.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import travelGuide.entity.User;
import travelGuide.repository.CategoryRepository;
import travelGuide.repository.UserRepository;
import travelGuide.service.NotificationService;
import travelGuide.utils.UserSession;
import travelGuide.utils.Messages;

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
            UserDetails userDetails = UserSession.getCurrentUser();
            User currUser = this.userRepository.findByEmail(userDetails.getUsername());

            model.addAttribute("user",currUser);
            model.addAttribute("view", "user/all_users");
            model.addAttribute("users", allUsers);
            return "admin/admin_panel-layout";
        }

        this.notifyService.addErrorMessage(Messages.YOU_HAVE_NO_PERMISSION);
        return "redirect:/login";
    }


}
