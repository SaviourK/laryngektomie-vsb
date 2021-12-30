package cz.laryngektomie.controller.home;

import cz.laryngektomie.service.security.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import static cz.laryngektomie.helper.Const.*;
import static cz.laryngektomie.helper.UrlConst.O_NAS_URL;

@Controller
@RequestMapping(O_NAS_URL)
public class ONasController {

    private static final String KDO_JSME_URL = "/kdo-jsme";
    private static final String CLENSTVI_URL = "/clenstvi";
    private static final String STANOVY_URL = "/stanovy";
    private static final String O_NAS_TEXT = "o-nas";
    private static final String O_NAS_POST_FIX = "| O nás";

    private final UserService userService;

    public ONasController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(KDO_JSME_URL)
    public ModelAndView kdoJsme() {
        ModelAndView mv = new ModelAndView(O_NAS_URL + KDO_JSME_URL);
        mv.addObject(ACTION, O_NAS_TEXT);
        mv.addObject(TITLE, "Kdo jsme " + O_NAS_POST_FIX);
        mv.addObject(USERS, userService.findTop3ByAboutUsTrue());
        return mv;
    }

    @RequestMapping(CLENSTVI_URL)
    public ModelAndView clenstvi() {
        ModelAndView mv = new ModelAndView(O_NAS_URL + CLENSTVI_URL);
        mv.addObject(ACTION, O_NAS_TEXT);
        mv.addObject(TITLE, "Členství " + O_NAS_POST_FIX);
        return mv;
    }

    @RequestMapping(STANOVY_URL)
    public ModelAndView oNas() {
        ModelAndView mv = new ModelAndView(O_NAS_URL + STANOVY_URL);
        mv.addObject(ACTION, O_NAS_TEXT);
        mv.addObject(TITLE, "Stanovy " + O_NAS_POST_FIX);
        return mv;
    }
}
