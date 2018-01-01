package softuniBlog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import softuniBlog.entity.Category;
import softuniBlog.entity.Destination;
import softuniBlog.repository.CategoryRepository;
import softuniBlog.repository.DestinationRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Controller
public class HomeController {
    private CategoryRepository categoryRepository;
    private DestinationRepository destinationRepository;

    @Autowired
    public HomeController(CategoryRepository categoryRepository, DestinationRepository destinationRepository) {
        this.destinationRepository = destinationRepository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/")
    public String index(Model model) {
        List<Category> categories = this.categoryRepository.findAll();
        List<Destination> destinations = this.destinationRepository.findAllOrderedByRatingDesc().stream().skip(1).collect(Collectors.toList());
        Destination topDestination = this.getBestByRating();

        model.addAttribute("categories", categories);
        model.addAttribute("destinations", destinations);
        model.addAttribute("topDestination", topDestination);
        model.addAttribute("view", "home/index");
        return "base-layout";
    }

    private Destination getBestByRating() {
        Optional<Destination> first = this.destinationRepository.findAllOrderedByRatingDesc().stream().findFirst();
        return first.orElse(null);
    }
}
