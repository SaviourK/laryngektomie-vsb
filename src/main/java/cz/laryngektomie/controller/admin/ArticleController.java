package cz.laryngektomie.controller.admin;

import cz.laryngektomie.helper.ForumHelper;
import cz.laryngektomie.model.article.Article;
import cz.laryngektomie.model.article.Image;
import cz.laryngektomie.service.article.ImageService;
import cz.laryngektomie.service.article.ArticleService;
import cz.laryngektomie.service.article.ArticleTypeService;
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
public class ArticleController {

    private final ArticleService articleService;
    private final ArticleTypeService articleTypeService;
    private final UserService userService;
    private final ImageService imageService;

    public ArticleController(ArticleService articleService, ArticleTypeService articleTypeService, UserService userService, ImageService imageService) {
        this.articleService = articleService;
        this.articleTypeService = articleTypeService;
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

        Page<Article> article = articleService.findAllSearch(pageNumber, ForumHelper.itemsOnPage, "createDateTime", false, queryString);


        mv.addObject("action", "nase-cinnost");
        mv.addObject("pageNumbers", ForumHelper.getListOfPageNumbers(article.getTotalPages(), pageNumber));
        mv.addObject("currentPage", pageNumber);
        mv.addObject("article", article);
        return mv;
    }

    @GetMapping("/novinky/{url}")
    public ModelAndView detail(@PathVariable("url") String url) {
        ModelAndView mv = new ModelAndView();
        Optional<Article> newsOptional = articleService.findByUrl(url);

        if (!newsOptional.isPresent()) {
            mv.addObject("messageError", "Požadovaná novinka neexstuje.");
            mv.setViewName("redirect:/admin/novinky");
            return mv;
        }

        mv.addObject("article", newsOptional.get());
        mv.setViewName("admin/novinky/detail");
        return mv;
    }

    @RequestMapping("/admin/novinky/vytvorit")
    public ModelAndView vytvoritGet() {
        ModelAndView mv = new ModelAndView("admin/novinky/vytvorit");
        mv.addObject("article", new Article());
        mv.addObject("articleTypes", articleTypeService.findAll());
        return mv;
    }

    @PostMapping("/admin/novinky/vytvorit")
    public ModelAndView vytvoritPost(@Valid @ModelAttribute("article") Article article, @RequestParam("files") List<MultipartFile> files, BindingResult result, Principal principal) throws IOException {
        ModelAndView mv = new ModelAndView();

        if (result.hasErrors() || article.getArticleType() == null) {
            mv.setViewName("admin/novinky/vytvorit");
            mv.addObject("article", article);
            mv.addObject("articleTypes", articleTypeService.findAll());
            mv.addObject("messageError", "Špatně vyplněná pole.");
            return mv;
        }

        List<Image> images = imageService.saveImages(files);
        article.setImages(images);
        article.setUser(userService.findByUsername(principal.getName()));
        articleService.saveOrUpdate(article);
        mv.addObject("messageSuccess", "Novinka " + article.getName() + " byla úspěšně přidána.");
        mv.setViewName("redirect:/admin/novinky");
        return mv;
    }

    @GetMapping("/admin/novinky/upravit/{id}")
    public ModelAndView upravitGet(@PathVariable long id) {
        ModelAndView mv = new ModelAndView("admin/novinky/upravit");
        Optional<Article> optionalNews = articleService.findById(id);
        if (!optionalNews.isPresent()) {
            mv.addObject("messageError", "Požadovaná novinka neexistuje.");
            mv.setViewName("redirect:/admin/novinky");
            return mv;
        }

        mv.addObject("articleTypes", articleTypeService.findAll());
        mv.addObject("article", optionalNews.get());
        return mv;
    }

    @PostMapping("/admin/novinky/upravit")
    public ModelAndView upravitPost(@Valid @ModelAttribute("article") Article article, BindingResult result) {
        ModelAndView mv = new ModelAndView();
        if (result.hasErrors()) {
            mv.setViewName("admin/novinky/upravit");
            mv.addObject("article", article);
            mv.addObject("articleTypes", articleTypeService.findAll());
            mv.addObject("messageError", "Špatně vyplněná pole.");
            return mv;
        }

        article.setUser(userService.findByUsername(article.getUser().getUsername()));

        articleService.saveOrUpdate(article);

        mv.addObject("messageSuccess", "Novinka:" + article.getName() + " byla upravena.");
        mv.setViewName("redirect:/admin/novinky");
        return mv;
    }

    @GetMapping("/admin/novinky/smazat/{id}")
    public ModelAndView smazat(@PathVariable long id) {
        ModelAndView mv = new ModelAndView();
        Optional<Article> newsOptional = articleService.findById(id);
        if (!newsOptional.isPresent()) {
            mv.addObject("messageError", "Novinka s id: " + id + " neexistuje.");
            mv.setViewName("redirect:/admin/novinky");
            return mv;
        }

        articleService.delete(newsOptional.get());
        mv.addObject("messageSuccess", "Novinka " + newsOptional.get().getName() + " byla smazána.");
        mv.setViewName("redirect:/admin/novinky");
        return mv;
    }
}
