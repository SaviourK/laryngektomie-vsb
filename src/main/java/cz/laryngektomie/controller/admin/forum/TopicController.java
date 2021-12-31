package cz.laryngektomie.controller.admin.forum;

import cz.laryngektomie.converter.UserConverter;
import cz.laryngektomie.helper.ForumHelper;
import cz.laryngektomie.model.forum.Topic;
import cz.laryngektomie.service.forum.CategoryService;
import cz.laryngektomie.service.forum.TopicService;
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
import static cz.laryngektomie.helper.UrlConst.*;

@Controller
@RequestMapping(ADMIN_PORADNA_URL + TEMATA_URL)
public class TopicController {

    private final TopicService topicService;
    private final CategoryService categoryService;
    private final UserConverter userConverter;

    public TopicController(TopicService topicService, CategoryService categoryService, UserConverter userConverter) {
        this.topicService = topicService;
        this.categoryService = categoryService;
        this.userConverter = userConverter;
    }

    @GetMapping()
    public ModelAndView topics(@RequestParam(value = PAGE, defaultValue = DEFAULT_VALUE_1) int page, @RequestParam Optional<String> query) {
        ModelAndView mv = new ModelAndView(ADMIN_PORADNA_URL + TEMATA_URL);

        int pageNumber = ForumHelper.resolvePageNumber(page);

        String queryString = query.orElse(EMPTY_STRING);

        Page<Topic> topics = topicService.findAllSearch(pageNumber, ForumHelper.ITEMS_ON_PAGE, CREATE_DATE_TIME, false, queryString);

        mv.addObject(PAGE_NUMBERS, ForumHelper.getListOfPageNumbers(topics.getTotalPages(), pageNumber));
        mv.addObject(CURRENT_PAGE, pageNumber);
        mv.addObject(TOPICS, topics);
        return mv;
    }


    @GetMapping(VYTVORIT_URL)
    public String createGet(Model model) {
        model.addAttribute(TOPIC, new Topic());
        model.addAttribute(CATEGORIES, categoryService.findAll());
        return ADMIN_PORADNA_URL + TEMATA_URL + VYTVORIT_URL;
    }

    @PostMapping(VYTVORIT_URL)
    public ModelAndView createPost(@ModelAttribute(TOPIC) @Valid Topic topic, BindingResult result, Principal principal) {
        ModelAndView mv = new ModelAndView();

        if (topic.getCategory() == null) {
            mv.setViewName(ADMIN_PORADNA_URL + TEMATA_URL + VYTVORIT_URL);
            mv.addObject(CATEGORIES, categoryService.findAll());
            mv.addObject(TOPIC, topic);
            mv.addObject(MESSAGE_ERROR, "Prosím vyberte kategorii pro téma");
            return mv;
        }

        if (result.hasErrors()) {
            mv.setViewName(ADMIN_PORADNA_URL + TEMATA_URL + VYTVORIT_URL);
            mv.addObject(TOPIC, topic);
            mv.addObject(CATEGORIES, categoryService.findAll());
            mv.addObject(MESSAGE_ERROR, SPATNE_VYPLNENA_POLE_ERROR_MSG);
            return mv;
        }

        topic.setUser(userConverter.convert(principal.getName()));

        topicService.saveOrUpdate(topic);
        mv.addObject(MESSAGE_SUCCESS, "Téma " + topic.getName() + " bylo úspěšně přidáno.");
        mv.setViewName(REDIRECT_URL + ADMIN_PORADNA_URL + TEMATA_URL);
        return mv;
    }

    @GetMapping(UPRAVIT_URL + ID_PATH_VAR)
    public ModelAndView updateGet(@PathVariable long id) {
        ModelAndView mv = new ModelAndView(ADMIN_PORADNA_URL + TEMATA_URL + UPRAVIT_URL);
        Optional<Topic> topicOptional = topicService.findById(id);
        if (!topicOptional.isPresent()) {
            mv.addObject(MESSAGE_ERROR, "Požadované téma neexistuje.");
            mv.setViewName(REDIRECT_URL + ADMIN_PORADNA_URL + TEMATA_URL);
            return mv;
        }

        mv.addObject(TOPIC, topicOptional.get());
        mv.addObject(CATEGORIES, categoryService.findAll());
        return mv;
    }

    @PostMapping(UPRAVIT_URL)
    public ModelAndView updatePost(@ModelAttribute(TOPIC) @Valid Topic topic, BindingResult result) {
        ModelAndView mv = new ModelAndView();
        if (result.hasErrors()) {
            mv.setViewName(ADMIN_PORADNA_URL + TEMATA_URL + UPRAVIT_URL);
            mv.addObject(TOPIC, topic);
            mv.addObject(CATEGORIES, categoryService.findAll());
            mv.addObject(MESSAGE_ERROR, SPATNE_VYPLNENA_POLE_ERROR_MSG);
            return mv;
        }

        topic.setUser(userConverter.convert(topic.getUser().getUsername()));

        topicService.saveOrUpdate(topic);
        mv.addObject(MESSAGE_SUCCESS, "Téma:" + topic.getName() + " bylo upraveno.");
        mv.setViewName(REDIRECT_URL + ADMIN_PORADNA_URL + TEMATA_URL);
        return mv;
    }

    @GetMapping(SMAZAT_URL + ID_PATH_VAR)
    public ModelAndView delete(@PathVariable long id) {
        ModelAndView mv = new ModelAndView();
        Optional<Topic> topicOptional = topicService.findById(id);

        if (!topicOptional.isPresent()) {
            mv.addObject(MESSAGE_ERROR, "Téma s id: " + id + " neexistuje.");
            mv.setViewName(REDIRECT_URL + ADMIN_PORADNA_URL + TEMATA_URL);
            return mv;
        }

        topicService.delete(topicOptional.get());
        mv.addObject(MESSAGE_SUCCESS, "Téma " + topicOptional.get().getName() + " bylo smazáno.");
        mv.setViewName(REDIRECT_URL + ADMIN_PORADNA_URL + TEMATA_URL);
        return mv;
    }
}
