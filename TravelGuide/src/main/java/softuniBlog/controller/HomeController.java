package softuniBlog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import softuniBlog.entity.Category;
import softuniBlog.entity.Destination;
import softuniBlog.entity.Image;
import softuniBlog.repository.CategoryRepository;
import softuniBlog.repository.DestinationRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Controller
public class HomeController {
    private static final int TOP_DESTINATIONS_COUNT = 3;
    private static int INDEX = 0;

//    private List<Image> imagesByIdDesc;
//    private int index;

    private CategoryRepository categoryRepository;
    private DestinationRepository destinationRepository;
//    private Destination currentDestination;

    @Autowired
    public HomeController(CategoryRepository categoryRepository, DestinationRepository destinationRepository) {
        this.destinationRepository = destinationRepository;
        this.categoryRepository = categoryRepository;
//        this.imagesByIdDesc = new ArrayList<>();
    }

    @GetMapping("/")
    public String index(Model model) {
        List<Category> categories = this.categoryRepository.findAll();
        List<Destination> destinations = this.destinationRepository.findAllOrderedByRatingDescIdDesc().stream().limit(TOP_DESTINATIONS_COUNT).collect(Collectors.toList());
//        List<Destination> destinations = this.destinationRepository.findAllOrderedByRatingDescIdDesc().stream().skip(TOP_DESTINATIONS_COUNT).collect(Collectors.toList());;
//        Destination topDestination = this.currentDestination;

        Destination topDestination = null;
        try {
            if (INDEX < 0) {
                INDEX = 0;
            }
            if (INDEX >= destinations.size()) {
                INDEX = destinations.size() - 1;
            }
            topDestination = destinations.get(INDEX);
            Set<Image> images = topDestination.getImages().stream().filter(i -> i.getMark() == null).collect(Collectors.toSet());
            topDestination.setImages(images);
        } catch (IndexOutOfBoundsException ignored) {
        }

        //TODO: null pointer possibility in the view, th:if...

        model.addAttribute("categories", categories);
        model.addAttribute("destinations", destinations);
        model.addAttribute("topDestination", topDestination);
        model.addAttribute("view", "home/index");
        return "base-layout";
    }


   /* private Destination getBestByRating(int destinationToSkip) {
        //skipping some param n
//        Optional<Destination> currentBest = this.destinationRepository.findAllOrderedByRatingDesc().stream()
//                .skip(INDEX).findFirst();
        Optional<Destination> first = this.destinationRepository.findAllOrderedByRatingDesc().stream()
                .skip(destinationToSkip)
                .findFirst();
        return first.orElse(null);
    }*/

    //TODO: Refactor all repeating logic in final class with static methods
    @RequestMapping(value = "/prev_dest", method = RequestMethod.GET)
    public String handlePrevMark(/*@RequestParam(name = "destId") String destId*/) {
        INDEX--;

        if (INDEX < 0) {
            INDEX = 0;
        }

//        this.currentDestination = this.getBestByRating(INDEX);

        return "redirect:/";
    }

    @RequestMapping(value = "/next_dest", method = RequestMethod.GET)
    public String handleNextMark(/*@RequestParam(name = "destId") String destId*/) {
        INDEX++;

        if (INDEX > TOP_DESTINATIONS_COUNT) {
            INDEX = TOP_DESTINATIONS_COUNT;
        }

       /* this.currentDestination = this.getBestByRating(INDEX);
        if (this.currentDestination == null) {
            //TODO: tomorrow i will fix it
            *//*this.currentDestination = this.getBestByRating(INDEX - 1);*//*
        }*/

        return "redirect:/";
    }

  /*  private Destination getBestByRating(int destinationToSkip) {
        Optional<Destination> first = this.destinationRepository.findAllOrderedByRatingDescIdDesc().stream()
                .skip(destinationToSkip)
                .findFirst();
        return first.orElse(null);
    }*/
}
