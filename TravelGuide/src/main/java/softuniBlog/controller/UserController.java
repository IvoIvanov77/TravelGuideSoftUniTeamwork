package softuniBlog.controller;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import softuniBlog.bindingModel.UserBindingModel;
import softuniBlog.entity.Role;
import softuniBlog.entity.User;
import softuniBlog.repository.RoleRepository;
import softuniBlog.repository.UserRepository;
import softuniBlog.service.NotificationService;
import softuniBlog.utils.Messages;

import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
public class UserController {


    private static final String ROLE_USER = "ROLE_USER";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";


    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final NotificationService notifyService;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(RoleRepository roleRepository, UserRepository userRepository, CategoryController categoryController, NotificationService notifyService) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.notifyService = notifyService;
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("view", "user/register");
        if(this.isCurrentUserAdmin()){
            return "admin/admin_panel-layout";
        }
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
    public String registerProcess(UserBindingModel userBindingModel,
                                  @RequestParam(defaultValue = "false") boolean checkbox) {

        //checking for existing email
        if(this.userRepository.findAll()
                .stream()
                .filter(user -> user.getEmail().equals(userBindingModel.getEmail()))
                .count() > 0){
            notifyService.addErrorMessage(Messages.EMAIL_ALREADY_EXIST);
            return "redirect:/register";
        }

        if (!userBindingModel.getPassword().equals(userBindingModel.getConfirmPassword())) {
            notifyService.addErrorMessage(Messages.THE_PASSWORD_DOESN_T_MATCH);
            return "redirect:/register";
        }


        User user = new User(
                userBindingModel.getEmail(),
                userBindingModel.getFullName(),
                this.passwordEncoder.encode(userBindingModel.getPassword())
        );

        createRoles();

        Role userRole = this.roleRepository.findByName("ROLE_USER");
        Role adminRole = this.roleRepository.findByName("ROLE_ADMIN");

        // add role admin if the user is the first registered
        if (this.userRepository.findOne(1) == null) {
//            this.categoryController.initializeData();
            user.addRole(adminRole);
        }

        if(checkbox){
            user.addRole(adminRole);
        }

        user.addRole(userRole);

        this.userRepository.saveAndFlush(user);

        notifyService.addInfoMessage(Messages.SUCCESSFULLY_CREATED_USER);
        if(this.isCurrentUserAdmin()){
            return "redirect:/all_users";
        }

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("view", "user/login");

        return "base-layout";
    }

//    @GetMapping ("/login?error")
//    public String loginFailure(@PathVariable final String error) {
//
//        this.notifyService.addErrorMessage(error);
//        return "redirect:/login";
//    }

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
            this.notifyService.addErrorMessage(Messages.YOU_HAVE_NO_PERMISSION);
            return "redirect:/login";
        }
        if (!this.userRepository.exists(id)) {
            this.notifyService.addErrorMessage(Messages.NOT_FOUND);
            return "redirect:/";
        }
        User user = this.userRepository.findOne(id);
        boolean isAdmin = user.isAdmin();
        model.addAttribute("isAdmin", isAdmin);

        model.addAttribute("view", "user/edit")
                .addAttribute("user", user);
        return "admin/admin_panel-layout";
    }

    @PostMapping("/user/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String editAction(UserBindingModel userBindingModel, @PathVariable Integer id,
                             @RequestParam(defaultValue = "false") boolean checkbox) {

        if (!this.userRepository.exists(id)) {
            this.notifyService.addErrorMessage(Messages.NOT_FOUND);
            return "redirect:/all_users";
        }

        if (!userBindingModel.getPassword().equals(userBindingModel.getConfirmPassword())) {
            this.notifyService.addErrorMessage(Messages.THE_PASSWORD_DOESN_T_MATCH);
            return "redirect:/user/edit/" + id;
        }
        User user = this.userRepository.findOne(id);

        //only first registered admin can edit admin profile
        if(user.isAdmin() && this.getCurrentUser().getId() != 1){
            this.notifyService.addErrorMessage(Messages.YOU_HAVE_NO_PERMISSION);
            return "redirect:/user/edit/" + id;
        }


        if (!StringUtils.isEmpty(userBindingModel.getPassword())
                && !StringUtils.isEmpty(userBindingModel.getConfirmPassword())) {

            if (userBindingModel.getPassword().equals(userBindingModel.getConfirmPassword())) {
                BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

                user.setPassword(bCryptPasswordEncoder.encode(userBindingModel.getPassword()));
            }
        }

        user.setFullName(userBindingModel.getFullName());

        if(checkbox && !user.isAdmin()){
            user.addRole(this.roleRepository.findByName("ROLE_ADMIN"));

        }else if(!checkbox && user.isAdmin()){
            user.deleteRole(this.roleRepository.findByName("ROLE_ADMIN"));
        }
        this.userRepository.saveAndFlush(user);
        notifyService.addInfoMessage(Messages.SUCCESSFULLY_EDITED_USER);

        if(Objects.equals(this.getCurrentUser().getId(), user.getId())){
            return "redirect:/login?logout";

        }
        return "redirect:/all_users";
    }

//    private boolean hasRights(User browsingUser) {
//        User currentLoggedInUser = getUser();
//        return browsingUser != null && (browsingUser.getEmail().equals(currentLoggedInUser.getEmail()) || currentLoggedInUser.getRoles().stream()
//                .map(Role::getName).collect(Collectors.toList()).contains("ROLE_ADMIN"));
//
//    }
//    private User getUser() {
//        UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
//                .getAuthentication()
//                .getPrincipal();
//        return this.userRepository.findByEmail(principal.getUsername());
//    }
    @GetMapping("/user/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String delete(Model model, @PathVariable Integer id) {

        if(!this.isCurrentUserAdmin()){
            this.notifyService.addErrorMessage(Messages.YOU_HAVE_NO_PERMISSION);
            return "redirect:/login?logout";
        }

        if (!this.userRepository.exists(id)) {
            this.notifyService.addErrorMessage(Messages.NOT_FOUND);
            return "redirect:/";
        }
        User user = this.userRepository.findOne(id);

        model.addAttribute("view", "user/delete")
                .addAttribute("user", user);
        return "admin/admin_panel-layout";
    }

    @PostMapping("/user/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String deleteAction(@PathVariable Integer id) {

        if (!this.userRepository.exists(id)) {
            this.notifyService.addErrorMessage(Messages.NOT_FOUND);
            return "redirect:/";
        }

        User user = this.userRepository.findOne(id);

        //only first registered admin can edit admin profile
        if(user.isAdmin() && this.getCurrentUser().getId() != 1){

            this.notifyService.addErrorMessage(Messages.YOU_HAVE_NO_PERMISSION);
            return "redirect:/user/delete/" + id;
        }

        //can't delete own profile
        if(Objects.equals(this.getCurrentUser().getId(), user.getId())){
            this.notifyService.addErrorMessage(Messages.YOU_CAN_NOT_DELETE_YOUR_PROFILE);
            return "redirect:/user/delete/" + id;
        }

        this.userRepository.delete(id);
        notifyService.addInfoMessage(Messages.SUCCESSFULLY_DELETED_USER);
        return "redirect:/all_users";

    }

    private boolean isCurrentUserAdmin(){
       return this.getCurrentUser() != null && this.getCurrentUser().isAdmin();
    }

    private User getCurrentUser(){

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

