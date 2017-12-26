package softuniBlog.controller;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import softuniBlog.bindingModel.ArticleBindingModel;
import softuniBlog.bindingModel.UserBindingModel;
import softuniBlog.entity.Article;
import softuniBlog.entity.Role;
import softuniBlog.entity.User;
import softuniBlog.repository.RoleRepository;
import softuniBlog.repository.UserRepository;

import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Controller
public class UserController {
    private static final String ROLE_USER = "ROLE_USER";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;
    private CategoryController categoryController;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(RoleRepository roleRepository, UserRepository userRepository, CategoryController categoryController) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.categoryController = categoryController;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("view", "user/register");
        return "base-layout";
    }

    private void createRoles() {
        if (this.roleRepository.findByName(ROLE_USER) == null && this.roleRepository.findByName(ROLE_ADMIN) == null) {
            Role userRole = new Role();
            userRole.setName("ROLE_USER");
            Role userAdmin = new Role();
            userAdmin.setName("ROLE_ADMIN");
            this.roleRepository.saveAndFlush(userRole);
            this.roleRepository.saveAndFlush(userAdmin);
        }
    }

    @PostMapping("/register")
    public String registerProcess(UserBindingModel userBindingModel) {

        if (!userBindingModel.getPassword().equals(userBindingModel.getConfirmPassword())) {
            return "redirect:/register";
        }

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        User user = new User(
                userBindingModel.getEmail(),
                userBindingModel.getFullName(),
                this.passwordEncoder.encode(userBindingModel.getPassword())
        );

        createRoles();

        Role userRole = this.roleRepository.findByName("ROLE_USER");

        // add role admin if the user is the first registered
        if (this.userRepository.findOne(1) == null) {
            this.categoryController.initializeData();
            Role adminRole = this.roleRepository.findByName("ROLE_ADMIN");
            user.addRole(adminRole);
        }

        user.addRole(userRole);

        this.userRepository.saveAndFlush(user);

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("view", "user/login");

        return "base-layout";
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        return "redirect:/login?logout";
    }

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public String profilePage(Model model) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        User user = this.userRepository.findByEmail(principal.getUsername());

        model.addAttribute("user", user);
        model.addAttribute("view", "user/profile");

        return "base-layout";
    }

    @GetMapping("/user/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String edit(Model model, @PathVariable Integer id) {

        if(!this.isCurrentUserAdmin()){
            return "redirect:/login?logout";
        }

        if (!this.userRepository.exists(id)) {
            return "redirect:/";
        }
        User user = this.userRepository.findOne(id);

        model.addAttribute("view", "user/edit")
                .addAttribute("user", user);
        return "base-layout";
    }

    @PostMapping("/user/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String editAction(UserBindingModel userBindingModel, @PathVariable Integer id) {

        if (!this.userRepository.exists(id)) {
            return "redirect:/all_users";
        }
        if (!userBindingModel.getPassword().equals(userBindingModel.getConfirmPassword())) {
            return "redirect:/user/edit/" + id;
        }
        User user = this.userRepository.findOne(id);

        //can't change admin profile
        if(user.isAdmin()){
            return "redirect:/user/edit/" + id;
        }

        user.setEmail(userBindingModel.getEmail());
        user.setFullName(userBindingModel.getFullName());
        if(userBindingModel.getPassword() != null){
            user.setPassword(this.passwordEncoder.encode(userBindingModel.getPassword()));
        }
        this.userRepository.saveAndFlush(user);
        return "redirect:/all_users";
    }

    @GetMapping("/user/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String delete(Model model, @PathVariable Integer id) {

        if(!this.isCurrentUserAdmin()){
            return "redirect:/login?logout";
        }

        if (!this.userRepository.exists(id)) {
            return "redirect:/";
        }
        User user = this.userRepository.findOne(id);

        model.addAttribute("view", "user/delete")
                .addAttribute("user", user);
        return "base-layout";
    }

    @PostMapping("/user/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String deleteAction(@PathVariable Integer id) {

        if (!this.userRepository.exists(id)) {
            return "redirect:/";
        }

        User user = this.userRepository.findOne(id);

        //can't delete admin profile
        if(user.isAdmin()){
            return "redirect:/user/delete/" + id;
        }


        this.userRepository.delete(id);
        return "redirect:/all_users";

    }

    private boolean isCurrentUserAdmin(){


       return this.getCurrentUser().isAdmin();
    }

    private User getCurrentUser(){
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        return this.userRepository.findByEmail(principal.getUsername());
    }



}

