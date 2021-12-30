package cz.laryngektomie.controller.home;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import static cz.laryngektomie.helper.Const.ACTION;
import static cz.laryngektomie.helper.Const.TITLE;
import static cz.laryngektomie.helper.UrlConst.KOMUNIKACE_URL;

@Controller
@RequestMapping(KOMUNIKACE_URL)
public class KomunikaceController {

    private static final String JICNOVY_HLAS_URL = "/jicnovy-hlas";
    private static final String HLASOVA_PROTEZA_URL = "/hlasova-proteza";
    private static final String ELEKTROLARYNX_URL = "/elektrolarynx";
    private static final String KOMUNIKACE_TEXT = "komunikace";
    private static final String KOMUNIKACE_POST_FIX = "| Komunikace";

    @GetMapping()
    public ModelAndView komunikace() {
        ModelAndView mv = new ModelAndView(KOMUNIKACE_TEXT);
        mv.addObject(ACTION, KOMUNIKACE_TEXT);
        mv.addObject(TITLE, KOMUNIKACE_TEXT);
        return mv;
    }

    @GetMapping(JICNOVY_HLAS_URL)
    public ModelAndView jicnovyHlas() {
        ModelAndView mv = new ModelAndView(KOMUNIKACE_URL + JICNOVY_HLAS_URL);
        mv.addObject(ACTION, KOMUNIKACE_TEXT);
        mv.addObject(TITLE, "Jícnový hlas " + KOMUNIKACE_POST_FIX);
        return mv;
    }

    @GetMapping(HLASOVA_PROTEZA_URL)
    public ModelAndView hlasovaProteza() {
        ModelAndView mv = new ModelAndView(KOMUNIKACE_URL + HLASOVA_PROTEZA_URL);
        mv.addObject(ACTION, KOMUNIKACE_TEXT);
        mv.addObject(TITLE, "Hlasová protéza " + KOMUNIKACE_POST_FIX);
        return mv;
    }

    @GetMapping(ELEKTROLARYNX_URL)
    public ModelAndView elektrolarynx() {
        ModelAndView mv = new ModelAndView(KOMUNIKACE_URL + ELEKTROLARYNX_URL);
        mv.addObject(ACTION, KOMUNIKACE_TEXT);
        mv.addObject(TITLE, "Elektrolarynx " + KOMUNIKACE_POST_FIX);
        return mv;
    }
}
