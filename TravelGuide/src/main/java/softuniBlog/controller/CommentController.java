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


@Controller
public class CommentController {
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final NotificationService notifyService;
    private final CommentRepository commentRepository;

    @Autowired
    public CommentController(ArticleRepository articleRepository, UserRepository userRepository, DestinationRepository destinationRepository, NotificationService notifyService, CommentRepository commentRepository) {
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
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
        this.notifyService.addInfoMessage(Messages.SUCCESSFULLY_CREATED_COMMENT);
        return "redirect:/";
    }

    @GetMapping("/comment/{id}")
    public String details(Model model, @PathVariable Integer id){
        if(!this.commentRepository.exists(id)){
            this.notifyService.addErrorMessage(Messages.NOT_FOUND);
            return "redirect:/";
        }
        Comment comment = this.commentRepository.findOne(id);
        model.addAttribute("view", "comment/details")
                .addAttribute("comment", comment);
        return "base-layout";
    }

    @GetMapping("/comment/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String edit(Model model, @PathVariable Integer id){
        if(!this.commentRepository.exists(id)){
            this.notifyService.addErrorMessage(Messages.NOT_FOUND);
            return "redirect:/";
        }
        Comment comment = this.commentRepository.findOne(id);
        if(this.getCurrentUser() != comment.getAuthor() && !isCurrentUserAdmin()){
            this.notifyService.addErrorMessage(Messages.YOU_HAVE_NO_PERMISSION);
            return "redirect:/";
        }
        model.addAttribute("view", "comment/edit");
        model.addAttribute("comment", comment);
        model.addAttribute("articles", this.articleRepository.findAll());
        return "base-layout";
    }

    @PostMapping("/comment/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String editProcess(CommentBindingModel commentBindingModel, @PathVariable Integer id){
        if(!this.commentRepository.exists(id)){
            this.notifyService.addErrorMessage(Messages.NOT_FOUND);
            return "redirect:/";
        }
        Comment comment = this.commentRepository.findOne(id);
        if(this.getCurrentUser() != comment.getAuthor() && !isCurrentUserAdmin()){
             this.notifyService.addErrorMessage(Messages.YOU_HAVE_NO_PERMISSION);
             return "redirect:/";
        }
        comment.setArticle(this.articleRepository.findOne(commentBindingModel.getArticleId()));
        comment.setContent(commentBindingModel.getContent());
        comment.setTitle(commentBindingModel.getTitle());
        this.commentRepository.saveAndFlush(comment);
        return "redirect:/";
    }

    @GetMapping("/comment/listAll")
    public String listAll(Model model){
        model.addAttribute("view", "comment/all_comments");
        model.addAttribute("comments", this.commentRepository.findAll());
        return "base-layout";
    }

    @GetMapping("/comment/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String delete(Model model, @PathVariable Integer id){
        if(!this.commentRepository.exists(id)){
            this.notifyService.addErrorMessage(Messages.NOT_FOUND);
            return "redirect:/";
        }
        Comment comment = this.commentRepository.findOne(id);
        if(this.getCurrentUser() != comment.getAuthor() && !isCurrentUserAdmin()){
            this.notifyService.addErrorMessage(Messages.YOU_HAVE_NO_PERMISSION);
            return "redirect:/";
        }
        model.addAttribute("view", "comment/delete");
        model.addAttribute("comment", comment);
        return "base-layout";
    }

    @PostMapping("/comment/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String deleteProcess(@PathVariable Integer id){
        if(!this.commentRepository.exists(id)){
            this.notifyService.addErrorMessage(Messages.NOT_FOUND);
            return "redirect:/";
        }
        Comment comment = this.commentRepository.findOne(id);
        if(this.getCurrentUser() != comment.getAuthor() && !isCurrentUserAdmin()){
            this.notifyService.addErrorMessage(Messages.YOU_HAVE_NO_PERMISSION);
            return "redirect:/";
        }
        this.commentRepository.delete(comment);
        this.commentRepository.flush();
        return "redirect:/comment/listAll";
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

    private boolean isCurrentUserAdmin() {
        return this.getCurrentUser() != null && this.getCurrentUser().isAdmin();
    }
}
