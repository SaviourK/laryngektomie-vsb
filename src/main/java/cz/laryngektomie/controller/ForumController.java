package cz.laryngektomie.controller;

import cz.laryngektomie.helper.ForumHelper;
import cz.laryngektomie.model.dto.TopicOrPost;
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

@Controller
@RequestMapping("/poradna")
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
        //Page<Post> posts = postService.findAll(pageNumber, ForumHelper.itemsOnPage, "createDateTime", false);
        //List<Topic> topics = topicService.findLatest();

        mv.addObject("categories", categoryService.findAll());
        //mv.addObject("pageNumbers", ForumHelper.getListOfPageNumbers(posts.getTotalPages(), pageNumber));
        mv.addObject("currentPage", pageNumber);
        mv.addObject("topicOrPostList", topicOrPostList);
        mv.addObject("postCount", postService.findAll().size());
        mv.addObject("topicCount", topicService.findAll().size());
        //mv.addObject("posts", posts);
        return mv;
    }

    @RequestMapping({"/{categoryUrl}"})
    public ModelAndView temata(@PathVariable("categoryUrl") String categoryUrl, @RequestParam("page") Optional<Integer> page) {
        int pageNumber = page.orElse(1);

        ModelAndView mv = new ModelAndView();

        Optional<Category> categoryOptional = categoryService.findByUrl(categoryUrl);

        if (!categoryOptional.isPresent()) {
            mv.addObject("messageError", "Kategorie neexistuje");
            mv.setViewName("redirect:/poradna");
        }


        Pageable pagged = PageRequest.of(pageNumber - 1, ForumHelper.itemsOnPage, Sort.by("createDateTime").descending());
        Page<Topic> topics = topicService.findByCategoryId(categoryOptional.get().getId(), pagged);

        int totalPages = topics.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
            mv.addObject("pageNumbers", pageNumbers);
        }


        mv.addObject("currentPage", pageNumber);
        mv.addObject("category", categoryOptional.get());
        mv.addObject("categories", categoryService.findAll());

        mv.addObject("topics", topics);
        mv.setViewName("poradna/kategorie");
        return mv;
    }

    @GetMapping("/tema/{id}")
    @ResponseBody
    public ModelAndView detailTema(@PathVariable("id") Long id, @RequestParam(value = "page", defaultValue = "1") int page) {
        ModelAndView mv = new ModelAndView();

        int pageNumber = page < 0 ? 1 : page;


        Pageable pagged = PageRequest.of(pageNumber - 1, 10, Sort.by("createDateTime").ascending());
        Optional<Topic> optionalTopic = topicService.findById(id);

        if (!optionalTopic.isPresent()) {
            mv.addObject("messageError", "Požadované téma neexstuje.");
            mv.setViewName("redirect:/poradna");
            return mv;
        }
        Page<Post> posts = postService.findByTopicId(optionalTopic.get().getId(), pagged);


        mv.addObject("pageNumbers", ForumHelper.getListOfPageNumbers(posts.getTotalPages(), pageNumber));
        mv.addObject("currentPage", pageNumber);

        mv.addObject("topic", optionalTopic.get());
        mv.addObject("posts", posts);
        mv.addObject("post", new Post());
        mv.setViewName("poradna/tema");
        return mv;
    }

    @GetMapping("/tema-nove/{categoryId}")
    public String vytvoritTemaGet(@PathVariable("categoryId") Long categoryId, Model model) {
        Topic topic = new Topic();
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("topic", topic);
        return "poradna/tema-nove";
    }

    @PostMapping("/tema-nove/{categoryId}")
    public ModelAndView vytvoritTemaPost(@Valid @ModelAttribute("topic") Topic topic, BindingResult result, @PathVariable("categoryId") Long categoryId, Principal principal) {
        ModelAndView mv = new ModelAndView();
        Optional<Category> categoryOptional = categoryService.findById(categoryId);
        if (!categoryOptional.isPresent()) {
            mv.setViewName("redirect:poradna");
            return mv;
        } else {
            topic.setCategory(categoryOptional.get());
        }
        if (result.hasErrors()) {
            mv.setViewName("poradna/tema-nove/" + categoryId);
            mv.addObject("topic", topic);
            mv.addObject("messageError", "Špatně vyplněná pole.");
            return mv;
        }

        if (principal == null) {
            mv.addObject("messageError", "Pro vytváření témat se musíte přihlásit");
            mv.setViewName("redirect:/poradna");
            return mv;
        } else {
            topic.setUser(userService.findByUsername(principal.getName()));
        }

        topicService.saveOrUpdate(topic);

        mv.addObject("messageSuccess", "Téma " + topic.getName() + " bylo úspěšně přidáno.");
        mv.setViewName("redirect:/poradna/tema/" + topic.getId());
        return mv;
    }

    @GetMapping("/prispevek-novy/{topicId}")
    public String vytvoritPrispevekGet(@PathVariable("topicId") Long topicId, Model model) {
        Post post = new Post();
        model.addAttribute("topicId", topicId);
        model.addAttribute("post", post);

        return "poradna/prispevek-novy";
    }

    @PostMapping("/prispevek-novy/{topicId}")
    public ModelAndView vytvoritPrispevekPost(@Valid @ModelAttribute("post") Post post, BindingResult result, @PathVariable("topicId") Long topicId, Principal principal) {
        ModelAndView mv = new ModelAndView();
        Optional<Topic> topicOptional = topicService.findById(topicId);

        if (!topicOptional.isPresent()) {
            mv.setViewName("redirect:/poradna");
            mv.addObject("messageError", "Téma nenalezeno");
            return mv;
        } else {
            post.setTopic(topicOptional.get());
        }

        if (result.hasErrors()) {
            mv.addObject("messageError", "Špatně vyplněná pole.");
            mv.setViewName("redirect:/poradna/tema/" + topicId);
            return mv;
        }

        if (principal == null) {
            post.setUser(userService.findByUsername("anonym"));
        } else {
            post.setUser(userService.findByUsername(principal.getName()));
        }

        postService.saveOrUpdate(post);

        mv.addObject("messageSuccess", "Příspěvek přidán");

        mv.setViewName("redirect:/poradna/tema/" + topicId + "?page=" + postService.getLastPageOfTopic(topicOptional.get().getId()));
        return mv;
    }

    @GetMapping("/sledovat/{topicId}")
    public String sledovatTema(@PathVariable("topicId") Long topicId, Principal principal) {
        Optional<Topic> optionalTopic = topicService.findById(topicId);
        if (optionalTopic.isPresent()) {
            Topic topic = optionalTopic.get();
            User user = userService.findByUsername(principal.getName());
            topic.addTopicWatchingUser(user);
            topicService.saveOrUpdate(topic);
        }
        return "redirect:/poradna/tema/" + topicId;
    }
}
