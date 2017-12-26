package softuniBlog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import softuniBlog.bindingModel.DestinationBindingModel;
import softuniBlog.entity.Category;
import softuniBlog.entity.Destination;
import softuniBlog.entity.User;
import softuniBlog.repository.CategoryRepository;
import softuniBlog.repository.DestinationRepository;
import softuniBlog.repository.UserRepository;

/**
 * Created by George-Lenovo on 6/29/2017.
 */
@Controller
public class DestinationController {
    private DestinationRepository destinationRepository;
    private UserRepository userRepository;
    private CategoryRepository categoryRepository;

    @Autowired
    public DestinationController(DestinationRepository destinationRepository, UserRepository userRepository, CategoryRepository categoryRepository) {
        this.destinationRepository = destinationRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/destination/add")
    public String createDestination(Model model) {
        model.addAttribute("view", "destination/create");
        model.addAttribute("categories", this.categoryRepository.findAll());
        return "base-layout";
    }

    @PostMapping("/destination/add")
    @PreAuthorize("isAuthenticated()")
    public String createDestinationProcess(DestinationBindingModel destinationBindingModel) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        User currentUser = this.userRepository.findByEmail(principal.getUsername());
        Category category = this.categoryRepository.findOne(Math.toIntExact(destinationBindingModel.getCategoryId()));
        this.destinationRepository.saveAndFlush(new Destination(destinationBindingModel.getDestinationName(),
                destinationBindingModel.getReview(), currentUser,category));
        return "redirect:/";
    }

}
