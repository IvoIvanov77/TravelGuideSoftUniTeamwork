package softuniBlog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import softuniBlog.bindingModel.SearchBindingModel;
import softuniBlog.entity.Article;
import softuniBlog.repository.ArticleRepository;

import java.util.List;

@Controller
public class SearchController {

    private ArticleRepository repository;

    @Autowired
    public SearchController(ArticleRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/search")
    public String getSearch(Model model){
        model.addAttribute("view", "search/search");
        return "base-layout";
    }

    @PostMapping("/search")
    public String postSearch(SearchBindingModel searchBindingModel){
        Article article = null;
        List<Article> articles = this.repository.findAll();
        for (Article currentArticle : articles) {
            if (searchBindingModel.getTitle().trim().equals(currentArticle.getTitle().trim())){
                article = currentArticle;
            }
        }

        if (article == null){
            return "redirect:/";
        }

        return String.format("redirect:/article/%s", article.getId());
    }
}
