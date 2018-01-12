package softuniBlog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import softuniBlog.bindingModel.DestinationBindingModel;
import softuniBlog.entity.*;
import softuniBlog.repository.*;
import softuniBlog.service.NotificationService;
import softuniBlog.utils.Constants;
import softuniBlog.utils.DeleteImage;
import softuniBlog.utils.Messages;
import softuniBlog.utils.UploadImage;

import java.util.*;
import java.util.stream.Collectors;

import static softuniBlog.utils.Constants.DESTINATION_AVAILABLE_IMAGES_COUNT;
import static softuniBlog.utils.Constants.EMTPY_STRING;


@Controller
public class DestinationController {

    private final DestinationRepository destinationRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final UsersVotesRepository usersVotesRepository;
    private final NotificationService notifyService;

    private MarkRepository markRepository;

    private int INDEX;
//    private Mark currentMark = null;

    @Autowired
    public DestinationController(DestinationRepository destinationRepository, UserRepository userRepository,
                                 UsersVotesRepository usersVotesRepository, MarkRepository markRepository, CategoryRepository categoryRepository,
                                 NotificationService notifyService) {
        this.destinationRepository = destinationRepository;
        this.usersVotesRepository = usersVotesRepository;
        this.markRepository = markRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.notifyService = notifyService;
    }

    @GetMapping("/destination/add")
    @PreAuthorize("isAuthenticated()")
    public String createDestination(Model model) {
        if (!this.isCurrentUserAdmin()) {
            this.notifyService.addErrorMessage(Messages.YOU_HAVE_NO_PERMISSION);
            return "redirect:/login";
        }
        if (this.categoryRepository.findAll().isEmpty()) {
            this.notifyService.addErrorMessage(Messages.THERE_ARE_NO_CATEGORIES_AVAILABLE);
        }
        model.addAttribute("view", "destination/create");
        model.addAttribute("categories", this.categoryRepository.findAll());
        return "admin/admin_panel-layout";
    }

    @PostMapping("/destination/add")
    @PreAuthorize("isAuthenticated()")
    public String createDestinationProcess(DestinationBindingModel destinationBindingModel) {
        if (!this.isCurrentUserAdmin()) {
            this.notifyService.addErrorMessage(Messages.YOU_HAVE_NO_PERMISSION);
            return "redirect:/login";
        }

        User currentUser = this.getCurrentUser();

        Set<MultipartFile> files = destinationBindingModel.getPictures();
        files.add(destinationBindingModel.getPicture());

        Category category = this.categoryRepository.findOne(Math.toIntExact(destinationBindingModel.getCategoryId()));
        Destination destination = new Destination(destinationBindingModel.getName(),
                destinationBindingModel.getReview(), currentUser, category, destinationBindingModel.getPrice());
        Set<Image> images = this.setImagesToDestination(files, destination);
        destination.setImages(images);

        this.destinationRepository.saveAndFlush(destination);
        this.notifyService.addInfoMessage(Messages.SUCCESSFULLY_CREATED_DESTINATION);
        return "redirect:/all_destinations";
    }

    private Set<Image> setImagesToDestination(Set<MultipartFile> files, Destination destination) {
        Set<Image> images = new HashSet<>();
        files.stream().filter(x -> !x.getOriginalFilename().equals(EMTPY_STRING))
                .forEach(file -> {
                    String smallImagePath = UploadImage.upload(Constants.IMG_SMALL_WIDTH, Constants.IMG_SMALL_HEIGHT, file);
                    String bigImagePath = UploadImage.upload(Constants.IMG_BIG_WIDTH, Constants.IMG_BIG_HEIGHT, file);
                    images.add(new Image(smallImagePath, bigImagePath, destination));
                });
        return images;
    }

    @GetMapping("/destination/{id}")
    public String destinationDetails(Model model, @PathVariable Integer id) {
        if (!this.destinationRepository.exists(id)) {
            this.notifyService.addErrorMessage(Messages.NOT_FOUND);
            return "redirect:/";
        }

        Destination destination = this.destinationRepository.findOne(id);
        List<Mark> marks = this.markRepository.findAllMarksOrderByIdDesc(id);

        Mark currentMark = null;
        try {
            if (INDEX < 0) INDEX = 0;
            if (INDEX >= marks.size()) INDEX = marks.size() - 1;

            currentMark = marks.get(INDEX);
        } catch (IndexOutOfBoundsException ignored) {
        }
       /* if (this.currentMark == null) {
            Set<Mark> marks = destination.getMarks();
            if (marks.size() > 0) {
                this.currentMark = marks.stream().collect(Collectors.toList()).get(0);
            }
        }*/

        model.addAttribute("view", "destination/details")
                .addAttribute("destination", destination)
                .addAttribute("mark", currentMark)
                .addAttribute("articles", destination.getArticles());

        return "base-layout";
    }

    @RequestMapping(value = "/prev_mark", method = RequestMethod.GET)
    public String handlePrevMark(@RequestParam(name = "destId") String destId) {
       /* int markIdInt = Integer.parseInt(markId) - 1;//previous
        int minId = this.markRepository.getMinId();

        if (markIdInt < minId) {
            markIdInt = minId;
        }*/
        INDEX--;
/*
        if (INDEX < 0) {
            INDEX = 0;
        }*/

        /*Mark prevMark = this.markRepository.findOne(markIdInt);
        this.currentMark = prevMark;*/

//        Integer destinationId = prevMark.getDestination().getId();
        Integer destinationId = this.destinationRepository.findOne(Integer.valueOf(destId)).getId();
        return "redirect:/destination/" + destinationId;
    }

    @RequestMapping(value = "/next_mark", method = RequestMethod.GET)
    public String handleNextMark(@RequestParam(name = "destId") String destId) {
      /*  int markIdInt = Integer.parseInt(markId) + 1;//next
        int maxId = this.markRepository.getMaxId();

        if (markIdInt > maxId) {
            markIdInt = maxId;
        }

        Mark prevMark = this.markRepository.findOne(markIdInt);
        this.currentMark = prevMark;

        Integer destinationId = prevMark.getDestination().getId();*/
        INDEX++;

      /*  if (INDEX > TOP_DESTINATIONS_COUNT) {
            INDEX = TOP_DESTINATIONS_COUNT;
        }*/

        Integer destinationId = this.destinationRepository.findOne(Integer.valueOf(destId)).getId();
        return "redirect:/destination/" + destinationId;
    }

    @GetMapping("/destination/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String edit(Model model, @PathVariable Integer id) {

        if (!this.isCurrentUserAdmin()) {
            this.notifyService.addErrorMessage(Messages.YOU_HAVE_NO_PERMISSION);
            return "redirect:/login";
        }

        if (!this.destinationRepository.exists(id)) {
            this.notifyService.addErrorMessage(Messages.NOT_FOUND);
            return "redirect:/all_destinations";
        }
        Destination destination = this.destinationRepository.findOne(id);

        model.addAttribute("view", "destination/edit")
                .addAttribute("destination", destination);
        model.addAttribute("categories", this.categoryRepository.findAll());
        return "admin/admin_panel-layout";
    }

    @PostMapping("/destination/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String editAction(DestinationBindingModel bindingModel, @PathVariable Integer id) {
        if (!this.isCurrentUserAdmin()) {
            this.notifyService.addErrorMessage(Messages.YOU_HAVE_NO_PERMISSION);
            return "redirect:/login";
        }

        if (!this.destinationRepository.exists(id)) {
            this.notifyService.addErrorMessage(Messages.NOT_FOUND);
            return "redirect:/all_destinations";
        }

        Destination destination = this.destinationRepository.findOne(id);

        destination.setName(bindingModel.getName());
        destination.setReview(bindingModel.getReview());
        destination.setCategory(this.categoryRepository.findOne(bindingModel.getCategoryId()));

        Set<MultipartFile> pictures = bindingModel.getPictures();
        pictures.add(bindingModel.getPicture());
        int availableImageToAdd = DESTINATION_AVAILABLE_IMAGES_COUNT - destination.getImages().size();
        Set<Image> images = this.setImagesToDestination(pictures, destination).stream().limit(availableImageToAdd).collect(Collectors.toSet());
        destination.addImages(images);

        this.destinationRepository.saveAndFlush(destination);
        this.notifyService.addInfoMessage(Messages.SUCCESSFULLY_EDITED_DESTINATION);

        //TODO: delete the rest
        DeleteImage.deleteImagesFiles(images);
        return "redirect:/all_destinations";
    }

    @GetMapping("/all_destinations")
    @PreAuthorize("isAuthenticated()")
    public String categories(Model model) {

        if (this.isCurrentUserAdmin()) {
            List<Destination> allDestinations = this.destinationRepository.findAll();
            model.addAttribute("view", "destination/all_destinations");
            model.addAttribute("destinations", allDestinations);
            return "admin/admin_panel-layout";
        }

        this.notifyService.addInfoMessage(Messages.YOU_HAVE_NO_PERMISSION);
        return "redirect:/login";
    }

    @GetMapping("/destination/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String delete(Model model, @PathVariable Integer id) {

        if (!this.isCurrentUserAdmin()) {
            this.notifyService.addErrorMessage(Messages.YOU_HAVE_NO_PERMISSION);
            return "redirect:/login";
        }

        if (!this.destinationRepository.exists(id)) {
            this.notifyService.addErrorMessage(Messages.NOT_FOUND);
            return "redirect:/";
        }

        Destination destination = this.destinationRepository.findOne(id);

        model.addAttribute("view", "destination/delete")
                .addAttribute("destination", destination)
                .addAttribute("images", destination.getImages());
        return "admin/admin_panel-layout";
    }

    @PostMapping("/destination/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String deleteAction(@PathVariable Integer id) {

        if (!this.isCurrentUserAdmin()) {
            this.notifyService.addErrorMessage(Messages.YOU_HAVE_NO_PERMISSION);
            return "redirect:/login";
        }

        if (!this.destinationRepository.exists(id)) {
            this.notifyService.addErrorMessage(Messages.NOT_FOUND);
            return "redirect:/";
        }

        DestinationController.deleteImagesFromDisk(this.destinationRepository.findOne(id).getImages());
        this.destinationRepository.delete(id);
        this.notifyService.addInfoMessage(Messages.SUCCESSFULLY_DELETED_DESTINATION);
        return "redirect:/all_destinations";

    }

    public static void deleteImagesFromDisk(Set<Image> images) {
        DeleteImage.deleteImagesFiles(images);
    }

//    private boolean isUserAuthorOrAdmin(Destination destination) {
//        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        User userEntity = this.userRepository.findByEmail(user.getUsername());
//
//        return userEntity.isAuthor(destination) || userEntity.isAdmin();
//    }

    private boolean isCurrentUserAdmin() {
        return this.getCurrentUser() != null && this.getCurrentUser().isAdmin();
    }

    private User getCurrentUser() {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        return this.userRepository.findByEmail(principal.getUsername());
    }
}
