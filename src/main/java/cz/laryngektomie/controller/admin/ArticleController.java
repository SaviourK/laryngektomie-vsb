package cz.laryngektomie.controller.admin;

import cz.laryngektomie.converter.UserConverter;
import cz.laryngektomie.helper.ForumHelper;
import cz.laryngektomie.model.article.Article;
import cz.laryngektomie.model.article.Image;
import cz.laryngektomie.service.article.ArticleService;
import cz.laryngektomie.service.article.ArticleTypeService;
import cz.laryngektomie.service.article.ImageService;
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

import static cz.laryngektomie.helper.Const.*;
import static cz.laryngektomie.helper.UrlConst.*;

@Controller
public class ArticleController {

    private final ArticleService articleService;
    private final ArticleTypeService articleTypeService;
    private final ImageService imageService;
    private final UserConverter userConverter;

    public ArticleController(ArticleService articleService, ArticleTypeService articleTypeService, ImageService imageService, UserConverter userConverter) {
        this.articleService = articleService;
        this.articleTypeService = articleTypeService;
        this.imageService = imageService;
        this.userConverter = userConverter;
    }

    @GetMapping({ADMIN_URL + CLANKY_URL, CLANKY_URL})
    public ModelAndView articles(@RequestParam(value = PAGE, defaultValue = DEFAULT_VALUE_1) int page, HttpServletRequest request, @RequestParam Optional<String> query) {
        ModelAndView mv = new ModelAndView(CLANKY);

        int pageNumber = ForumHelper.resolvePageNumber(page);

        if (request.getRequestURI().contains(ADMIN_URL + CLANKY_URL)) {
            mv.setViewName(ADMIN_URL + CLANKY_URL);
        }

        String queryString = query.orElse(EMPTY_STRING);

        Page<Article> article = articleService.findAllSearch(pageNumber, ForumHelper.ITEMS_ON_PAGE, CREATE_DATE_TIME, false, queryString);

        mv.addObject(ACTION, "nase-cinnost");
        mv.addObject(PAGE_NUMBERS, ForumHelper.getListOfPageNumbers(article.getTotalPages(), pageNumber));
        mv.addObject(CURRENT_PAGE, pageNumber);
        mv.addObject(ARTICLE, article);
        return mv;
    }

    @GetMapping(CLANKY_URL + URL_PATH_VAR)
    public ModelAndView article(@PathVariable(URL) String url) {
        ModelAndView mv = new ModelAndView();
        Optional<Article> articleOptional = articleService.findByUrl(url);

        if (articleOptional.isEmpty()) {
            mv.addObject(MESSAGE_ERROR, "Po??adovan?? ??l??nek neexstuje.");
            mv.setViewName(REDIRECT_URL + ADMIN_URL + CLANKY_URL);
            return mv;
        }

        mv.addObject(ARTICLE, articleOptional.get());
        mv.setViewName(ADMIN_URL + CLANKY_URL + DETAIL_URL);
        return mv;
    }

    @RequestMapping(ADMIN_URL + CLANKY_URL + VYTVORIT_URL)
    public ModelAndView createGet() {
        ModelAndView mv = new ModelAndView(ADMIN_URL + CLANKY_URL + VYTVORIT_URL);
        mv.addObject(ARTICLE, new Article());
        mv.addObject(ARTICLE_TYPES, articleTypeService.findAll());
        return mv;
    }

    @PostMapping(ADMIN_URL + CLANKY_URL + VYTVORIT_URL)
    public ModelAndView createPost(@Valid @ModelAttribute(ARTICLE) Article article, @RequestParam(FILES) List<MultipartFile> files, BindingResult result, Principal principal) throws IOException {
        ModelAndView mv = new ModelAndView();

        if (result.hasErrors() || article.getArticleType() == null) {
            mv.setViewName(ADMIN_URL + CLANKY_URL + VYTVORIT_URL);
            mv.addObject(ARTICLE, article);
            mv.addObject(ARTICLE_TYPES, articleTypeService.findAll());
            mv.addObject(MESSAGE_ERROR, SPATNE_VYPLNENA_POLE_ERROR_MSG);
            return mv;
        }

        List<Image> images = imageService.saveImages(files);
        article.setImages(images);
        article.setUser(userConverter.convert(principal.getName()));
        articleService.saveOrUpdate(article);
        mv.addObject(MESSAGE_SUCCESS, "??l??nek " + article.getName() + " byl ??sp????n?? p??id??n.");
        mv.setViewName(REDIRECT_URL + ADMIN_URL + CLANKY_URL);
        return mv;
    }

    @GetMapping(ADMIN_URL + CLANKY_URL + UPRAVIT_URL + ID_PATH_VAR)
    public ModelAndView updateGet(@PathVariable long id) {
        ModelAndView mv = new ModelAndView(ADMIN_URL + CLANKY_URL + UPRAVIT_URL);
        Optional<Article> articleOptional = articleService.findById(id);
        if (articleOptional.isEmpty()) {
            mv.addObject(MESSAGE_ERROR, "Po??adovan?? ??l??nek neexistuje.");
            mv.setViewName(REDIRECT_URL + ADMIN_URL + CLANKY_URL);
            return mv;
        }

        mv.addObject(ARTICLE_TYPES, articleTypeService.findAll());
        mv.addObject(ARTICLE, articleOptional.get());
        return mv;
    }

    @PostMapping(ADMIN_URL + CLANKY_URL + UPRAVIT_URL)
    public ModelAndView updatePost(@Valid @ModelAttribute(ARTICLE) Article article, BindingResult result) {
        ModelAndView mv = new ModelAndView();
        if (result.hasErrors()) {
            mv.setViewName(ADMIN_URL + CLANKY_URL + UPRAVIT_URL);
            mv.addObject(ARTICLE, article);
            mv.addObject(ARTICLE_TYPES, articleTypeService.findAll());
            mv.addObject(MESSAGE_ERROR, SPATNE_VYPLNENA_POLE_ERROR_MSG);
            return mv;
        }

        article.setUser(userConverter.convert(article.getUser().getUsername()));

        articleService.saveOrUpdate(article);

        mv.addObject(MESSAGE_SUCCESS, "??l??nek:" + article.getName() + " byl upraven.");
        mv.setViewName(REDIRECT_URL + ADMIN_URL + CLANKY_URL);
        return mv;
    }

    @GetMapping(ADMIN_URL + CLANKY_URL + SMAZAT_URL + ID_PATH_VAR)
    public ModelAndView delete(@PathVariable long id) {
        ModelAndView mv = new ModelAndView();
        Optional<Article> articleOptional = articleService.findById(id);
        if (articleOptional.isEmpty()) {
            mv.addObject(MESSAGE_ERROR, "??l??nek s id: " + id + " neexistuje.");
            mv.setViewName(REDIRECT_URL + ADMIN_URL + CLANKY_URL);
            return mv;
        }

        articleService.delete(articleOptional.get());
        mv.addObject(MESSAGE_SUCCESS, "??l??nek " + articleOptional.get().getName() + " byla smaz??na.");
        mv.setViewName(REDIRECT_URL + ADMIN_URL + CLANKY_URL);
        return mv;
    }
}
