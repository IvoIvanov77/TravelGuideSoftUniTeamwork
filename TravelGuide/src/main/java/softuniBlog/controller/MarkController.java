package softuniBlog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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

    @Autowired
    public MarkController(UserRepository userRepository, DestinationRepository destinationRepo, NotificationService notifyService) {
        this.userRepository = userRepository;
        this.destinationRepository = destinationRepo;
        this.notifyService = notifyService;
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
//        User user = this.getCurrentUser();
        Destination destination = this.destinationRepository.findOne(markBindingModel.getDestinationId());
        MultipartFile file = markBindingModel.getPicture();
        String imagePath = UploadImage.upload(Constants.IMG_SMALL_WIDTH, Constants.IMG_SMALL_HEIGHT, file);
        //TODO Refactor my code: change non required code with cascade DB later on
        Image image = new Image(imagePath, destination);
        Mark mark = new Mark(destination, image);
        this.markRepository.saveAndFlush(mark);
        this.notifyService.addInfoMessage(Messages.SUCCESSFULLY_CREATED_MARK);
        return "redirect:/all_articles";
    }

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
}
