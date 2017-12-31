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


@Controller
public class HomeController {

    private CategoryRepository categoryRepository;
    private DestinationRepository destinationRepository;
//    private UserRepository userRepository;
//    private ArticleRepository articleRepository;

    @Autowired
    public HomeController(CategoryRepository categoryRepository, DestinationRepository destinationRepository
                         /* UserRepository userRepository, ArticleRepository articleRepository*/) {
        this.destinationRepository = destinationRepository;
        this.categoryRepository = categoryRepository;
//        this.userRepository = userRepository;
//        this.articleRepository = articleRepository;
    }

    @GetMapping("/")
    public String index(Model model) {
        List<Category> categories = this.categoryRepository.findAll();
        List<Destination> destinations = this.destinationRepository.findAll();
        model.addAttribute("categories", categories);
        model.addAttribute("destinations", destinations);
        model.addAttribute("view", "home/index");
        return "base-layout";
    }

//    @GetMapping("/destination/{id}")
//    public String details(Model model, @PathVariable Integer id) {
//
//        if (!this.destinationRepository.exists(id)) {
//            return "redirect:/";
//        }
//        if (!(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)) {
//            UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
//                    .getAuthentication()
//                    .getPrincipal();
//
//            User user = this.userRepository.findByEmail(principal.getUsername());
//            model.addAttribute("user", user);
//        }
//        Destination destination = this.destinationRepository.findOne(id);
//        model.addAttribute("view", "destination/details")
//                .addAttribute("destination", destination);
//        return "base-layout";
//    }
//    private void initializeData() {
//        //TODO: new idea: if the db is empty, seed the tourism categories
//        Category coastal = new Category("Coastal Tourism");
//        Category urban = new Category("Urban Tourism");
//        Category rural = new Category("Rural Tourism");
//        this.categoryRepository.save(Arrays.asList(new Category[]{coastal, urban, rural}));
//    }
}
