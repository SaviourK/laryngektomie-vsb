package cz.laryngektomie.controller.home;

import cz.laryngektomie.model.news.News;
import cz.laryngektomie.service.news.NewsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
@RequestMapping("nase-cinnost")
public class NaseCinnostController {

    private final NewsService newsService;

    public NaseCinnostController(NewsService newsService) {
        this.newsService = newsService;
    }

    @RequestMapping("/novinky")
    public ModelAndView aktuality() {
        return new ModelAndView("redirect:/novinky");
    }

    @RequestMapping("/zpravodaj")
    public ModelAndView zpravodaj() {
        ModelAndView mv = new ModelAndView("nase-cinnost/zpravodaj");
        Optional<News> newsOptional = newsService.findLastNewsletter();

        if (!newsOptional.isPresent()) {
            mv.addObject("messageError", "Zatím žádný zpravodaj");
            mv.setViewName("redirect:/index");
            return mv;
        }

        mv.addObject("action", "nase-cinnost");
        mv.addObject("title", "Zpravodaj | Naše činnost");
        mv.addObject("news", newsOptional.get());
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
