package softuniBlog.controller.rest;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import softuniBlog.ajax_bodies.PointRequest;
import softuniBlog.ajax_bodies.TextResponse;

import javax.validation.Valid;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/mark")
public class UserMarksController {

    @PostMapping(value="/request")
    public ResponseEntity getUserMark(
            @Valid @RequestBody PointRequest pointRequest, Errors errors){

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
