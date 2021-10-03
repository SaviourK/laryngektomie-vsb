package cz.laryngektomie.controller.admin;

import cz.laryngektomie.helper.ForumHelper;
import cz.laryngektomie.model.news.Image;
import cz.laryngektomie.model.security.User;
import cz.laryngektomie.service.news.ImageService;
import cz.laryngektomie.service.security.RoleService;
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
import java.util.Optional;

@Controller
@RequestMapping("/admin/poradna/uzivatele")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;
    private final ImageService imageService;

    public UserController(UserService userService, RoleService roleService, ImageService imageService) {
        this.userService = userService;
        this.roleService = roleService;
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
        model.addAttribute("roles", roleService.findAll());
        return "admin/poradna/uzivatele/vytvorit";
    }

    @PostMapping("/vytvorit")
    public ModelAndView vytvoritPost(@ModelAttribute("user") @Valid User user, @RequestParam("file") MultipartFile file, BindingResult result) throws IOException {
        ModelAndView mv = new ModelAndView();

        if (result.hasErrors()) {
            mv.setViewName("admin/poradna/uzivatele/vytvorit");
            mv.addObject("user", user);
            mv.addObject("roles", roleService.findAll());
            mv.addObject("messageError", "Špatně vyplněná pole.");
            return mv;
        }

        if (userService.findByUsername(user.getUsername()) != null) {
            mv.setViewName("/admin/poradna/uzivatele/vytvorit");
            mv.addObject("user", user);
            mv.addObject("roles", roleService.findAll());
            mv.addObject("messageError", "Uživatelské jméno obsazeno");
            return mv;
        }

        if (userService.findByEmail(user.getEmail()).isPresent()) {
            mv.setViewName("/admin/poradna/uzivatele/vytvorit");
            mv.addObject("user", user);
            mv.addObject("roles", roleService.findAll());
            mv.addObject("messageError", "Email obsazen");
            return mv;
        }

        if (user.getRoles().size() == 0) {
            mv.addObject("user", user);
            mv.addObject("roles", roleService.findAll());
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
        mv.addObject("userRoleId", userOptional.get().getFirstRoleId());
        mv.addObject("roles", roleService.findAll());
        return mv;
    }

    @PostMapping("/upravit")
    public ModelAndView upravitPost(@ModelAttribute("user") @Valid User user, @RequestParam("file") MultipartFile file, BindingResult result) throws IOException {
        ModelAndView mv = new ModelAndView();

        if (result.hasErrors()) {
            mv.setViewName("admin/poradna/uzivatele/upravit");
            mv.addObject("user", user);
            mv.addObject("userRoleId", user.getFirstRoleId());
            mv.addObject("roles", roleService.findAll());
            mv.addObject("messageError", "Špatně vyplněná pole.");
            return mv;
        }

        if (user.getRoles().size() == 0) {
            mv.setViewName("admin/poradna/uzivatele/upravit");
            mv.addObject("user", user);
            mv.addObject("roles", roleService.findAll());
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
