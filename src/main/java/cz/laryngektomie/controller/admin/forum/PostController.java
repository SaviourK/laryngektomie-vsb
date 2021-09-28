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

@Controller
@RequestMapping("/admin/poradna/prispevky")
public class PostController {

    private PostService postService;
    private TopicService topicService;
    private UserService userService;

    public PostController(PostService postService, TopicService topicService, UserService userService) {
        this.postService = postService;
        this.topicService = topicService;
        this.userService = userService;
    }

    @GetMapping()
    public ModelAndView prispevky(@RequestParam(value = "page", defaultValue = "1") int page,  @RequestParam Optional<String> query) {
        ModelAndView mv = new ModelAndView("admin/poradna/prispevky");

        int pageNumber = page < 0 ? 1 : page;

        String queryString = query.orElse("");

        Page<Post> posts = postService.findAllSearch(pageNumber, ForumHelper.itemsOnPage, "createDateTime", false, queryString);

        mv.addObject("pageNumbers", ForumHelper.getListOfPageNumbers(posts.getTotalPages(), pageNumber));
        mv.addObject("currentPage", pageNumber);
        mv.addObject("posts", posts);
        return mv;
    }



    @GetMapping("/vytvorit")
    public String vytvoritGet(Model model) {
        model.addAttribute("post", new Post());
        model.addAttribute("topics", topicService.findAll());
        return "admin/poradna/prispevky/vytvorit";

    }


    @PostMapping("/vytvorit")
    public ModelAndView vytvoritPost(@ModelAttribute("post") @Valid Post post, BindingResult result, Principal principal) {
        ModelAndView mv = new ModelAndView();

        if (result.hasErrors()) {
            mv.addObject("post", post);
            mv.addObject("messageError", "Špatně vypněné pole.");
            mv.setViewName("admin/poradna/prispevky/vytvorit");
            return mv;
        }



        if (post.getTopic() == null) {
            mv.addObject("topics", topicService.findAll());
            mv.addObject("post", post);
            mv.setViewName("admin/poradna/prispevky/vytvorit");
            mv.addObject("messageError", "Prosím vyberte téma pro přispěvek");
            return mv;
        }


        post.setUser(userService.findByUsername(principal.getName()));

        postService.saveOrUpdate(post);
        mv.addObject("messageSuccess", "Příspěvek byl přidán");
        mv.setViewName("redirect:/admin/poradna/prispevky");
        return mv;
    }

    @GetMapping("/upravit/{id}")
    public ModelAndView upravitGet(@PathVariable long id) {
        ModelAndView mv = new ModelAndView("admin/poradna/prispevky/upravit");
        Optional<Post> postOptional = postService.findById(id);


        if (!postOptional.isPresent()) {
            mv.addObject("messageError", "Požadovaný příspěvek neexistuje.");
            mv.setViewName("redirect:/admin/poradna/prispevky");
            return mv;
        }

        mv.addObject("post", postOptional.get());
        mv.addObject("topics", topicService.findAll());
        return mv;
    }

    @PostMapping("/upravit")
    public ModelAndView upravitPost(@ModelAttribute("post") @Valid Post post, BindingResult result) {
        ModelAndView mv = new ModelAndView();

        if (result.hasErrors()) {
            mv.setViewName("admin/poradna/prispevky/upravit");
            mv.addObject("post", post);
            mv.addObject("topics", topicService.findAll());
            mv.addObject("messageError", "Špatně vyplněná pole.");
            return mv;
        }

        post.setUser(userService.findByUsername(post.getUser().getUsername()));
        post.setTopic(topicService.findByName(post.getTopic().getName()).get());
        postService.saveOrUpdate(post);
        mv.addObject("messageSuccess", "Příspěvek byl upraven.");
        mv.setViewName("redirect:/admin/poradna/prispevky");
        return mv;
    }



    @GetMapping("/smazat/{id}")
    public ModelAndView smazat(@PathVariable long id) {
        ModelAndView mv = new ModelAndView();
        Optional<Post> postOptional = postService.findById(id);

        if (!postOptional.isPresent()) {
            mv.addObject("messageError", "Příspěvek s id: " + id + " neexistuje.");
            mv.setViewName("redirect:/admin/poradna/prispevky");
            return mv;
        }

        postService.delete(postOptional.get());
        mv.addObject("messageSuccess", "Příspěvek byl smazán.");
        mv.setViewName("redirect:/admin/poradna/prispevky");
        return mv;

    }

}

