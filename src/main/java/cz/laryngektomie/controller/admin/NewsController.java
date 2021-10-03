package cz.laryngektomie.controller.admin;

import cz.laryngektomie.helper.ForumHelper;
import cz.laryngektomie.model.news.Image;
import cz.laryngektomie.model.news.News;
import cz.laryngektomie.service.news.ImageService;
import cz.laryngektomie.service.news.NewsService;
import cz.laryngektomie.service.news.NewsTypeService;
import cz.laryngektomie.service.security.UserService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class NewsController {

    private final NewsService newsService;
    private final NewsTypeService newsTypeService;
    private final UserService userService;
    private final ImageService imageService;

    public NewsController(NewsService newsService, NewsTypeService newsTypeService, UserService userService, ImageService imageService) {
        this.newsService = newsService;
        this.newsTypeService = newsTypeService;
        this.userService = userService;
        this.imageService = imageService;
    }

    @GetMapping({"/admin/novinky", "/novinky"})
    public ModelAndView novinky(@RequestParam(value = "page", defaultValue = "1") int page, HttpServletRequest request, @RequestParam Optional<String> query) {
        ModelAndView mv = new ModelAndView("novinky");

        int pageNumber = page < 0 ? 1 : page;

        if (request.getRequestURI().contains("/admin/novinky")) {
            mv.setViewName("admin/novinky");
        }

        String queryString = query.orElse("");

        Page<News> news = newsService.findAllSearch(pageNumber, ForumHelper.itemsOnPage, "createDateTime", false, queryString);


        mv.addObject("action", "nase-cinnost");
        mv.addObject("pageNumbers", ForumHelper.getListOfPageNumbers(news.getTotalPages(), pageNumber));
        mv.addObject("currentPage", pageNumber);
        mv.addObject("news", news);
        return mv;
    }

    @GetMapping("/novinky/{url}")
    public ModelAndView detail(@PathVariable("url") String url) {
        ModelAndView mv = new ModelAndView();
        Optional<News> newsOptional = newsService.findByUrl(url);

        if (!newsOptional.isPresent()) {
            mv.addObject("messageError", "Požadovaná novinka neexstuje.");
            mv.setViewName("redirect:/admin/novinky");
            return mv;
        }

        mv.addObject("news", newsOptional.get());
        mv.setViewName("admin/novinky/detail");
        return mv;
    }

    @RequestMapping("/admin/novinky/vytvorit")
    public ModelAndView vytvoritGet() {
        ModelAndView mv = new ModelAndView("admin/novinky/vytvorit");
        mv.addObject("news", new News());
        mv.addObject("newsTypes", newsTypeService.findAll());
        return mv;
    }

    @PostMapping("/admin/novinky/vytvorit")
    public ModelAndView vytvoritPost(@Valid @ModelAttribute("news") News news, @RequestParam("files") List<MultipartFile> files, BindingResult result, Principal principal) throws IOException {
        ModelAndView mv = new ModelAndView();

        if (result.hasErrors() || news.getNewsType() == null) {
            mv.setViewName("admin/novinky/vytvorit");
            mv.addObject("news", news);
            mv.addObject("newsTypes", newsTypeService.findAll());
            mv.addObject("messageError", "Špatně vyplněná pole.");
            return mv;
        }

        List<Image> images = imageService.saveImages(files);
        news.setImages(images);
        news.setUser(userService.findByUsername(principal.getName()));
        newsService.saveOrUpdate(news);
        mv.addObject("messageSuccess", "Novinka " + news.getName() + " byla úspěšně přidána.");
        mv.setViewName("redirect:/admin/novinky");
        return mv;
    }

    @GetMapping("/admin/novinky/upravit/{id}")
    public ModelAndView upravitGet(@PathVariable long id) {
        ModelAndView mv = new ModelAndView("admin/novinky/upravit");
        Optional<News> optionalNews = newsService.findById(id);
        if (!optionalNews.isPresent()) {
            mv.addObject("messageError", "Požadovaná novinka neexistuje.");
            mv.setViewName("redirect:/admin/novinky");
            return mv;
        }

        mv.addObject("newsTypes", newsTypeService.findAll());
        mv.addObject("news", optionalNews.get());
        return mv;
    }

    @PostMapping("/admin/novinky/upravit")
    public ModelAndView upravitPost(@Valid @ModelAttribute("news") News news, BindingResult result) {
        ModelAndView mv = new ModelAndView();
        if (result.hasErrors()) {
            mv.setViewName("admin/novinky/upravit");
            mv.addObject("news", news);
            mv.addObject("newsTypes", newsTypeService.findAll());
            mv.addObject("messageError", "Špatně vyplněná pole.");
            return mv;
        }

        news.setUser(userService.findByUsername(news.getUser().getUsername()));

        newsService.saveOrUpdate(news);

        mv.addObject("messageSuccess", "Novinka:" + news.getName() + " byla upravena.");
        mv.setViewName("redirect:/admin/novinky");
        return mv;
    }

    @GetMapping("/admin/novinky/smazat/{id}")
    public ModelAndView smazat(@PathVariable long id) {
        ModelAndView mv = new ModelAndView();
        Optional<News> newsOptional = newsService.findById(id);
        if (!newsOptional.isPresent()) {
            mv.addObject("messageError", "Novinka s id: " + id + " neexistuje.");
            mv.setViewName("redirect:/admin/novinky");
            return mv;
        }

        newsService.delete(newsOptional.get());
        mv.addObject("messageSuccess", "Novinka " + newsOptional.get().getName() + " byla smazána.");
        mv.setViewName("redirect:/admin/novinky");
        return mv;
    }
}
