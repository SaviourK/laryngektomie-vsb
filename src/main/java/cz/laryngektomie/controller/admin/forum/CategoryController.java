package cz.laryngektomie.controller.admin.forum;

import cz.laryngektomie.helper.ForumHelper;
import cz.laryngektomie.model.forum.Category;
import cz.laryngektomie.service.forum.CategoryService;
import cz.laryngektomie.service.security.UserService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Optional;

import static cz.laryngektomie.helper.Const.*;
import static cz.laryngektomie.helper.ForumHelper.resolvePageNumber;
import static cz.laryngektomie.helper.UrlConst.*;

@Controller
@RequestMapping(ADMIN_PORADNA_URL + KATEGORIE_URL)
public class CategoryController {

    private final CategoryService categoryService;
    private final UserService userService;

    public CategoryController(CategoryService categoryService, UserService userService) {
        this.categoryService = categoryService;
        this.userService = userService;
    }

    @GetMapping()
    public ModelAndView kategorie(@RequestParam(value = PAGE, defaultValue = DEFAULT_VALUE_1) int page) {
        ModelAndView mv = new ModelAndView(ADMIN + PORADNA_URL + KATEGORIE_URL);

        int pageNumber = resolvePageNumber(page);

        Page<Category> categories = categoryService.findAll(pageNumber, ForumHelper.ITEMS_ON_PAGE, CREATE_DATE_TIME, false);

        mv.addObject(PAGE_NUMBERS, ForumHelper.getListOfPageNumbers(categories.getTotalPages(), pageNumber));
        mv.addObject(CURRENT_PAGE, pageNumber);
        mv.addObject(CATEGORIES, categories);
        return mv;
    }

    @GetMapping(VYTVORIT_URL)
    public String vytvoritGet(Model model) {
        model.addAttribute(CATEGORY, new Category());
        return ADMIN + PORADNA_URL + KATEGORIE_URL + VYTVORIT_URL;
    }

    @PostMapping(VYTVORIT_URL)
    public ModelAndView vytvoritPost(@ModelAttribute(CATEGORY) @Valid Category category, BindingResult result, Principal principal) {
        ModelAndView mv = new ModelAndView();

        if (result.hasErrors()) {
            mv.setViewName(ADMIN + PORADNA_URL + KATEGORIE_URL + VYTVORIT_URL);
            mv.addObject(CATEGORY, category);
            mv.addObject(MESSAGE_ERROR, SPATNE_VYPLNENA_POLE_ERROR_MSG);
            return mv;
        }

        if (categoryService.findByName(category.getName()).isPresent()) {
            mv.setViewName(ADMIN + PORADNA_URL + KATEGORIE_URL + VYTVORIT_URL);
            mv.addObject(CATEGORY, category);
            mv.addObject(MESSAGE_ERROR, "Název kategorie " + category.getName() + " je již obsazen. Zvolte jiný název.");
            return mv;
        }

        category.setUser(userService.findByUsername(principal.getName()));

        categoryService.saveOrUpdate(category);
        mv.addObject(MESSAGE_SUCCESS, "Kategorie " + category.getName() + " byla úspěšně přidána.");
        mv.setViewName(REDIRECT_URL + ADMIN_PORADNA_URL + KATEGORIE_URL);
        return mv;
    }

    @GetMapping(UPRAVIT_URL + ID_PATH_VAR)
    public ModelAndView upravitGet(@PathVariable long id) {
        ModelAndView mv = new ModelAndView(ADMIN + PORADNA_URL + KATEGORIE_URL + UPRAVIT_URL);
        Optional<Category> categoryOptional = categoryService.findById(id);
        if (!categoryOptional.isPresent()) {
            mv.addObject(MESSAGE_ERROR, "Požadovaná kategorie neexistuje.");
            mv.setViewName(REDIRECT_URL + ADMIN_PORADNA_URL + KATEGORIE_URL);
            return mv;
        }
        mv.addObject(CATEGORY, categoryOptional.get());
        return mv;
    }

    @PostMapping(UPRAVIT_URL)
    public ModelAndView upravitPost(@ModelAttribute(CATEGORY) @Valid Category category, BindingResult result) {
        ModelAndView mv = new ModelAndView();
        if (result.hasErrors()) {
            mv.setViewName(ADMIN + PORADNA_URL + KATEGORIE_URL + UPRAVIT_URL);
            mv.addObject(CATEGORY, category);
            mv.addObject(MESSAGE_ERROR, SPATNE_VYPLNENA_POLE_ERROR_MSG);
            return mv;
        }

        if (categoryService.findByName(category.getName()).isPresent()) {
            mv.setViewName(ADMIN + PORADNA_URL + KATEGORIE_URL + UPRAVIT_URL);
            mv.addObject(CATEGORY, category);
            mv.addObject(MESSAGE_ERROR, "Název kategorie " + category.getName() + " je již obsazen. Zvolte jiný název.");
            return mv;
        }
        category.setUser(userService.findByUsername(category.getUser().getUsername()));

        categoryService.saveOrUpdate(category);

        mv.addObject(MESSAGE_SUCCESS, "Kategorie:" + category.getName() + " byla upravena.");
        mv.setViewName(REDIRECT_URL + ADMIN_PORADNA_URL + KATEGORIE_URL);
        return mv;
    }

    @GetMapping(SMAZAT_URL + ID_PATH_VAR)
    public ModelAndView smazat(@PathVariable long id) {
        ModelAndView mv = new ModelAndView();
        Optional<Category> categoryOptional = categoryService.findById(id);

        if (!categoryOptional.isPresent()) {
            mv.addObject(MESSAGE_ERROR, "Kategorie s id: " + id + " neexistuje.");
            mv.setViewName(REDIRECT_URL + ADMIN_PORADNA_URL + KATEGORIE_URL);
            return mv;
        }

        categoryService.delete(categoryOptional.get());
        mv.addObject(MESSAGE_SUCCESS, "Kategorie " + categoryOptional.get().getName() + " byla smazána.");
        mv.setViewName(REDIRECT_URL + ADMIN_PORADNA_URL + KATEGORIE_URL);
        return mv;
    }
}
