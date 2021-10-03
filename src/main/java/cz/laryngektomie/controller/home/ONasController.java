package cz.laryngektomie.controller.home;

import cz.laryngektomie.service.security.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/o-nas")
public class ONasController {

    private final UserService userService;

    public ONasController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/kdo-jsme")
    public ModelAndView kdoJsme() {
        ModelAndView mv = new ModelAndView("o-nas/kdo-jsme");
        mv.addObject("action", "o-nas");
        mv.addObject("title", "Kdo jsme | O nás");
        mv.addObject("users", userService.findByAboutUsTrue());
        return mv;
    }

    @RequestMapping("/clenstvi")
    public ModelAndView clenstvi() {
        ModelAndView mv = new ModelAndView("o-nas/clenstvi");
        mv.addObject("action", "o-nas");
        mv.addObject("title", "Členství | O nás");
        return mv;
    }

    @RequestMapping("/stanovy")
    public ModelAndView oNas() {
        ModelAndView mv = new ModelAndView("o-nas/stanovy");
        mv.addObject("action", "o-nas");
        mv.addObject("title", "Stanovy | O nás");
        return mv;
    }
}
