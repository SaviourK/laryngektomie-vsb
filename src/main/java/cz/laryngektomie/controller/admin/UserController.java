package cz.laryngektomie.controller.admin;

import cz.laryngektomie.helper.Const;
import cz.laryngektomie.helper.ForumHelper;
import cz.laryngektomie.model.article.Image;
import cz.laryngektomie.model.security.User;
import cz.laryngektomie.service.article.ImageService;
import cz.laryngektomie.service.security.UserService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/poradna/uzivatele")
public class UserController {

    private final UserService userService;
    private final ImageService imageService;

    private static final List<String> ROLES = Arrays.asList(Const.ROLE_ADMIN, Const.ROLE_USER, Const.ROLE_SPECIALISTS);

    public UserController(UserService userService, ImageService imageService) {
        this.userService = userService;
        this.imageService = imageService;
    }

    @GetMapping()
    public ModelAndView uzivatele(@RequestParam(value = "page", defaultValue = "1") int page, @RequestParam Optional<String> query) {
        ModelAndView mv = new ModelAndView("admin/poradna/uzivatele");

        int pageNumber = page < 0 ? 1 : page;

        String queryString = query.orElse("");

        Page<User> users = userService.findAllSearch(pageNumber, ForumHelper.itemsOnPage, "createDateTime", false, queryString);

        mv.addObject("pageNumbers", ForumHelper.getListOfPageNumbers(users.getTotalPages(), pageNumber));
        mv.addObject("currentPage", pageNumber);
        mv.addObject("users", users);

        return mv;
    }


    @GetMapping("/{id}")
    public ModelAndView detail(@PathVariable long id) {
        ModelAndView mv = new ModelAndView("admin/poradna/uzivatele/detail");
        Optional<User> userOptional = userService.findById(id);
        if (!userOptional.isPresent()) {
            mv.addObject("messageError", "Požadovaný uživatel neexistuje.");
            mv.setViewName("redirect:/admin/poradna/uzivatele");
            return mv;
        }

        mv.addObject("user", userOptional.get());
        return mv;
    }

    @GetMapping("/vytvorit")
    public String vytvoritGet(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", ROLES);
        return "admin/poradna/uzivatele/vytvorit";
    }

    @PostMapping("/vytvorit")
    public ModelAndView vytvoritPost(@ModelAttribute("user") @Valid User user, @RequestParam("file") MultipartFile file, BindingResult result) throws IOException {
        ModelAndView mv = new ModelAndView();

        if (result.hasErrors()) {
            mv.setViewName("admin/poradna/uzivatele/vytvorit");
            mv.addObject("user", user);
            mv.addObject("roles", ROLES);
            mv.addObject("messageError", "Špatně vyplněná pole.");
            return mv;
        }

        if (userService.findByUsername(user.getUsername()) != null) {
            mv.setViewName("/admin/poradna/uzivatele/vytvorit");
            mv.addObject("user", user);
            mv.addObject("roles", ROLES);
            mv.addObject("messageError", "Uživatelské jméno obsazeno");
            return mv;
        }

        if (userService.findByEmail(user.getEmail()).isPresent()) {
            mv.setViewName("/admin/poradna/uzivatele/vytvorit");
            mv.addObject("user", user);
            mv.addObject("roles", ROLES);
            mv.addObject("messageError", "Email obsazen");
            return mv;
        }

        if (user.getRole().isEmpty()) {
            mv.addObject("user", user);
            mv.addObject("roles", ROLES);
            mv.setViewName("admin/poradna/uzivatele/vytvorit");
            mv.addObject("messageError", "Prosím vyberte roli pro uživatele");
            return mv;
        }

        //TODO change to resize method
        if (file != null) {
            Image image = imageService.saveImage(file);
            if (image != null) {
                user.setImage(image);
            }
        }

        user.setPassword(userService.encode(user.getPassword()));
        userService.saveOrUpdate(user);
        mv.addObject("messageSuccess", "Uživatel " + user.getUsername() + " byl úspěšně přidán.");
        mv.setViewName("redirect:/admin/poradna/uzivatele");
        return mv;
    }

    @GetMapping("/upravit/{id}")
    public ModelAndView upravitGet(@PathVariable long id) {
        ModelAndView mv = new ModelAndView("admin/poradna/uzivatele/upravit");
        Optional<User> userOptional = userService.findById(id);
        if (!userOptional.isPresent()) {
            mv.addObject("messageError", "Požadovaný uživatel neexistuje.");
            mv.setViewName("redirect:/admin/poradna/uzivatele");
            return mv;
        }

        mv.addObject("user", userOptional.get());
        mv.addObject("userRole", userOptional.get().getRole());
        mv.addObject("roles", ROLES);
        return mv;
    }

    @PostMapping("/upravit")
    public ModelAndView upravitPost(@ModelAttribute("user") @Valid User user, @RequestParam("file") MultipartFile file, BindingResult result) throws IOException {
        ModelAndView mv = new ModelAndView();

        if (result.hasErrors()) {
            mv.setViewName("admin/poradna/uzivatele/upravit");
            mv.addObject("user", user);
            mv.addObject("userRole", user.getRole());
            mv.addObject("roles", ROLES);
            mv.addObject("messageError", "Špatně vyplněná pole.");
            return mv;
        }

        if (user.getRole().isEmpty()) {
            mv.setViewName("admin/poradna/uzivatele/upravit");
            mv.addObject("user", user);
            mv.addObject("roles", ROLES);
            mv.addObject("messageError", "Vyberte roli");
            return mv;
        }

        //TODO change to resize method
        Image oldImage = user.getImage();
        if (file != null) {
            Image image = imageService.saveImage(file);
            if (image != null) {
                user.setImage(image);

            }
        }

        //TODO jinak ověřit ostatní uzivatele a jejich emaily a ne toho ktereho upravuju
        /*if (userRepository.findByUsername(user.getUsername()) != null) {
            mv.setViewName("/admin/poradna/uzivatele/upravit");
            mv.addObject("user", user);
            mv.addObject("userRoleId", user.getFirtRoleId());
            mv.addObject("roles", roleRepository.findAll());
            mv.addObject("messageError", "Uživatelské jméno obsazeno");
            return mv;

        }

        if (userRepository.findByEmail(user.getEmail()) != null) {
            mv.setViewName("/admin/poradna/uzivatele/upravit");
            mv.addObject("user", user);
            mv.addObject("userRoleId", user.getFirtRoleId());
            mv.addObject("roles", roleRepository.findAll());
            mv.addObject("messageError", "Email obsazen");
            return mv;

        }*/

        userService.saveOrUpdate(user);
        if (oldImage != null) {
            imageService.delete(oldImage);
        }
        mv.addObject("messageSuccess", "Uživatel:" + user.getUsername() + " byl upraven.");
        mv.setViewName("redirect:/admin/poradna/uzivatele");
        return mv;
    }

    @GetMapping("/smazat/{id}")
    public ModelAndView smazat(@PathVariable long id) {
        ModelAndView mv = new ModelAndView();
        Optional<User> userOptional = userService.findById(id);

        if (!userOptional.isPresent()) {
            mv.addObject("messageError", "Uživatel s id: " + id + " neexistuje.");
            mv.setViewName("redirect:/admin/poradna/uzivatele");
            return mv;
        }

        userService.delete(userOptional.get());
        mv.addObject("messageSuccess", "Uživatel " + userOptional.get().getUsername() + " byl vymazán.");
        mv.setViewName("redirect:/admin/poradna/uzivatele");
        return mv;
    }
}
