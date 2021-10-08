package cz.laryngektomie.controller.home;

import cz.laryngektomie.CreateDataJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("komunikace")
public class KomunikaceController {

    @Autowired
    private CreateDataJob createDataJob;

    @GetMapping()
    public ModelAndView komunikace() {
        ModelAndView mv = new ModelAndView("komunikace");
        mv.addObject("action", "komunikace");
        mv.addObject("title", "Komunikace");
        createDataJob.run();
        return mv;
    }

    @GetMapping("/jicnovy-hlas")
    public ModelAndView jicnovyHlas() {
        ModelAndView mv = new ModelAndView("komunikace/jicnovy-hlas");
        mv.addObject("action", "komunikace");
        mv.addObject("title", "Jícnový hlas | Komunikace");
        return mv;
    }

    @GetMapping("/hlasova-proteza")
    public ModelAndView hlasovaProteza() {
        ModelAndView mv = new ModelAndView("komunikace/hlasova-proteza");
        mv.addObject("action", "komunikace");
        mv.addObject("title", "Hlasová protéza | Komunikace");
        return mv;
    }

    @GetMapping("/elektrolarynx")
    public ModelAndView elektrolarynx() {
        ModelAndView mv = new ModelAndView("komunikace/elektrolarynx");
        mv.addObject("action", "komunikace");
        mv.addObject("title", "Elektrolarynx | Komunikace");
        return mv;
    }
}
