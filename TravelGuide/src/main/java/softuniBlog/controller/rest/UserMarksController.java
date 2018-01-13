package softuniBlog.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import softuniBlog.ajax_bodies.PointRequest;
import softuniBlog.ajax_bodies.TextResponse;
import softuniBlog.entity.Mark;
import softuniBlog.repository.MarkRepository;

import javax.validation.Valid;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/mark")
public class UserMarksController {

    private final MarkRepository markRepository;

    @Autowired
    public UserMarksController(MarkRepository markRepository) {
        this.markRepository = markRepository;
    }

    @PostMapping(value="/request")
    public ResponseEntity setUserMark(
            @Valid @RequestBody PointRequest pointRequest, Errors errors){

        Mark userMark = new Mark();


        TextResponse result = new TextResponse();

        //If error, just return a 400 bad request, along with the error message
        if (errors.hasErrors()) {
            result.setMessage(errors.getAllErrors()
                    .stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(",")));

            return ResponseEntity.badRequest().body(result);
        }

        result.setMessage("Your point is submitted for review!");

        return ResponseEntity.ok(result);
    }

}
