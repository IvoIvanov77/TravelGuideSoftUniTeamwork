package softuniBlog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import softuniBlog.bindingModel.DestinationBindingModel;
import softuniBlog.entity.*;
import softuniBlog.repository.*;
import softuniBlog.service.NotificationService;
import softuniBlog.utils.Constants;
import softuniBlog.utils.Messages;
import softuniBlog.utils.UploadImage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static softuniBlog.utils.Constants.EMTPY_STRING;


@Controller
public class DestinationController {

    private final DestinationRepository destinationRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final NotificationService notifyService;
    private final ArticleRepository articleRepository;
//    private ImageRepository imageRepository;

    @Autowired
    public DestinationController(DestinationRepository destinationRepository, UserRepository userRepository,
                                 /*ImageRepository imageRepository,*/ CategoryRepository categoryRepository,
                                 NotificationService notifyService, ArticleRepository articleRepository) {
        this.destinationRepository = destinationRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.notifyService = notifyService;
        this.articleRepository = articleRepository;
//        this.imageRepository = imageRepository;
    }

    @GetMapping("/destination/add")
    @PreAuthorize("isAuthenticated()")
    public String createDestination(Model model) {
        if (!this.isCurrentUserAdmin()) {
            this.notifyService.addErrorMessage(Messages.YOU_HAVE_NO_PERMISSION);
            return "redirect:/login";
        }
        if (this.categoryRepository.findAll().isEmpty()) {
            this.notifyService.addErrorMessage(Messages.THERE_ARE_NO_CATEGORIES_AVAILABLE);
        }
        model.addAttribute("view", "destination/create");
        model.addAttribute("categories", this.categoryRepository.findAll());
        return "admin/admin_panel-layout";
    }

    @PostMapping("/destination/add")
    @PreAuthorize("isAuthenticated()")
    public String createDestinationProcess(DestinationBindingModel destinationBindingModel) {
        if (!this.isCurrentUserAdmin()) {
            this.notifyService.addErrorMessage(Messages.YOU_HAVE_NO_PERMISSION);
            return "redirect:/login";
        }

        User currentUser = this.getCurrentUser();

        Set<MultipartFile> files = destinationBindingModel.getPictures();
        files.add(destinationBindingModel.getPicture());

        Category category = this.categoryRepository.findOne(Math.toIntExact(destinationBindingModel.getCategoryId()));
        Destination destination = new Destination(destinationBindingModel.getName(),
                destinationBindingModel.getReview(), currentUser, category, destinationBindingModel.getPrice());
        Set<Image> images = this.setImagesToDestination(files, destination);
        destination.setImages(images);

        this.destinationRepository.saveAndFlush(destination);
        this.notifyService.addInfoMessage(Messages.SUCCESSFULLY_CREATED_DESTINATION);
        return "redirect:/all_destinations";
    }

    private Set<Image> setImagesToDestination(Set<MultipartFile> files, Destination destination) {
        Set<Image> images = new HashSet<>();
        files.stream().filter(x -> !x.getOriginalFilename().equals(EMTPY_STRING))
                .forEach(file -> {
                    String path = UploadImage.upload(Constants.IMG_WIDTH, Constants.IMG_HEIGHT, file);
                    images.add(new Image(path, destination));
                });
        return images;
    }

    @GetMapping("/destination/{id}")
    public String destinationDetails(Model model, @PathVariable Integer id) {

        if (!this.destinationRepository.exists(id)) {
            this.notifyService.addErrorMessage(Messages.NOT_FOUND);
            return "redirect:/";
        }

        Destination destination = this.destinationRepository.findOne(id);
        Set<Article> articles = destination.getArticles();
        model.addAttribute("view", "destination/details")
                .addAttribute("destination", destination)
                .addAttribute("articles", articles);
        return "base-layout";
    }

    @GetMapping("/destination/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String edit(Model model, @PathVariable Integer id) {

        if (!this.isCurrentUserAdmin()) {
            this.notifyService.addErrorMessage(Messages.YOU_HAVE_NO_PERMISSION);
            return "redirect:/login";
        }

        if (!this.destinationRepository.exists(id)) {
            this.notifyService.addErrorMessage(Messages.NOT_FOUND);
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
    public String editAction(DestinationBindingModel bindingModel, @PathVariable Integer id) {
        if (!this.isCurrentUserAdmin()) {
            this.notifyService.addErrorMessage(Messages.YOU_HAVE_NO_PERMISSION);
            return "redirect:/login";
        }

        if (!this.destinationRepository.exists(id)) {
            this.notifyService.addErrorMessage(Messages.NOT_FOUND);
            return "redirect:/all_destinations";
        }

        Destination destination = this.destinationRepository.findOne(id);

        destination.setName(bindingModel.getName());
        destination.setReview(bindingModel.getReview());
        destination.setCategory(this.categoryRepository.findOne(bindingModel.getCategoryId()));
        this.destinationRepository.saveAndFlush(destination);
        this.notifyService.addInfoMessage(Messages.SUCCESSFULLY_EDITED_DESTINATION);

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

        this.notifyService.addInfoMessage(Messages.YOU_HAVE_NO_PERMISSION);
        return "redirect:/login";
    }

    @GetMapping("/destination/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String delete(Model model, @PathVariable Integer id) {

        if (!this.isCurrentUserAdmin()) {
            this.notifyService.addErrorMessage(Messages.YOU_HAVE_NO_PERMISSION);
            return "redirect:/login";
        }

        if (!this.destinationRepository.exists(id)) {
            this.notifyService.addErrorMessage(Messages.NOT_FOUND);
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
            this.notifyService.addErrorMessage(Messages.YOU_HAVE_NO_PERMISSION);
            return "redirect:/login";
        }

        if (!this.destinationRepository.exists(id)) {
            this.notifyService.addErrorMessage(Messages.NOT_FOUND);
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
        this.notifyService.addInfoMessage(Messages.SUCCESSFULLY_DELETED_DESTINATION);
        return "redirect:/all_destinations";

    }

//    private boolean isUserAuthorOrAdmin(Destination destination) {
//        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        User userEntity = this.userRepository.findByEmail(user.getUsername());
//
//        return userEntity.isAuthor(destination) || userEntity.isAdmin();
//    }

    private boolean isCurrentUserAdmin() {
        return this.getCurrentUser() != null && this.getCurrentUser().isAdmin();
    }

    private User getCurrentUser() {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        return this.userRepository.findByEmail(principal.getUsername());
    }
}
