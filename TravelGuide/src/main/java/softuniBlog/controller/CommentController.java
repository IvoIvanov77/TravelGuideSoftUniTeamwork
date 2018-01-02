package softuniBlog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import softuniBlog.bindingModel.CommentBindingModel;
import softuniBlog.entity.Article;
import softuniBlog.entity.Comment;
import softuniBlog.entity.User;
import softuniBlog.repository.ArticleRepository;
import softuniBlog.repository.CommentRepository;
import softuniBlog.repository.DestinationRepository;
import softuniBlog.repository.UserRepository;
import softuniBlog.service.NotificationService;
import softuniBlog.utils.Messages;

//TODO
@Controller
public class CommentController {
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final DestinationRepository destinationRepository;
    private final NotificationService notifyService;
    private final CommentRepository commentRepository;

    @Autowired
    public CommentController(ArticleRepository articleRepository, UserRepository userRepository, DestinationRepository destinationRepository, NotificationService notifyService, CommentRepository commentRepository) {
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
        this.destinationRepository = destinationRepository;
        this.notifyService = notifyService;
        this.commentRepository = commentRepository;
    }

    @GetMapping("/comment/create")
    @PreAuthorize("isAuthenticated()")
    public String create(Model model){
        if(this.articleRepository.findAll().isEmpty()){
            this.notifyService.addErrorMessage(Messages.THERE_ARE_NO_ARTICLES_AVAILABLE);
        }
        model.addAttribute("view", "comment/create");
        model.addAttribute("articles", this.articleRepository.findAll());
        return "base-layout";
    }

    @PostMapping("/comment/create")
    @PreAuthorize("isAuthenticated()")
    public String createProcess(CommentBindingModel bindingModel){
        User author = this.getCurrentUser();
        Article article = this.articleRepository.findOne(bindingModel.getArticleId());
        Comment comment = new Comment(bindingModel.getTitle(), bindingModel.getContent(), author, article);
        this.commentRepository.saveAndFlush(comment);
        return "redirect:/";
    }

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
