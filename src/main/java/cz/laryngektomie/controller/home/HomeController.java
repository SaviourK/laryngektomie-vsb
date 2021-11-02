package cz.laryngektomie.controller.home;


import cz.laryngektomie.CreateDataJob;
import cz.laryngektomie.service.article.ArticleService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class HomeController {

    private final ArticleService articleService;
    private final CreateDataJob createDataJob;

    public HomeController(ArticleService articleService, CreateDataJob createDataJob) {
        this.articleService = articleService;
        this.createDataJob = createDataJob;
    }

    @GetMapping("/")
    public ModelAndView home() {
        ModelAndView mv = new ModelAndView("index");
        mv.addObject("action", "home");
        mv.addObject("title", "Úvod");
        mv.addObject("article", articleService.findFirst3ByOrderByCreateDateTimeDesc());
        return mv;
    }

    @GetMapping("/batch")
    public ModelAndView batch() {
        ModelAndView mv = new ModelAndView("redirect:/komunikace");
        createDataJob.run();
        return mv;
    }

    @GetMapping("ke-stazeni")
    public ModelAndView keStazeni() {
        ModelAndView mv = new ModelAndView("ke-stazeni");
        mv.addObject("action", "ke-stazeni");
        mv.addObject("title", "Ke stažení");
        return mv;
    }

    @GetMapping("partneri-sponzori")
    public ModelAndView partneriASponzori() {
        ModelAndView mv = new ModelAndView("partneri-sponzori");
        mv.addObject("action", "partneri-sponzori");
        mv.addObject("title", "Partneři a Sponzoři");
        return mv;
    }
}
