package cz.laryngektomie.controller.home;

import cz.laryngektomie.model.article.Article;
import cz.laryngektomie.service.article.ArticleService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
@RequestMapping("nase-cinnost")
public class NaseCinnostController {

    private final ArticleService articleService;

    public NaseCinnostController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @RequestMapping("/clanky")
    public ModelAndView aktuality() {
        return new ModelAndView("redirect:/clanky");
    }

    @RequestMapping("/zpravodaj")
    public ModelAndView zpravodaj() {
        ModelAndView mv = new ModelAndView("nase-cinnost/zpravodaj");
        Optional<Article> articleOptional = articleService.findLastNewsletter();

        if (!articleOptional.isPresent()) {
            mv.addObject("messageError", "Zatím žádný zpravodaj");
            mv.setViewName("redirect:/index");
            return mv;
        }

        mv.addObject("action", "nase-cinnost");
        mv.addObject("title", "Zpravodaj | Naše činnost");
        mv.addObject("article", articleOptional.get());
        return mv;
    }

    @RequestMapping("/pomucky")
    public ModelAndView rehabilitacniPobyt() {
        ModelAndView mv = new ModelAndView("nase-cinnost/pomucky");
        mv.addObject("action", "nase-cinnost");
        mv.addObject("title", "Pomůcky | Naše činnost");
        return mv;
    }
}
