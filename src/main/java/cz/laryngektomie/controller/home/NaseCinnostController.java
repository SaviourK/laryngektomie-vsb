package cz.laryngektomie.controller.home;

import cz.laryngektomie.model.article.Article;
import cz.laryngektomie.service.article.ArticleService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

import static cz.laryngektomie.helper.Const.*;
import static cz.laryngektomie.helper.UrlConst.*;


@Controller
@RequestMapping(NASE_CINNOST_URL)
public class NaseCinnostController {

    private static final String ZPRAVODAJ_URL = "/zpravodaj";
    private static final String NASE_CINNOST_TEXT = "nase-cinnost";
    private static final String POMUCKY_URL = "/pomucky";
    private static final String NASE_CINNOST_POST_FIX = "| Naše činnost";

    private final ArticleService articleService;

    public NaseCinnostController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @RequestMapping(CLANKY_URL)
    public ModelAndView aktuality() {
        return new ModelAndView(REDIRECT_URL + CLANKY_URL);
    }

    @RequestMapping(ZPRAVODAJ_URL)
    public ModelAndView zpravodaj() {
        ModelAndView mv = new ModelAndView(NASE_CINNOST_URL + ZPRAVODAJ_URL);
        Optional<Article> articleOptional = articleService.findLastNewsletter();

        if (!articleOptional.isPresent()) {
            mv.addObject(MESSAGE_ERROR, "Zatím žádný zpravodaj");
            mv.setViewName(REDIRECT_URL + INDEX_URL);
            return mv;
        }

        mv.addObject(ACTION, NASE_CINNOST_TEXT);
        mv.addObject(TITLE, "Zpravodaj " + NASE_CINNOST_POST_FIX);
        mv.addObject(ARTICLE, articleOptional.get());
        return mv;
    }

    @RequestMapping(POMUCKY_URL)
    public ModelAndView rehabilitacniPobyt() {
        ModelAndView mv = new ModelAndView(NASE_CINNOST_URL + POMUCKY_URL);
        mv.addObject(ACTION, NASE_CINNOST_TEXT);
        mv.addObject(TITLE, "Pomůcky " + NASE_CINNOST_POST_FIX);
        return mv;
    }
}
