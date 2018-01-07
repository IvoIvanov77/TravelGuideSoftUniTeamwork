package softuniBlog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import softuniBlog.entity.Category;
import softuniBlog.entity.Destination;
import softuniBlog.repository.CategoryRepository;
import softuniBlog.repository.DestinationRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Controller
public class HomeController {
    private static final int TOP_DESTINATIONS_COUNT = 3;
    private static int TOP_DESTINATION_SKIP_INDEXATOR = 0;

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
        Destination topDestination = this.currentDestination;

        if (topDestination == null)
            topDestination = this.getBestByRating(0);

        model.addAttribute("categories", categories);
        model.addAttribute("destinations", destinations);
        model.addAttribute("topDestination", topDestination);
        model.addAttribute("view", "home/index");
        return "base-layout";
    }


    private Destination getBestByRating(int destinationToSkip) {
        //skipping some param n
//        Optional<Destination> currentBest = this.destinationRepository.findAllOrderedByRatingDesc().stream()
//                .skip(TOP_DESTINATION_SKIP_INDEXATOR).findFirst();
        Optional<Destination> first = this.destinationRepository.findAllOrderedByRatingDesc().stream()
                .skip(destinationToSkip)
                .findFirst();
        return first.orElse(null);
    }

    //TODO: Refactor all repeating logic in final class with static methods
    @RequestMapping(value = "/prev_dest", method = RequestMethod.GET)
    public String handlePrevMark(/*@RequestParam(name = "destId") String destId*/) {
        TOP_DESTINATION_SKIP_INDEXATOR--;

        if (TOP_DESTINATION_SKIP_INDEXATOR < 0) {
            TOP_DESTINATION_SKIP_INDEXATOR = 0;
        }

        this.currentDestination = this.getBestByRating(TOP_DESTINATION_SKIP_INDEXATOR);

        return "redirect:/";
    }

    @RequestMapping(value = "/next_dest", method = RequestMethod.GET)
    public String handleNextMark(/*@RequestParam(name = "destId") String destId*/) {
        TOP_DESTINATION_SKIP_INDEXATOR++;

        if (TOP_DESTINATION_SKIP_INDEXATOR > TOP_DESTINATIONS_COUNT) {
            TOP_DESTINATION_SKIP_INDEXATOR = TOP_DESTINATIONS_COUNT;
        }

        this.currentDestination = this.getBestByRating(TOP_DESTINATION_SKIP_INDEXATOR);
        if (this.currentDestination == null) {
            //TODO: tomorrow i will fix it
            /*this.currentDestination = this.getBestByRating(TOP_DESTINATION_SKIP_INDEXATOR - 1);*/
        }

        return "redirect:/";
    }
}
