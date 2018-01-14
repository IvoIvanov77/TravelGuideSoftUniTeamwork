package travelGuide.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import travelGuide.repository.ArticleRepository;
import travelGuide.repository.UserRepository;

@Controller
public class UsersVotesController {
    private UserRepository userRepository;
    private ArticleRepository articleRepository;

    @Autowired
    public UsersVotesController(UserRepository userRepository, ArticleRepository articleRepository) {
        this.userRepository = userRepository;
        this.articleRepository = articleRepository;
    }



}
