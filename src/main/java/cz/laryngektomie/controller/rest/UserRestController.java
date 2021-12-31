package cz.laryngektomie.controller.rest;

import cz.laryngektomie.model.forum.Topic;
import cz.laryngektomie.model.security.User;
import cz.laryngektomie.service.forum.TopicService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@Api
@RestController("/api")
public class UserRestController {

    private final TopicService topicService;

    public UserRestController(TopicService topicService) {
        this.topicService = topicService;
    }

    @GetMapping("topic/{id}")
    public Topic getTopic(@PathVariable long id) {
        return topicService.findById(id).get();
    }

    @GetMapping("user/{id}")
    public User getUser(@PathVariable long id) {
        return null;
    }

    @GetMapping("users")
    public List<User> getUsers() {
        return Collections.emptyList();
    }

    @PostMapping
    public User addUser(@RequestBody @Valid User user) {
        return null;
    }

}
