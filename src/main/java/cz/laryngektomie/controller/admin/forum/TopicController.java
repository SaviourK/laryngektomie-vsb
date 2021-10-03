package cz.laryngektomie.controller.admin.forum;

import cz.laryngektomie.helper.ForumHelper;
import cz.laryngektomie.model.forum.Topic;
import cz.laryngektomie.service.forum.CategoryService;
import cz.laryngektomie.service.forum.TopicService;
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
@RequestMapping("/admin/poradna/temata")
public class TopicController {

    private final TopicService topicService;
    private final CategoryService categoryService;
    private final UserService userService;

    public TopicController(TopicService topicService, CategoryService categoryService, UserService userService) {
        this.topicService = topicService;
        this.categoryService = categoryService;
        this.userService = userService;
    }

    @GetMapping()
    public ModelAndView temata(@RequestParam(value = "page", defaultValue = "1") int page, @RequestParam Optional<String> query) {
        ModelAndView mv = new ModelAndView("admin/poradna/temata");

        int pageNumber = page < 0 ? 1 : page;

        String queryString = query.orElse("");

        Page<Topic> topics = topicService.findAllSearch(pageNumber, ForumHelper.itemsOnPage, "createDateTime", false, queryString);

        mv.addObject("pageNumbers", ForumHelper.getListOfPageNumbers(topics.getTotalPages(), pageNumber));
        mv.addObject("currentPage", pageNumber);
        mv.addObject("topics", topics);
        return mv;
    }


    @GetMapping("/vytvorit")
    public String vytvoritGet(Model model) {
        model.addAttribute("topic", new Topic());
        model.addAttribute("categories", categoryService.findAll());
        return "admin/poradna/temata/vytvorit";
    }

    @PostMapping("/vytvorit")
    public ModelAndView vytvoritPost(@ModelAttribute("topic") @Valid Topic topic, BindingResult result, Principal principal) {
        ModelAndView mv = new ModelAndView();

        if (topic.getCategory() == null) {
            mv.setViewName("admin/poradna/temata/vytvorit");
            mv.addObject("categories", categoryService.findAll());
            mv.addObject("topic", topic);
            mv.addObject("messageError", "Prosím vyberte kategorii pro téma");
            return mv;
        }

        if (result.hasErrors()) {
            mv.setViewName("admin/poradna/kategorie/vytvorit");
            mv.addObject("topic", topic);
            mv.addObject("categories", categoryService.findAll());
            mv.addObject("messageError", "Špatně vyplněná pole.");
            return mv;
        }

        topic.setUser(userService.findByUsername(principal.getName()));

        topicService.saveOrUpdate(topic);
        mv.addObject("messageSuccess", "Téma " + topic.getName() + " bylo úspěšně přidáno.");
        mv.setViewName("redirect:/admin/poradna/temata");
        return mv;
    }

    @GetMapping("/upravit/{id}")
    public ModelAndView upravitGet(@PathVariable long id) {
        ModelAndView mv = new ModelAndView("admin/poradna/temata/upravit");
        Optional<Topic> topicOptional = topicService.findById(id);
        if (!topicOptional.isPresent()) {
            mv.addObject("messageError", "Požadované téma neexistuje.");
            mv.setViewName("redirect:/admin/poradna/temata");
            return mv;
        }

        mv.addObject("topic", topicOptional.get());
        mv.addObject("categories", categoryService.findAll());
        return mv;
    }

    @PostMapping("/upravit")
    public ModelAndView upravitPost(@ModelAttribute("topic") @Valid Topic topic, BindingResult result) {
        ModelAndView mv = new ModelAndView();
        if (result.hasErrors()) {
            mv.setViewName("admin/poradna/temata/upravit");
            mv.addObject("topic", topic);
            mv.addObject("categories", categoryService.findAll());
            mv.addObject("messageError", "Špatně vyplněná pole.");
            return mv;
        }

        topic.setUser(userService.findByUsername(topic.getUser().getUsername()));

        topicService.saveOrUpdate(topic);
        mv.addObject("messageSuccess", "Téma:" + topic.getName() + " bylo upraveno.");
        mv.setViewName("redirect:/admin/poradna/temata");
        return mv;
    }

    @GetMapping("/smazat/{id}")
    public ModelAndView smazat(@PathVariable long id) {
        ModelAndView mv = new ModelAndView();
        Optional<Topic> topicOptional = topicService.findById(id);

        if (!topicOptional.isPresent()) {
            mv.addObject("messageError", "Téma s id: " + id + " neexistuje.");
            mv.setViewName("redirect:/admin/poradna/temata");
            return mv;
        }

        topicService.delete(topicOptional.get());
        mv.addObject("messageSuccess", "Téma " + topicOptional.get().getName() + " bylo smazáno.");
        mv.setViewName("redirect:/admin/poradna/temata");
        return mv;
    }
}
