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
import softuniBlog.bindingModel.ArticleBindingModel;
import softuniBlog.entity.Article;
import softuniBlog.entity.User;
import softuniBlog.repository.ArticleRepository;
import softuniBlog.repository.UserRepository;

@Controller
public class ArticleController {

    @Autowired
    ArticleRepository articleRepository;
    @Autowired
    UserRepository userRepository;

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

        this.articleRepository.delete(id);
        return "redirect:/";

    }


}
