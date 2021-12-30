package cz.laryngektomie.controller.admin.forum;

import cz.laryngektomie.helper.ForumHelper;
import cz.laryngektomie.model.forum.Post;
import cz.laryngektomie.service.forum.PostService;
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

import static cz.laryngektomie.helper.Const.*;
import static cz.laryngektomie.helper.UrlConst.*;

@Controller
@RequestMapping(ADMIN_PORADNA_URL + PRISPEVKY_URL)
public class PostController {

    private final PostService postService;
    private final TopicService topicService;
    private final UserService userService;

    public PostController(PostService postService, TopicService topicService, UserService userService) {
        this.postService = postService;
        this.topicService = topicService;
        this.userService = userService;
    }

    @GetMapping()
    public ModelAndView prispevky(@RequestParam(value = PAGE, defaultValue = DEFAULT_VALUE_1) int page, @RequestParam Optional<String> query) {
        ModelAndView mv = new ModelAndView(ADMIN_PORADNA_URL + PRISPEVKY_URL);

        int pageNumber = ForumHelper.resolvePageNumber(page);

        String queryString = query.orElse(EMPTY_STRING);

        Page<Post> posts = postService.findAllSearch(pageNumber, ForumHelper.ITEMS_ON_PAGE, CREATE_DATE_TIME, false, queryString);

        mv.addObject(PAGE_NUMBERS, ForumHelper.getListOfPageNumbers(posts.getTotalPages(), pageNumber));
        mv.addObject(CURRENT_PAGE, pageNumber);
        mv.addObject(POSTS, posts);
        return mv;
    }

    @GetMapping(VYTVORIT_URL)
    public String vytvoritGet(Model model) {
        model.addAttribute(POST, new Post());
        model.addAttribute(TOPICS, topicService.findAll());
        return ADMIN_PORADNA_URL + PRISPEVKY_URL + VYTVORIT_URL;

    }

    @PostMapping(VYTVORIT_URL)
    public ModelAndView vytvoritPost(@ModelAttribute(POST) @Valid Post post, BindingResult result, Principal principal) {
        ModelAndView mv = new ModelAndView();

        if (result.hasErrors()) {
            mv.addObject(POST, post);
            mv.addObject(MESSAGE_ERROR, SPATNE_VYPLNENE_POLE_ERROR_MSG);
            mv.setViewName(ADMIN_PORADNA_URL + PRISPEVKY_URL + VYTVORIT_URL);
            return mv;
        }

        if (post.getTopic() == null) {
            mv.addObject(TOPICS, topicService.findAll());
            mv.addObject(POST, post);
            mv.setViewName(ADMIN_PORADNA_URL + PRISPEVKY_URL + VYTVORIT_URL);
            mv.addObject(MESSAGE_ERROR, "Prosím vyberte téma pro přispěvek");
            return mv;
        }

        post.setUser(userService.findByUsername(principal.getName()));

        postService.saveOrUpdate(post);
        mv.addObject(MESSAGE_SUCCESS, "Příspěvek byl přidán");
        mv.setViewName(REDIRECT_URL + ADMIN_PORADNA_URL + PRISPEVKY_URL);
        return mv;
    }

    @GetMapping(UPRAVIT_URL + ID_PATH_VAR)
    public ModelAndView upravitGet(@PathVariable long id) {
        ModelAndView mv = new ModelAndView(ADMIN_PORADNA_URL + PRISPEVKY_URL + UPRAVIT_URL);
        Optional<Post> postOptional = postService.findById(id);

        if (!postOptional.isPresent()) {
            mv.addObject(MESSAGE_ERROR, "Požadovaný příspěvek neexistuje.");
            mv.setViewName(REDIRECT_URL + ADMIN_PORADNA_URL + PRISPEVKY_URL);
            return mv;
        }

        mv.addObject(POST, postOptional.get());
        mv.addObject(TOPICS, topicService.findAll());
        return mv;
    }

    @PostMapping(UPRAVIT_URL)
    public ModelAndView upravitPost(@ModelAttribute(POST) @Valid Post post, BindingResult result) {
        ModelAndView mv = new ModelAndView();

        if (result.hasErrors()) {
            mv.setViewName(ADMIN_PORADNA_URL + PRISPEVKY_URL + UPRAVIT_URL);
            mv.addObject(POST, post);
            mv.addObject(TOPICS, topicService.findAll());
            mv.addObject(MESSAGE_ERROR, SPATNE_VYPLNENA_POLE_ERROR_MSG);
            return mv;
        }

        post.setUser(userService.findByUsername(post.getUser().getUsername()));
        post.setTopic(topicService.findByName(post.getTopic().getName()).get());
        postService.saveOrUpdate(post);
        mv.addObject(MESSAGE_SUCCESS, "Příspěvek byl upraven.");
        mv.setViewName(REDIRECT_URL + ADMIN_PORADNA_URL + PRISPEVKY_URL);
        return mv;
    }


    @GetMapping(SMAZAT_URL + ID_PATH_VAR)
    public ModelAndView smazat(@PathVariable long id) {
        ModelAndView mv = new ModelAndView();
        Optional<Post> postOptional = postService.findById(id);

        if (!postOptional.isPresent()) {
            mv.addObject(MESSAGE_ERROR, "Příspěvek s id: " + id + " neexistuje.");
            mv.setViewName(REDIRECT_URL + ADMIN_PORADNA_URL + PRISPEVKY_URL);
            return mv;
        }

        postService.delete(postOptional.get());
        mv.addObject(MESSAGE_SUCCESS, "Příspěvek byl smazán.");
        mv.setViewName(REDIRECT_URL + ADMIN_PORADNA_URL + PRISPEVKY_URL);
        return mv;
    }
}

