package travelGuide.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import travelGuide.bindingModel.FilterBindingModel;
import travelGuide.bindingModel.SearchBindingModel;
import travelGuide.entity.Article;
import travelGuide.entity.Category;
import travelGuide.entity.Destination;
import travelGuide.entity.User;
import travelGuide.repository.ArticleRepository;
import travelGuide.repository.CategoryRepository;
import travelGuide.repository.DestinationRepository;
import travelGuide.repository.UserRepository;
import travelGuide.service.NotificationService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@SessionAttributes("filterBindingModel")
public class SearchController {

    private final CategoryRepository categoryRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final NotificationService notifyService;
    private final DestinationRepository destinationRepository;

    @Autowired
    public SearchController(ArticleRepository repository, CategoryRepository categoryRepository,
                            ArticleRepository articleRepository, UserRepository userRepository,
                            NotificationService notifyService, DestinationRepository destinationRepository) {
        this.categoryRepository = categoryRepository;
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
        this.notifyService = notifyService;
        this.destinationRepository = destinationRepository;

    }

    @GetMapping("/search")
    public String getSearch(Model model) {
        model.addAttribute("view", "search/search_keyword");
        return "base-layout";
    }

    @GetMapping("/filter")
    public String filter(Model model, FilterBindingModel filterBindingModel) {
        List<Category> categories = this.categoryRepository.findAll();
        List<User> authors = this.destinationRepository.findAll()
                .stream()
                .map(Destination::getAuthor)
                .distinct()
                .collect(Collectors.toList());
        List<Destination> destinations = this.destinationRepository.findAll();

        if(!filterBindingModel.getCategoryIds().isEmpty()){
           destinations = destinations
                    .stream()
                    .filter(destination -> filterBindingModel.getCategoryIds().contains(destination.getCategory().getId()))
                    .collect(Collectors.toList());
        }

        if(!filterBindingModel.getAuthorIds().isEmpty()){
            destinations = destinations
                    .stream()
                    .filter(destination -> filterBindingModel.getAuthorIds().contains(destination.getAuthor().getId()))
                    .collect(Collectors.toList());
        }


        filterBindingModel.clearData();
        model.addAttribute("destinations", destinations);
        model.addAttribute("categories", categories);
        model.addAttribute("authors", authors);
        model.addAttribute("view", "search/destination_filter");

        return "base-layout";
    }

    @PostMapping("/filter")
    public String filterAction(Model model, FilterBindingModel filterBindingModel) {
        System.out.println("values:");
        for (Integer value : filterBindingModel.getCategoryIds()) {
            System.out.println(value);
        }
        model.addAttribute("filterBindingModel", filterBindingModel);
        return "redirect:/filter";
    }


    @PostMapping("/search")
    public String postSearch(SearchBindingModel searchBindingModel) {
        Article article = this.articleRepository.findByTitle(searchBindingModel.getTitle());
        return article == null ? "redirect:/" : String.format("redirect:/article/%s", article.getId());
    }

    @GetMapping("/search_keyword")
    public String getSearchByKeyword(Model model) {
        model.addAttribute("view", "search/search_keyword");
        return "base-layout";
    }


    @PostMapping("/search_keyword")
    public String searchByTitleKeyword(SearchBindingModel searchBindingModel,Model model) {
        List<Article> article = this.articleRepository.findAll();
        List<Article> result = article.stream()
                .filter(a -> a.getTitle().toLowerCase().contains(searchBindingModel.getTitle().toLowerCase()))
                .collect(Collectors.toList());
        model.addAttribute("results", result);
        model.addAttribute("view", "search/search_keyword_results");

        return "base-layout";
    }


}
