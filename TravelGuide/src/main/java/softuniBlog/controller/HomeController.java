package softuniBlog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import softuniBlog.entity.Category;
import softuniBlog.entity.Destination;
import softuniBlog.repository.CategoryRepository;
import softuniBlog.repository.DestinationRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Controller
public class HomeController {
    private static final int TOP_DESTINATIONS_COUNT = 1;

    private CategoryRepository categoryRepository;
    private DestinationRepository destinationRepository;
    private Destination currentDestination;

    @Autowired
    public HomeController(CategoryRepository categoryRepository, DestinationRepository destinationRepository) {
        this.destinationRepository = destinationRepository;
        this.categoryRepository = categoryRepository;
        this.currentDestination = null;
    }

    @GetMapping("/")
    public String index(Model model) {
        List<Category> categories = this.categoryRepository.findAll();
        List<Destination> destinations = this.destinationRepository.findAllOrderedByRatingDesc().stream().skip(TOP_DESTINATIONS_COUNT).collect(Collectors.toList());
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

    @RequestMapping(value = "/prev_dest", method = RequestMethod.GET)
    public String handlePrevMark(@RequestParam(name = "destId") String destId) {
        int destIdInt = Integer.parseInt(destId) - 1;//previous
        int minId = this.destinationRepository.getMinId();

        if (destIdInt < minId) {
            destIdInt = minId;
        }

        this.currentDestination = this.destinationRepository.findOne(destIdInt);

        return "redirect:/";
    }

    @RequestMapping(value = "/next_dest", method = RequestMethod.GET)
    public String handleNextMark(@RequestParam(name = "destId") String destId) {
        int destIdInt = Integer.parseInt(destId) + 1;//next
        int maxId = this.destinationRepository.getMaxId();

        if (destIdInt > maxId) {
            destIdInt = maxId;
        }

        this.currentDestination = this.destinationRepository.findOne(destIdInt);

        return "redirect:/";
    }
}
