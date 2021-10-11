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

    @GetMapping({"/admin/clanky", "/clanky"})
    public ModelAndView clanky(@RequestParam(value = "page", defaultValue = "1") int page, HttpServletRequest request, @RequestParam Optional<String> query) {
        ModelAndView mv = new ModelAndView("clanky");

        int pageNumber = page < 0 ? 1 : page;

        if (request.getRequestURI().contains("/admin/clanky")) {
            mv.setViewName("admin/clanky");
        }

        String queryString = query.orElse("");

        Page<Article> article = articleService.findAllSearch(pageNumber, ForumHelper.itemsOnPage, "createDateTime", false, queryString);


        mv.addObject("action", "nase-cinnost");
        mv.addObject("pageNumbers", ForumHelper.getListOfPageNumbers(article.getTotalPages(), pageNumber));
        mv.addObject("currentPage", pageNumber);
        mv.addObject("article", article);
        return mv;
    }

    @GetMapping("/clanky/{url}")
    public ModelAndView detail(@PathVariable("url") String url) {
        ModelAndView mv = new ModelAndView();
        Optional<Article> articleOptional = articleService.findByUrl(url);

        if (!articleOptional.isPresent()) {
            mv.addObject("messageError", "Požadovaný článek neexstuje.");
            mv.setViewName("redirect:/admin/clanky");
            return mv;
        }

        mv.addObject("article", articleOptional.get());
        mv.setViewName("admin/clanky/detail");
        return mv;
    }

    @RequestMapping("/admin/clanky/vytvorit")
    public ModelAndView vytvoritGet() {
        ModelAndView mv = new ModelAndView("admin/clanky/vytvorit");
        mv.addObject("article", new Article());
        mv.addObject("articleTypes", articleTypeService.findAll());
        return mv;
    }

    @PostMapping("/admin/clanky/vytvorit")
    public ModelAndView vytvoritPost(@Valid @ModelAttribute("article") Article article, @RequestParam("files") List<MultipartFile> files, BindingResult result, Principal principal) throws IOException {
        ModelAndView mv = new ModelAndView();

        if (result.hasErrors() || article.getArticleType() == null) {
            mv.setViewName("admin/clanky/vytvorit");
            mv.addObject("article", article);
            mv.addObject("articleTypes", articleTypeService.findAll());
            mv.addObject("messageError", "Špatně vyplněná pole.");
            return mv;
        }

        List<Image> images = imageService.saveImages(files);
        article.setImages(images);
        article.setUser(userService.findByUsername(principal.getName()));
        articleService.saveOrUpdate(article);
        mv.addObject("messageSuccess", "Článek " + article.getName() + " byl úspěšně přidán.");
        mv.setViewName("redirect:/admin/clanky");
        return mv;
    }

    @GetMapping("/admin/clanky/upravit/{id}")
    public ModelAndView upravitGet(@PathVariable long id) {
        ModelAndView mv = new ModelAndView("admin/clanky/upravit");
        Optional<Article> articleOptional = articleService.findById(id);
        if (!articleOptional.isPresent()) {
            mv.addObject("messageError", "Požadovaný článek neexistuje.");
            mv.setViewName("redirect:/admin/clanky");
            return mv;
        }

        mv.addObject("articleTypes", articleTypeService.findAll());
        mv.addObject("article", articleOptional.get());
        return mv;
    }

    @PostMapping("/admin/clanky/upravit")
    public ModelAndView upravitPost(@Valid @ModelAttribute("article") Article article, BindingResult result) {
        ModelAndView mv = new ModelAndView();
        if (result.hasErrors()) {
            mv.setViewName("admin/clanky/upravit");
            mv.addObject("article", article);
            mv.addObject("articleTypes", articleTypeService.findAll());
            mv.addObject("messageError", "Špatně vyplněná pole.");
            return mv;
        }

        article.setUser(userService.findByUsername(article.getUser().getUsername()));

        articleService.saveOrUpdate(article);

        mv.addObject("messageSuccess", "Článek:" + article.getName() + " byl upraven.");
        mv.setViewName("redirect:/admin/clanky");
        return mv;
    }

    @GetMapping("/admin/clanky/smazat/{id}")
    public ModelAndView smazat(@PathVariable long id) {
        ModelAndView mv = new ModelAndView();
        Optional<Article> articleOptional = articleService.findById(id);
        if (!articleOptional.isPresent()) {
            mv.addObject("messageError", "Článek s id: " + id + " neexistuje.");
            mv.setViewName("redirect:/admin/clanky");
            return mv;
        }

        articleService.delete(articleOptional.get());
        mv.addObject("messageSuccess", "Článek " + articleOptional.get().getName() + " byla smazána.");
        mv.setViewName("redirect:/admin/clanky");
        return mv;
    }
}
