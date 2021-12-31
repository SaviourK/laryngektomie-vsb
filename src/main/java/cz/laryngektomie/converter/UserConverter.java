package cz.laryngektomie.converter;

import cz.laryngektomie.model.security.User;
import cz.laryngektomie.service.security.UserService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserConverter implements Converter<String, User> {

    private final UserService userService;

    public UserConverter(UserService userService) {
        this.userService = userService;
    }

    @Override
    public User convert(String s) {
        if (!s.isEmpty()) {
            return userService.findByUsername(s);
        }
        return null;
    }
}
