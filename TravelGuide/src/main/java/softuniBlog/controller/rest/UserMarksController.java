package softuniBlog.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import softuniBlog.ajax_bodies.PointRequest;
import softuniBlog.ajax_bodies.TextResponse;
import softuniBlog.entity.Mark;
import softuniBlog.entity.User;
import softuniBlog.repository.DestinationRepository;
import softuniBlog.repository.MarkRepository;
import softuniBlog.repository.UserRepository;
import softuniBlog.utils.Constants;
import softuniBlog.utils.UploadImage;

import javax.validation.Valid;
import java.io.File;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/mark")
public class UserMarksController {

    private final MarkRepository markRepository;
    private final UserRepository userRepository;
    private final DestinationRepository destinationRepository;

    @Autowired
    public UserMarksController(MarkRepository markRepository, UserRepository userRepository, DestinationRepository destinationRepository) {
        this.markRepository = markRepository;
        this.userRepository = userRepository;
        this.destinationRepository = destinationRepository;
    }

    @PostMapping(value = "/request")
    public ResponseEntity setUserMark(
            @Valid @RequestBody PointRequest pointRequest, Errors errors) {
        TextResponse result = new TextResponse();
        User currentUser = this.getCurrentUser();

        if (currentUser == null){
            result.setMessage("You must log in to create point on the map!");
            return ResponseEntity.badRequest().body(result);
        }

        File file = new File(pointRequest.getImage());
        UploadImage.resizeAndWriteImage(System.getProperty("user.dir") + Constants.IMAGE_PATH, file,
                Constants.IMG_SMALL_WIDTH, Constants.IMG_SMALL_HEIGHT);

        //If error, just return a 400 bad request, along with the error message
        if (errors.hasErrors()) {
            result.setMessage(errors.getAllErrors()
                    .stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(",")));

            return ResponseEntity.badRequest().body(result);
        }

        Mark userMark = new Mark();

        userMark.setEvent(pointRequest.getEvent());
        userMark.setComment(pointRequest.getComment());
        userMark.setLat(pointRequest.getLat());
        userMark.setLng(pointRequest.getLon());
        userMark.setApproved(false);
        userMark.setAuthor(currentUser);
        userMark.setDestination(
                this.destinationRepository.findOne(pointRequest.getDest_id()));
        this.markRepository.saveAndFlush(userMark);

        result.setMessage("Your point is submitted for review!");

        return ResponseEntity.ok(result);
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
