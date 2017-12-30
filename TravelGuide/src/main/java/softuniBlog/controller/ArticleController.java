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
import softuniBlog.bindingModel.ArticleBindingModel;
import softuniBlog.entity.Article;
import softuniBlog.entity.Destination;
import softuniBlog.entity.User;
import softuniBlog.repository.ArticleRepository;
import softuniBlog.repository.DestinationRepository;
import softuniBlog.repository.UserRepository;
import softuniBlog.service.NotificationService;
import softuniBlog.utils.Messages;

import java.util.HashSet;
import java.util.List;

@Controller
public class ArticleController {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final DestinationRepository destinationRepository;
    private final NotificationService notifyService;

    @Autowired
    public ArticleController(ArticleRepository articleRepository, UserRepository userRepository, DestinationRepository destinationRepo, NotificationService notifyService) {
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
        this.destinationRepository= destinationRepo;
        this.notifyService = notifyService;
    }

    @GetMapping("/article/create")
    @PreAuthorize("isAuthenticated()")
    public String create(Model model) {
        if (!this.isCurrentUserAdmin()) {
            this.notifyService.addErrorMessage(Messages.ERROR);
            return "redirect:/login";
        }

        if(this.destinationRepository.findAll().isEmpty()){
            this.notifyService.addErrorMessage("there are no destination available");
        }
        model.addAttribute("view", "article/create");
        model.addAttribute("destinations", this.destinationRepository.findAll());
        return "admin/admin_panel-layout";
    }

    @PostMapping("/article/create")
    @PreAuthorize("isAuthenticated()")
    public String createAction(ArticleBindingModel articleBindingModel) {

        if (!this.isCurrentUserAdmin()) {
            this.notifyService.addErrorMessage(Messages.ERROR);
            return "redirect:/login";
        }
        User user = this.getCurrentUser();

        Destination destination = this.destinationRepository.findOne(articleBindingModel.getDestinationId());

        Article article = new Article(articleBindingModel.getTitle(),
                articleBindingModel.getContent(),
                user,destination, new HashSet<>());

        this.articleRepository.saveAndFlush(article);
        return "redirect:/all_articles";
    }

    @GetMapping("/article/{id}")
    public String details(Model model, @PathVariable Integer id) {

        if (!this.articleRepository.exists(id)) {
            return "redirect:/";
        }

        Article article = this.articleRepository.findOne(id);
        model.addAttribute("view", "article/details")
                .addAttribute("article", article);
        return "base-layout";
    }

    @GetMapping("/article/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String edit(Model model, @PathVariable Integer id) {

        if (!this.isCurrentUserAdmin()) {
            this.notifyService.addErrorMessage("ERROR");
            return "redirect:/login";
        }

        if (!this.articleRepository.exists(id)) {
            return "redirect:/";
        }

        Article article = this.articleRepository.findOne(id);

        model.addAttribute("view", "article/edit")
                .addAttribute("article", article);
        model.addAttribute("destinations", this.destinationRepository.findAll());
        return "admin/admin_panel-layout";
    }

    @PostMapping("/article/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String editAction(ArticleBindingModel articleBindingModel, @PathVariable Integer id) {

        if (!this.isCurrentUserAdmin()) {
            this.notifyService.addErrorMessage("ERROR");
            return "redirect:/login";
        }

        if (!this.articleRepository.exists(id)) {
            return "redirect:/";
        }

        Article article = this.articleRepository.findOne(id);

        article.setTitle(articleBindingModel.getTitle());
        article.setContent(articleBindingModel.getContent());
        Destination destination = this.destinationRepository.findOne(articleBindingModel.getDestinationId());
        article.setDestination(destination);
        this.articleRepository.saveAndFlush(article);
        return "redirect:/article/" + article.getId();

    }

    @GetMapping("/article/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String delete(Model model, @PathVariable Integer id) {

        if (!this.isCurrentUserAdmin()) {
            this.notifyService.addErrorMessage("ERROR");
            return "redirect:/login";
        }

        if (!this.articleRepository.exists(id)) {
            return "redirect:/";
        }

        Article article = this.articleRepository.findOne(id);

        model.addAttribute("view", "article/delete")
                .addAttribute("article", article);
        return "admin/admin_panel-layout";
    }

    @PostMapping("/article/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String deleteAction(@PathVariable Integer id) {

        if (!this.isCurrentUserAdmin()) {
            this.notifyService.addErrorMessage("ERROR");
            return "redirect:/login";
        }

        if (!this.articleRepository.exists(id)) {
            return "redirect:/";       }


        this.articleRepository.delete(id);
        return "redirect:/all_articles";

    }

    @GetMapping("/all_articles")
    @PreAuthorize("isAuthenticated()")
    public String categories(Model model) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        User user = this.userRepository.findByEmail(principal.getUsername());
        model.addAttribute("user", user);
        if (user.isAdmin()) {
            List<Article> allArticles = this.articleRepository.findAll();
            model.addAttribute("view", "article/all_articles");
            model.addAttribute("articles", allArticles);
            return "admin/admin_panel-layout";
        }
        //// TODO: 12/30/2017
        this.notifyService.addInfoMessage(Messages.ERROR);
        return "redirect:/login";
    }

    private boolean isUserAuthorOrAdmin(Article article) {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User userEntity = this.userRepository.findByEmail(user.getUsername());

        return userEntity.isAuthor(article) || userEntity.isAdmin();
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
