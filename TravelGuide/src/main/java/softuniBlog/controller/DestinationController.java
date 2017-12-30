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
import softuniBlog.bindingModel.DestinationBindingModel;
import softuniBlog.entity.Article;
import softuniBlog.entity.Category;
import softuniBlog.entity.Destination;
import softuniBlog.entity.User;
import softuniBlog.repository.ArticleRepository;
import softuniBlog.repository.CategoryRepository;
import softuniBlog.repository.DestinationRepository;
import softuniBlog.repository.UserRepository;
import softuniBlog.service.NotificationService;
import softuniBlog.utils.Messages;

import java.util.List;
import java.util.stream.Collectors;


@Controller
public class DestinationController {
    
    private final DestinationRepository destinationRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final NotificationService notifyService;
    private final ArticleRepository articleRepository;

    @Autowired
    public DestinationController(DestinationRepository destinationRepository, UserRepository userRepository, CategoryRepository categoryRepository, NotificationService notifyService, ArticleRepository articleRepository) {
        this.destinationRepository = destinationRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.notifyService = notifyService;
        this.articleRepository = articleRepository;
    }

    @GetMapping("/destination/add")
    public String createDestination(Model model) {
        if (!this.isCurrentUserAdmin()) {
            this.notifyService.addInfoMessage(Messages.ERROR);
            return "redirect:/login";
        }
        if(this.categoryRepository.findAll().isEmpty()){
            this.notifyService.addErrorMessage("there are no categories available");
        }
        model.addAttribute("view", "destination/create");
        model.addAttribute("categories", this.categoryRepository.findAll());
        return "admin/admin_panel-layout";
    }

    @PostMapping("/destination/add")
    @PreAuthorize("isAuthenticated()")
    public String createDestinationProcess(DestinationBindingModel destinationBindingModel) {
        if (!this.isCurrentUserAdmin()) {
            this.notifyService.addErrorMessage(Messages.ERROR);
            return "redirect:/login";
        }

        User currentUser = this.getCurrentUser();

        Category category = this.categoryRepository.findOne(Math.toIntExact(destinationBindingModel.getCategoryId()));
        this.destinationRepository.saveAndFlush(new Destination(destinationBindingModel.getName(),
                destinationBindingModel.getReview(), currentUser, category, destinationBindingModel.getPrice()));
        return "redirect:/all_destinations";
    }

    @GetMapping("/destination/{id}")
    public String destinationDetails(Model model, @PathVariable Integer id) {
        if (!this.destinationRepository.exists(id)) {
            return "redirect:/";
        }
        if (!(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)) {
            UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();

            User user = this.userRepository.findByEmail(principal.getUsername());
            model.addAttribute("user", user);
        }
        Destination destination = this.destinationRepository.findOne(id);
        model.addAttribute("view", "destination/details")
                .addAttribute("destination", destination);
        return "base-layout";
    }

    @GetMapping("/destination/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String edit(Model model, @PathVariable Integer id) {

        if (!this.isCurrentUserAdmin()) {
            this.notifyService.addInfoMessage(Messages.ERROR);
            return "redirect:/login";
        }

        if (!this.destinationRepository.exists(id)) {
            return "redirect:/all_destinations";
        }
        Destination destination = this.destinationRepository.findOne(id);

        model.addAttribute("view", "destination/edit")
                .addAttribute("destination", destination);
        model.addAttribute("categories", this.categoryRepository.findAll());
        return "admin/admin_panel-layout";
    }

    @PostMapping("/destination/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String editAction(DestinationBindingModel bindingModel, @PathVariable Integer id){
        if (!this.isCurrentUserAdmin()) {
            this.notifyService.addInfoMessage(Messages.ERROR);
            return "redirect:/login";
        }

        if (!this.destinationRepository.exists(id)) {
            this.notifyService.addInfoMessage(Messages.ERROR);
            return "redirect:/all_destinations";
        }

        Destination destination = this.destinationRepository.findOne(id);

        destination.setName(bindingModel.getName());
        destination.setReview(bindingModel.getReview());
        destination.setCategory(this.categoryRepository.findOne(bindingModel.getCategoryId()));
        this.destinationRepository.saveAndFlush(destination);

        return "redirect:/all_destinations";
    }

    @GetMapping("/all_destinations")
    @PreAuthorize("isAuthenticated()")
    public String categories(Model model) {

        if (this.isCurrentUserAdmin()) {
            List<Destination> allDestinations = this.destinationRepository.findAll();
            model.addAttribute("view", "destination/all_destinations");
            model.addAttribute("destinations", allDestinations);
            return "admin/admin_panel-layout";
        }
        //// TODO: 12/30/2017
        this.notifyService.addInfoMessage(Messages.ERROR);
        return "redirect:/login";
    }

    @GetMapping("/destination/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String delete(Model model, @PathVariable Integer id) {

        if (!this.isCurrentUserAdmin()) {
            this.notifyService.addErrorMessage("ERROR");
            return "redirect:/login";
        }

        if (!this.destinationRepository.exists(id)) {
            return "redirect:/";
        }

        Destination destination = this.destinationRepository.findOne(id);

        model.addAttribute("view", "destination/delete")
                .addAttribute("destination", destination);
        return "admin/admin_panel-layout";
    }

    @PostMapping("/destination/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String deleteAction(@PathVariable Integer id) {

        if (!this.isCurrentUserAdmin()) {
            this.notifyService.addErrorMessage("ERROR");
            return "redirect:/login";
        }

        if (!this.destinationRepository.exists(id)) {
            return "redirect:/";
        }

        //remove all articles with given destination
        this.articleRepository.delete(
                this.articleRepository.findAll()
                        .stream()
                        .filter(article -> article.getDestination().getId().equals(id))
                        .collect(Collectors.toList())
        );

        this.destinationRepository.delete(id);
        return "redirect:/all_destinations";

    }

    private boolean isUserAuthorOrAdmin(Destination destination) {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User userEntity = this.userRepository.findByEmail(user.getUsername());

        return userEntity.isAuthor(destination) || userEntity.isAdmin();
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
