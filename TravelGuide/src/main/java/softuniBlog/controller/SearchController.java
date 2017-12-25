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
import java.util.stream.Collectors;

@Controller
public class SearchController {

    private ArticleRepository repository;

    @Autowired
    public SearchController(ArticleRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/search")
    public String getSearch(Model model) {
        model.addAttribute("view", "search/search");
        return "base-layout";
    }

    @PostMapping("/search")
    public String postSearch(SearchBindingModel searchBindingModel) {
        Article article = this.repository.findByTitle(searchBindingModel.getTitle());
        return article == null ? "redirect:/" : String.format("redirect:/article/%s", article.getId());
    }

    @GetMapping("/search_keyword")
    public String getSearchByKeyword(Model model) {
        model.addAttribute("view", "search/search_keyword");
        return "base-layout";
    }


    @PostMapping("/search_keyword")
    public String searchByTitleKeyword(SearchBindingModel searchBindingModel,Model model) {
        List<Article> article = this.repository.findAll();
        List<Article> result = article.stream()
                .filter(a -> a.getTitle().toLowerCase().contains(searchBindingModel.getTitle().toLowerCase()))
                .collect(Collectors.toList());
        model.addAttribute("results", result);
        model.addAttribute("view", "search/search_keyword_results");

        return "base-layout";
    }
}
