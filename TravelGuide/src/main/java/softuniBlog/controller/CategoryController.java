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
import softuniBlog.entity.Image;
import softuniBlog.entity.User;
import softuniBlog.repository.ArticleRepository;
import softuniBlog.repository.CategoryRepository;
import softuniBlog.repository.DestinationRepository;
import softuniBlog.repository.UserRepository;
import softuniBlog.service.NotificationService;
import softuniBlog.utils.Messages;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Controller
public class CategoryController {

    private final CategoryRepository categoryRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final DestinationRepository destinationRepository;
    private final NotificationService notifyService;

    @Autowired
    public CategoryController(CategoryRepository categoryRepository, ArticleRepository articleRepository, UserRepository userRepository, DestinationRepository destinationRepository, NotificationService notifyService) {
        this.categoryRepository = categoryRepository;
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
        this.destinationRepository = destinationRepository;
        this.notifyService = notifyService;
    }

    @GetMapping("/category/{id}")
    public String details(Model model, @PathVariable Integer id) {
        if (!this.categoryRepository.exists(id)) {
            this.notifyService.addErrorMessage(Messages.NOT_FOUND);
            return "redirect:/";
        }
        Category category = this.categoryRepository.findOne(id);
        List<Category> categories = this.categoryRepository.findAll();
//        model.addAttribute("category", category);
        model.addAttribute("categories", categories);
        model.addAttribute("destinations", category.getDestinations());

        model.addAttribute("view", "home/index");
        return "base-layout";
    }

    @GetMapping("/category/addCategory")
    @PreAuthorize("isAuthenticated()")
    public String addCategory(Model model) {
        if (!this.isCurrentUserAdmin()) {
            this.notifyService.addErrorMessage(Messages.YOU_HAVE_NO_PERMISSION);
            return "redirect:/login";
        }
        model.addAttribute("view", "category/add_category");
        return "admin/admin_panel-layout";
    }

    @PostMapping("/category/addCategory")
    @PreAuthorize("isAuthenticated()")
    public String addCategoryProcess(Model model, CategoryBindingModel categoryBindingModel) {
        if (!this.isCurrentUserAdmin()) {
            this.notifyService.addErrorMessage(Messages.YOU_HAVE_NO_PERMISSION);
            return "redirect:/login";
        }

        User currentUser = this.getCurrentUser();
        this.categoryRepository.saveAndFlush(new Category(categoryBindingModel.getName(), currentUser));
        this.notifyService.addInfoMessage(Messages.SUCCESSFULLY_CREATED_CATEGORY);
        return "redirect:/all_categories";
    }

    @GetMapping("/all_categories")
    @PreAuthorize("isAuthenticated()")
    public String categories(Model model) {

        if (this.isCurrentUserAdmin()) {
            List<Category> allCategories = this.categoryRepository.findAll();
            model.addAttribute("view", "category/all_categories");
            model.addAttribute("categories", allCategories);
            return "admin/admin_panel-layout";
        }

        this.notifyService.addErrorMessage(Messages.YOU_HAVE_NO_PERMISSION);
        return "redirect:/login";
    }

    @GetMapping("/category/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String editCategory(Model model, @PathVariable Integer id) {

        if (!this.isCurrentUserAdmin()) {
            this.notifyService.addErrorMessage(Messages.YOU_HAVE_NO_PERMISSION);
            return "redirect:/login";
        }

        if (!this.categoryRepository.exists(id)) {
            this.notifyService.addErrorMessage(Messages.NOT_FOUND);
            return "redirect:/";
        }

        Category category = this.categoryRepository.findOne(id);

        model.addAttribute("view", "category/edit")
                .addAttribute("category", category);

        return "admin/admin_panel-layout";
    }

    @PostMapping("/category/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String editCategoryAction(CategoryBindingModel categoryBindingModel, @PathVariable Integer id) {

        if (!this.isCurrentUserAdmin()) {
            this.notifyService.addErrorMessage(Messages.YOU_HAVE_NO_PERMISSION);
            return "redirect:/login";
        }

        if (!this.categoryRepository.exists(id)) {
            this.notifyService.addErrorMessage(Messages.NOT_FOUND);
            return "redirect:/";
        }

        Category category = this.categoryRepository.findOne(id);

        category.setName(categoryBindingModel.getName());

        this.categoryRepository.saveAndFlush(category);
        this.notifyService.addInfoMessage(Messages.SUCCESSFULLY_EDITED_CATEGORY);
        return "redirect:/all_categories";

    }

    @GetMapping("/category/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String deleteCategory(Model model, @PathVariable Integer id) {

        if (!this.isCurrentUserAdmin()) {
            this.notifyService.addErrorMessage(Messages.YOU_HAVE_NO_PERMISSION);
            return "redirect:/login";
        }

        if (!this.categoryRepository.exists(id)) {
            this.notifyService.addErrorMessage(Messages.NOT_FOUND);
            return "redirect:/";
        }

        Category category = this.categoryRepository.findOne(id);

        model.addAttribute("view", "category/delete")
                .addAttribute("category", category);
        return "admin/admin_panel-layout";
    }

    @PostMapping("/category/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String deleteCategoryAction(@PathVariable Integer id) {

        if (!this.isCurrentUserAdmin()) {
            this.notifyService.addErrorMessage(Messages.YOU_HAVE_NO_PERMISSION);
            return "redirect:/login";
        }

        if (!this.categoryRepository.exists(id)) {
            this.notifyService.addErrorMessage(Messages.NOT_FOUND);
            return "redirect:/";
        }

        /*TODO: done it cascade, test if it works
        List<Destination> destinationToDelete = this.destinationRepository.findAll().stream()
                .filter(destination -> destination.getCategory().getId().equals(id))
                .collect(Collectors.toList());
        delete all articles and destinations with given category
        for (Destination destination : destinationToDelete) {
            this.deleteDestination(destination.getId());
        }*/

        Set<Image> imagesToDelete = new HashSet<>();
        this.categoryRepository.findOne(id).getDestinations().forEach(d -> imagesToDelete.addAll(d.getImages()));
        DestinationController.deleteImagesFromDisk(imagesToDelete);
        this.categoryRepository.delete(id);
        this.notifyService.addInfoMessage(Messages.SUCCESSFULLY_DELETED_CATEGORY);
        return "redirect:/all_categories";

    }

    /*private void deleteDestination(Integer id) {
        List<Article> articlesToDelete = this.articleRepository.findAll().stream()
                .filter(destination -> destination.getDestination().getId().equals(id))
                .collect(Collectors.toList());
        this.articleRepository.delete(articlesToDelete);
        this.destinationRepository.delete(id);
    }*/

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
