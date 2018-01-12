package softuniBlog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import softuniBlog.bindingModel.MarkBindingModel;
import softuniBlog.entity.Destination;
import softuniBlog.entity.Image;
import softuniBlog.entity.Mark;
import softuniBlog.entity.User;
import softuniBlog.repository.DestinationRepository;
import softuniBlog.repository.MarkRepository;
import softuniBlog.repository.UserRepository;
import softuniBlog.service.NotificationService;
import softuniBlog.utils.Constants;
import softuniBlog.utils.Messages;
import softuniBlog.utils.UploadImage;

import java.util.List;

/**
 * Created by George-Lenovo on 6/29/2017.
 */
@Controller
public class MarkController {

    private DestinationRepository destinationRepository;
    private UserRepository userRepository;
    private final NotificationService notifyService;
    private MarkRepository markRepository;
//    private ImageRepository imageRepository;

    @Autowired
    public MarkController(/*ImageRepository imageRepository, */MarkRepository markRepository, UserRepository userRepository, DestinationRepository destinationRepo, NotificationService notifyService) {
        this.userRepository = userRepository;
        this.destinationRepository = destinationRepo;
        this.notifyService = notifyService;
        this.markRepository = markRepository;
//        this.imageRepository = imageRepository;
    }

    @GetMapping("/mark/add")
    @PreAuthorize("isAuthenticated()")
    public String addMark(Model model) {
        List<Destination> destinations = this.destinationRepository.findAll();
        model.addAttribute("destinations", destinations);
        model.addAttribute("view", "mark/add");
        return "base-layout";
    }

    @PostMapping("/mark/add")
    @PreAuthorize("isAuthenticated()")
    public String addMarkProcess(MarkBindingModel markBindingModel) {
        Destination destination = this.destinationRepository.findOne(markBindingModel.getDestinationId());
        MultipartFile file = markBindingModel.getMarkPicture();
        String imagePathSmall = UploadImage.upload(Constants.IMG_SMALL_WIDTH, Constants.IMG_SMALL_HEIGHT, file);
        String imagePathBig = UploadImage.upload(Constants.IMG_BIG_WIDTH, Constants.IMG_BIG_HEIGHT, file);
        Image image = new Image(imagePathSmall, imagePathBig, destination);
        User currentUser = this.getCurrentUser();
        Mark mark = new Mark(destination, image, currentUser);
        this.markRepository.saveAndFlush(mark);
        this.notifyService.addInfoMessage(Messages.SUCCESSFULLY_CREATED_MARK);
        System.out.println(12);
        return "redirect:/";
    }

    @GetMapping("/mark/listAll")
    @PreAuthorize("isAuthenticated()")
    public String listAll(Model model) {
        model.addAttribute("view", "mark/all_marks");
        model.addAttribute("marks", this.markRepository.findAll());
        return "base-layout";
    }


    @GetMapping("/mark/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String delete(Model model, @PathVariable Integer id) {
        if (!this.isCurrentUserAdmin()) {
            this.notifyService.addErrorMessage(Messages.YOU_HAVE_NO_PERMISSION);
            return "redirect:/login";
        }

        if (!this.markRepository.exists(id)) {
            this.notifyService.addErrorMessage(Messages.NOT_FOUND);
            return "redirect:/";
        }

        model.addAttribute("view", "mark/delete");
        model.addAttribute("mark", this.markRepository.findOne(id));
        return "base-layout";
    }

    @PostMapping("/mark/delete/{id}")
    public String deleteProcess(@PathVariable Integer id) {
        if (!this.isCurrentUserAdmin()) {
            this.notifyService.addErrorMessage(Messages.YOU_HAVE_NO_PERMISSION);
            return "redirect:/login";
        }

        if (!this.markRepository.exists(id)) {
            this.notifyService.addErrorMessage(Messages.NOT_FOUND);
            return "redirect:/";
        }

        this.markRepository.delete(id);
        return "redirect:/";
    }

    @GetMapping("/mark/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String edit(Model model, @PathVariable Integer id) {

        if (!this.isCurrentUserAdmin()) {
            this.notifyService.addErrorMessage(Messages.YOU_HAVE_NO_PERMISSION);
            return "redirect:/login";
        }

        if (!this.markRepository.exists(id)) {
            this.notifyService.addErrorMessage(Messages.NOT_FOUND);
            return "redirect:/";
        }

        Mark mark = this.markRepository.findOne(id);

        model.addAttribute("view", "mark/edit")
                .addAttribute("mark", mark);
        return "base-layout";
    }
/*

    @PostMapping("/mark/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String editAction(MarkBindingModel markBindingModel, @PathVariable Integer id) {

        if (!this.isCurrentUserAdmin()) {
            this.notifyService.addErrorMessage(Messages.YOU_HAVE_NO_PERMISSION);
            return "redirect:/login";
        }

        if (!this.markRepository.exists(id)) {
            this.notifyService.addErrorMessage(Messages.NOT_FOUND);
            return "redirect:/";
        }

        mark mark = this.markRepository.findOne(id);

        mark.setTitle(markBindingModel.getTitle());
        mark.setContent(markBindingModel.getContent());
        Destination destination = this.destinationRepository.findOne(markBindingModel.getDestinationId());
        mark.setDestination(destination);
        this.markRepository.saveAndFlush(mark);
        this.notifyService.addInfoMessage(Messages.SUCCESSFULLY_EDITED_mark);
        return "redirect:/mark/" + mark.getId();

    }

  */

    private User getCurrentUser() {
        if (!(SecurityContextHolder.getContext().getAuthentication()
                instanceof AnonymousAuthenticationToken)) {
            UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();
            return this.userRepository.findByEmail(principal.getUsername());
        }
        return null;
    }

    private boolean isCurrentUserAdmin() {
        return this.getCurrentUser() != null && this.getCurrentUser().isAdmin();
    }

}