package softuniBlog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import softuniBlog.bindingModel.ArticleBindingModel;
import softuniBlog.entity.Article;
import softuniBlog.entity.User;
import softuniBlog.repository.ArticleRepository;
import softuniBlog.repository.UserRepository;

@Controller
public class ArticleController {

    private final
    ArticleRepository articleRepository;
    private final
    UserRepository userRepository;

    @Autowired
    public ArticleController(ArticleRepository articleRepository, UserRepository userRepository) {
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/article/create")
    @PreAuthorize("isAuthenticated()")
    public String create(Model model){
        model.addAttribute("view", "article/create");
        return "base-layout";
    }

    @PostMapping("/article/create")
    @PreAuthorize("isAuthenticated()")
    public String createAction(ArticleBindingModel articleBindingModel){

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        User user = this.userRepository.findByEmail(principal.getUsername());

        Article article = new Article(articleBindingModel.getTitle(),
                articleBindingModel.getContent(),
                user);

        this.articleRepository.saveAndFlush(article);
        return "redirect:/";
    }

    @GetMapping("/article/{id}")
    public String details(Model model, @PathVariable Integer id){

        if(!this.articleRepository.exists(id)){
            return "redirect:/";
        }
        if(!(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)){
            UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();

            User user = this.userRepository.findByEmail(principal.getUsername());
            model.addAttribute("user", user);
        }
        Article article = this.articleRepository.findOne(id);
        model.addAttribute("view", "article/details")
                .addAttribute("article", article);
        return "base-layout";
    }

    @GetMapping("/article/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String edit(Model model, @PathVariable Integer id){

        if(!this.articleRepository.exists(id)){
            return "redirect:/";
        }
        Article article = this.articleRepository.findOne(id);

        if(this.isUserAuthorOrAdmin(article)){
            return "redirect:/article/" + id;
        }

        model.addAttribute("view", "article/edit")
                .addAttribute("article", article);
        return "base-layout";
    }

    @PostMapping("/article/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String editAction(ArticleBindingModel articleBindingModel, @PathVariable Integer id){

        if(!this.articleRepository.exists(id)){
            return "redirect:/";
        }

        Article article = this.articleRepository.findOne(id);

        if(this.isUserAuthorOrAdmin(article)){
            return "redirect:/article/" + id;
        }

        article.setTitle(articleBindingModel.getTitle());
        article.setContent(articleBindingModel.getContent());


        this.articleRepository.saveAndFlush(article);
        return "redirect:/article/" + article.getId();

    }

    @GetMapping("/article/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String delete(Model model, @PathVariable Integer id){

        if(!this.articleRepository.exists(id)){
            return "redirect:/";
        }
        Article article = this.articleRepository.findOne(id);

        if(this.isUserAuthorOrAdmin(article)){
            return "redirect:/article/" + id;
        }

        model.addAttribute("view", "article/delete")
                .addAttribute("article", article);
        return "base-layout";
    }

    @PostMapping("/article/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String deleteAction(@PathVariable Integer id){

        if(!this.articleRepository.exists(id)){
            return "redirect:/";
        }

        Article article = this.articleRepository.findOne(id);

        if(this.isUserAuthorOrAdmin(article)){
            return "redirect:/article/" + id;
        }


        this.articleRepository.delete(id);
        return "redirect:/";

    }

    private boolean isUserAuthorOrAdmin(Article article){
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User userEntity = this.userRepository.findByEmail(user.getUsername());

        return userEntity.isAuthor(article) || userEntity.isAdmin();
    }


}
