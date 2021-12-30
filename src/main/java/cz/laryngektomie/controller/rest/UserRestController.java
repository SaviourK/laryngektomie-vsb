package cz.laryngektomie.controller.rest;

import cz.laryngektomie.model.security.User;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@Api
@RestController("/api")
public class UserRestController {

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
