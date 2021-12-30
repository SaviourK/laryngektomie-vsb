package cz.laryngektomie.controller.home;

import cz.laryngektomie.service.article.ArticleService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import static cz.laryngektomie.helper.Const.*;
import static cz.laryngektomie.helper.UrlConst.HOME_URL;

@Controller
public class HomeController {

    private static final String KE_STAZENI_URL = "ke-stazeni";
    private static final String PARTNERI_SPONZORI_URL = "partneri-sponzori";

    private final ArticleService articleService;

    public HomeController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping(HOME_URL)
    public ModelAndView home() {
        ModelAndView mv = new ModelAndView(INDEX);
        mv.addObject(ACTION, "home");
        mv.addObject(TITLE, "Úvod");
        mv.addObject(ARTICLE, articleService.findFirst3ByOrderByCreateDateTimeDesc());
        return mv;
    }

    @GetMapping(KE_STAZENI_URL)
    public ModelAndView keStazeni() {
        ModelAndView mv = new ModelAndView(KE_STAZENI_URL);
        mv.addObject(ACTION, KE_STAZENI_URL);
        mv.addObject(TITLE, "Ke stažení");
        return mv;
    }

    @GetMapping(PARTNERI_SPONZORI_URL)
    public ModelAndView partneriASponzori() {
        ModelAndView mv = new ModelAndView(PARTNERI_SPONZORI_URL);
        mv.addObject(ACTION, PARTNERI_SPONZORI_URL);
        mv.addObject(TITLE, "Partneři a Sponzoři");
        return mv;
    }
}
