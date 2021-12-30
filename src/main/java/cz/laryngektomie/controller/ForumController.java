package cz.laryngektomie.controller;

import cz.laryngektomie.dto.forum.TopicOrPost;
import cz.laryngektomie.helper.ForumHelper;
import cz.laryngektomie.model.forum.Category;
import cz.laryngektomie.model.forum.Post;
import cz.laryngektomie.model.forum.Topic;
import cz.laryngektomie.model.security.User;
import cz.laryngektomie.service.forum.CategoryService;
import cz.laryngektomie.service.forum.ForumService;
import cz.laryngektomie.service.forum.PostService;
import cz.laryngektomie.service.forum.TopicService;
import cz.laryngektomie.service.security.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static cz.laryngektomie.helper.Const.*;
import static cz.laryngektomie.helper.UrlConst.*;

@Controller
@RequestMapping(PORADNA_URL)
public class ForumController {

    private final CategoryService categoryService;
    private final PostService postService;
    private final TopicService topicService;
    private final UserService userService;
    private final ForumService forumService;

    public ForumController(CategoryService categoryService, PostService postService, TopicService topicService, UserService userService, ForumService forumService) {
        this.categoryService = categoryService;
        this.postService = postService;
        this.topicService = topicService;
        this.userService = userService;
        this.forumService = forumService;
    }

    @RequestMapping()
    public ModelAndView poradna(@RequestParam(value = "page", defaultValue = "1") int page) {
        ModelAndView mv = new ModelAndView("poradna");

        int pageNumber = page < 0 ? 1 : page;

        List<TopicOrPost> topicOrPostList = forumService.getTopicOrPostList();
        Page<Post> posts = postService.findAll(pageNumber, ForumHelper.ITEMS_ON_PAGE, "createDateTime", false);
        List<Topic> topics = topicService.findLatest();

        mv.addObject(CATEGORIES, categoryService.findAll());
        mv.addObject(PAGE_NUMBERS, ForumHelper.getListOfPageNumbers(posts.getTotalPages(), pageNumber));
        mv.addObject(CURRENT_PAGE, pageNumber);
        mv.addObject(TOPIC_OR_POST_LIST, topicOrPostList);
        mv.addObject(POST_COUNT, postService.findAll().size());
        mv.addObject(TOPIC_COUNT, topicService.findAll().size());
        mv.addObject(POSTS, posts);
        return mv;
    }

    @RequestMapping({CATEGORY_PATH_VAR})
    public ModelAndView temata(@PathVariable(CATEGORY_URL) String categoryUrl, @RequestParam(PAGE) Optional<Integer> page) {
        int pageNumber = page.orElse(1);

        ModelAndView mv = new ModelAndView();

        Optional<Category> categoryOptional = categoryService.findByUrl(categoryUrl);

        if (!categoryOptional.isPresent()) {
            mv.addObject(MESSAGE_ERROR, "Kategorie neexistuje");
            mv.setViewName(REDIRECT_URL + PORADNA_URL);
        }


        Pageable pagged = PageRequest.of(pageNumber - 1, ForumHelper.ITEMS_ON_PAGE, Sort.by(CREATE_DATE_TIME).descending());
        Page<Topic> topics = topicService.findByCategoryId(categoryOptional.get().getId(), pagged);

        int totalPages = topics.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
            mv.addObject(PAGE_NUMBERS, pageNumbers);
        }


        mv.addObject(CURRENT_PAGE, pageNumber);
        mv.addObject(CATEGORY, categoryOptional.get());
        mv.addObject(CATEGORIES, categoryService.findAll());

        mv.addObject(TOPICS, topics);
        mv.setViewName(PORADNA_URL + KATEGORIE_URL);
        return mv;
    }

    @GetMapping(TEMA_URL + ID_PATH_VAR)
    @ResponseBody
    public ModelAndView detailTema(@PathVariable(ID) Long id, @RequestParam(value = PAGE, defaultValue = DEFAULT_VALUE_1) int page) {
        ModelAndView mv = new ModelAndView();

        int pageNumber = ForumHelper.resolvePageNumber(page);


        Pageable pagged = PageRequest.of(pageNumber - 1, 10, Sort.by(CREATE_DATE_TIME).ascending());
        Optional<Topic> optionalTopic = topicService.findById(id);

        if (!optionalTopic.isPresent()) {
            mv.addObject(MESSAGE_ERROR, "Požadované téma neexstuje.");
            mv.setViewName(REDIRECT_URL + PORADNA_URL);
            return mv;
        }
        Page<Post> posts = postService.findByTopicId(optionalTopic.get().getId(), pagged);


        mv.addObject(PAGE_NUMBERS, ForumHelper.getListOfPageNumbers(posts.getTotalPages(), pageNumber));
        mv.addObject(CURRENT_PAGE, pageNumber);

        mv.addObject(TOPIC, optionalTopic.get());
        mv.addObject(POSTS, posts);
        mv.addObject(POST, new Post());
        mv.setViewName(PORADNA_URL + TEMA_URL);
        return mv;
    }

    @GetMapping(TEMA_NOVE_URL + CATEGORY_ID_PATH_VAR)
    public String vytvoritTemaGet(@PathVariable(CATEGORY_ID) Long categoryId, Model model) {
        Topic topic = new Topic();
        model.addAttribute(CATEGORY_ID, categoryId);
        model.addAttribute(TOPIC, topic);
        return PORADNA_URL + TEMA_NOVE_URL;
    }

    @PostMapping(TEMA_NOVE_URL + CATEGORY_ID_PATH_VAR)
    public ModelAndView vytvoritTemaPost(@Valid @ModelAttribute(TOPIC) Topic topic, BindingResult result, @PathVariable(CATEGORY_ID) Long categoryId, Principal principal) {
        ModelAndView mv = new ModelAndView();
        Optional<Category> categoryOptional = categoryService.findById(categoryId);
        if (!categoryOptional.isPresent()) {
            mv.setViewName(REDIRECT_URL + PORADNA_URL);
            return mv;
        } else {
            topic.setCategory(categoryOptional.get());
        }
        if (result.hasErrors()) {
            mv.setViewName(PORADNA_URL + TEMA_NOVE_URL + SLASH + categoryId);
            mv.addObject(TOPIC, topic);
            mv.addObject(MESSAGE_ERROR, SPATNE_VYPLNENA_POLE_ERROR_MSG);
            return mv;
        }

        if (principal == null) {
            mv.addObject(MESSAGE_ERROR, "Pro vytváření témat se musíte přihlásit");
            mv.setViewName(REDIRECT_URL + PORADNA_URL);
            return mv;
        } else {
            topic.setUser(userService.findByUsername(principal.getName()));
        }

        topicService.saveOrUpdate(topic);

        mv.addObject(MESSAGE_SUCCESS, "Téma " + topic.getName() + " bylo úspěšně přidáno.");
        mv.setViewName(REDIRECT_URL + PORADNA_URL + TEMA_URL + SLASH + topic.getId());
        return mv;
    }

    @GetMapping(PRISPEVEK_NOVY_URL + TOPIC_ID_PATH_VAR)
    public String vytvoritPrispevekGet(@PathVariable(TOPIC_ID) Long topicId, Model model) {
        Post post = new Post();
        model.addAttribute(TOPIC_ID, topicId);
        model.addAttribute(POST, post);

        return PORADNA_URL + PRISPEVEK_NOVY_URL;
    }

    @PostMapping(PRISPEVEK_NOVY_URL + TOPIC_ID_PATH_VAR)
    public ModelAndView vytvoritPrispevekPost(@Valid @ModelAttribute(POST) Post post, BindingResult result, @PathVariable(TOPIC_ID) Long topicId, Principal principal) {
        ModelAndView mv = new ModelAndView();
        Optional<Topic> topicOptional = topicService.findById(topicId);

        if (!topicOptional.isPresent()) {
            mv.setViewName(REDIRECT_URL + PORADNA_URL);
            mv.addObject(MESSAGE_ERROR, "Téma nenalezeno");
            return mv;
        } else {
            post.setTopic(topicOptional.get());
        }

        if (result.hasErrors()) {
            mv.addObject(MESSAGE_ERROR, SPATNE_VYPLNENA_POLE_ERROR_MSG);
            mv.setViewName(REDIRECT_URL + PORADNA_URL + TEMA_URL + SLASH + topicId);
            return mv;
        }

        if (principal == null) {
            post.setUser(userService.findByUsername("anonym"));
        } else {
            post.setUser(userService.findByUsername(principal.getName()));
        }

        postService.saveOrUpdate(post);

        mv.addObject(MESSAGE_SUCCESS, "Příspěvek přidán");

        mv.setViewName(REDIRECT_URL + PORADNA_URL + TEMA_URL + SLASH + topicId + "?page=" + postService.getLastPageOfTopic(topicOptional.get().getId()));
        return mv;
    }

    @GetMapping(SLEDOVAT_URL + TOPIC_ID_PATH_VAR)
    public String sledovatTema(@PathVariable(TOPIC_ID) Long topicId, Principal principal) {
        Optional<Topic> optionalTopic = topicService.findById(topicId);
        if (optionalTopic.isPresent()) {
            Topic topic = optionalTopic.get();
            User user = userService.findByUsername(principal.getName());
            topic.addTopicWatchingUser(user);
            topicService.saveOrUpdate(topic);
        }
        return REDIRECT_URL + PORADNA_URL + TEMA_URL + SLASH + topicId;
    }
}
