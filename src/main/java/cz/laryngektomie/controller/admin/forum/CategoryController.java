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

@Controller
@RequestMapping("/admin/poradna/kategorie")
public class CategoryController {

    private final CategoryService categoryService;
    private final UserService userService;

    public CategoryController(CategoryService categoryService, UserService userService) {
        this.categoryService = categoryService;
        this.userService = userService;
    }

    @GetMapping()
    public ModelAndView kategorie(@RequestParam(value = "page", defaultValue = "1") int page) {
        ModelAndView mv = new ModelAndView("admin/poradna/kategorie");

        int pageNumber = page < 0 ? 1 : page;

        Page<Category> categories = categoryService.findAll(pageNumber, ForumHelper.itemsOnPage, "createDateTime", false);

        mv.addObject("pageNumbers", ForumHelper.getListOfPageNumbers(categories.getTotalPages(), pageNumber));
        mv.addObject("currentPage", pageNumber);
        mv.addObject("categories", categories);
        return mv;
    }


    @GetMapping("/vytvorit")
    public String vytvoritGet(Model model) {
        model.addAttribute("category", new Category());
        return "admin/poradna/kategorie/vytvorit";

    }

    @PostMapping("/vytvorit")
    public ModelAndView vytvoritPost(@ModelAttribute("category") @Valid Category category, BindingResult result, Principal principal) {
        ModelAndView mv = new ModelAndView();

        if (result.hasErrors()) {
            mv.setViewName("admin/poradna/kategorie/vytvorit");
            mv.addObject("category", category);
            mv.addObject("messageError", "Špatně vyplněná pole.");
            return mv;
        }

        if (categoryService.findByName(category.getName()).isPresent()) {
            mv.setViewName("admin/poradna/kategorie/vytvorit");
            mv.addObject("category", category);
            mv.addObject("messageError", "Název kategorie " + category.getName() + " je již obsazen. Zvolte jiný název.");
            return mv;
        }


        category.setUser(userService.findByUsername(principal.getName()));

        categoryService.saveOrUpdate(category);
        mv.addObject("messageSuccess", "Kategorie " + category.getName() + " byla úspěšně přidána.");
        mv.setViewName("redirect:/admin/poradna/kategorie");
        return mv;
    }

    @GetMapping("/upravit/{id}")
    public ModelAndView upravitGet(@PathVariable long id) {
        ModelAndView mv = new ModelAndView("admin/poradna/kategorie/upravit");
        Optional<Category> categoryOptional = categoryService.findById(id);
        if (!categoryOptional.isPresent()) {
            mv.addObject("messageError", "Požadovaná kategorie neexistuje.");
            mv.setViewName("redirect:/admin/poradna/kategorie");
            return mv;
        }
        mv.addObject("category", categoryOptional.get());
        return mv;
    }

    @PostMapping("/upravit")
    public ModelAndView upravitPost(@ModelAttribute("category") @Valid Category category, BindingResult result) {
        ModelAndView mv = new ModelAndView();
        if (result.hasErrors()) {
            mv.setViewName("admin/poradna/kategorie/upravit");
            mv.addObject("category", category);
            mv.addObject("messageError", "Špatně vyplněná pole.");
            return mv;
        }

        if (categoryService.findByName(category.getName()).isPresent()) {
            mv.setViewName("admin/poradna/kategorie/upravit");
            mv.addObject("category", category);
            mv.addObject("messageError", "Název kategorie " + category.getName() + " je již obsazen. Zvolte jiný název.");
            return mv;
        }
        category.setUser(userService.findByUsername(category.getUser().getUsername()));

        categoryService.saveOrUpdate(category);

        mv.addObject("messageSuccess", "Kategorie:" + category.getName() + " byla upravena.");
        mv.setViewName("redirect:/admin/poradna/kategorie");
        return mv;
    }

    @GetMapping("/smazat/{id}")
    public ModelAndView smazat(@PathVariable long id) {
        ModelAndView mv = new ModelAndView();
        Optional<Category> categoryOptional = categoryService.findById(id);

        if (!categoryOptional.isPresent()) {
            mv.addObject("messageError", "Kategorie s id: " + id + " neexistuje.");
            mv.setViewName("redirect:/admin/poradna/kategorie");
            return mv;
        }

        categoryService.delete(categoryOptional.get());
        mv.addObject("messageSuccess", "Kategorie " + categoryOptional.get().getName() + " byla smazána.");
        mv.setViewName("redirect:/admin/poradna/kategorie");
        return mv;
    }
}
