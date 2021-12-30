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

import static cz.laryngektomie.helper.Const.*;
import static cz.laryngektomie.helper.UrlConst.*;

@Controller
@RequestMapping(ADMIN_PORADNA_URL + UZIVATELE_URL)
public class UserController {

    private static final List<String> ROLES = Arrays.asList(Const.ROLE_ADMIN, Const.ROLE_USER, Const.ROLE_SPECIALISTS);
    private final UserService userService;
    private final ImageService imageService;

    public UserController(UserService userService, ImageService imageService) {
        this.userService = userService;
        this.imageService = imageService;
    }

    @GetMapping()
    public ModelAndView uzivatele(@RequestParam(value = PAGE, defaultValue = DEFAULT_VALUE_1) int page, @RequestParam Optional<String> query) {
        ModelAndView mv = new ModelAndView(ADMIN_PORADNA_URL + UZIVATELE_URL);

        int pageNumber = ForumHelper.resolvePageNumber(page);

        String queryString = query.orElse(EMPTY_STRING);

        Page<User> users = userService.findAllSearch(pageNumber, ForumHelper.ITEMS_ON_PAGE, CREATE_DATE_TIME, false, queryString);

        mv.addObject(PAGE_NUMBERS, ForumHelper.getListOfPageNumbers(users.getTotalPages(), pageNumber));
        mv.addObject(CURRENT_PAGE, pageNumber);
        mv.addObject(USERS, users);

        return mv;
    }


    @GetMapping(ID_PATH_VAR)
    public ModelAndView detail(@PathVariable long id) {
        ModelAndView mv = new ModelAndView(ADMIN_PORADNA_URL + UZIVATELE_URL + DETAIL_URL);
        Optional<User> userOptional = userService.findById(id);
        if (!userOptional.isPresent()) {
            mv.addObject(MESSAGE_ERROR, "Požadovaný uživatel neexistuje.");
            mv.setViewName(REDIRECT_URL + ADMIN_PORADNA_URL + UZIVATELE_URL);
            return mv;
        }

        mv.addObject(USER, userOptional.get());
        //TOOD pridat dotazy
        mv.addObject(ADMIN_CATEGORY, "Laryngektomie, Pravidla");
        mv.addObject(WATCHING_TOPICS, 5);
        return mv;
    }

    @GetMapping(VYTVORIT_URL)
    public String vytvoritGet(Model model) {
        model.addAttribute(USER, new User());
        model.addAttribute(Const.ROLES, ROLES);
        return ADMIN_PORADNA_URL + UZIVATELE_URL + VYTVORIT_URL;
    }

    @PostMapping(VYTVORIT_URL)
    public ModelAndView vytvoritPost(@ModelAttribute(USER) @Valid User user, @RequestParam(FILE) MultipartFile file, BindingResult result) throws IOException {
        ModelAndView mv = new ModelAndView();

        if (result.hasErrors()) {
            mv.setViewName(ADMIN_PORADNA_URL + UZIVATELE_URL + VYTVORIT_URL);
            mv.addObject(USER, user);
            mv.addObject(Const.ROLES, ROLES);
            mv.addObject(MESSAGE_ERROR, SPATNE_VYPLNENA_POLE_ERROR_MSG);
            return mv;
        }

        if (userService.findByUsername(user.getUsername()) != null) {
            mv.setViewName(ADMIN_PORADNA_URL + UZIVATELE_URL + VYTVORIT_URL);
            mv.addObject(USER, user);
            mv.addObject(Const.ROLES, ROLES);
            mv.addObject(MESSAGE_ERROR, "Uživatelské jméno obsazeno");
            return mv;
        }

        if (userService.findByEmail(user.getEmail()).isPresent()) {
            mv.setViewName(ADMIN_PORADNA_URL + UZIVATELE_URL + VYTVORIT_URL);
            mv.addObject(USER, user);
            mv.addObject(Const.ROLES, ROLES);
            mv.addObject(MESSAGE_ERROR, "Email obsazen");
            return mv;
        }

        if (user.getRole() == null) {
            mv.addObject(USER, user);
            mv.addObject(Const.ROLES, ROLES);
            mv.setViewName(ADMIN_PORADNA_URL + UZIVATELE_URL + VYTVORIT_URL);
            mv.addObject(MESSAGE_ERROR, "Prosím vyberte roli pro uživatele");
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
        mv.addObject(MESSAGE_SUCCESS, "Uživatel " + user.getUsername() + " byl úspěšně přidán.");
        mv.setViewName(REDIRECT_URL + ADMIN_PORADNA_URL + UZIVATELE_URL);
        return mv;
    }

    @GetMapping(UPRAVIT_URL + ID_PATH_VAR)
    public ModelAndView upravitGet(@PathVariable long id) {
        ModelAndView mv = new ModelAndView(ADMIN_PORADNA_URL + UZIVATELE_URL + UPRAVIT_URL);
        Optional<User> userOptional = userService.findById(id);
        if (!userOptional.isPresent()) {
            mv.addObject(MESSAGE_ERROR, "Požadovaný uživatel neexistuje.");
            mv.setViewName(REDIRECT_URL + ADMIN_PORADNA_URL + UZIVATELE_URL);
            return mv;
        }

        mv.addObject(USER, userOptional.get());
        mv.addObject(USER_ROLE, userOptional.get().getRole());
        mv.addObject(Const.ROLES, ROLES);
        return mv;
    }

    @PostMapping(UPRAVIT_URL)
    public ModelAndView upravitPost(@ModelAttribute(USER) @Valid User user, @RequestParam(FILE) MultipartFile file, BindingResult result) throws IOException {
        ModelAndView mv = new ModelAndView();

        if (result.hasErrors()) {
            mv.setViewName(ADMIN_PORADNA_URL + UZIVATELE_URL + UPRAVIT_URL);
            mv.addObject(USER, user);
            mv.addObject(USER_ROLE, user.getRole());
            mv.addObject(Const.ROLES, ROLES);
            mv.addObject(MESSAGE_ERROR, SPATNE_VYPLNENA_POLE_ERROR_MSG);
            return mv;
        }

        if (user.getRole() == null) {
            mv.setViewName(ADMIN_PORADNA_URL + UZIVATELE_URL + UPRAVIT_URL);
            mv.addObject(USER, user);
            mv.addObject(Const.ROLES, ROLES);
            mv.addObject(MESSAGE_ERROR, "Vyberte roli");
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
            mv.addObject(MESSAGE_ERROR, "Uživatelské jméno obsazeno");
            return mv;

        }

        if (userRepository.findByEmail(user.getEmail()) != null) {
            mv.setViewName("/admin/poradna/uzivatele/upravit");
            mv.addObject("user", user);
            mv.addObject("userRoleId", user.getFirtRoleId());
            mv.addObject("roles", roleRepository.findAll());
            mv.addObject(MESSAGE_ERROR, "Email obsazen");
            return mv;

        }*/

        userService.saveOrUpdate(user);
        if (oldImage != null) {
            imageService.delete(oldImage);
        }
        mv.addObject(MESSAGE_SUCCESS, "Uživatel:" + user.getUsername() + " byl upraven.");
        mv.setViewName(REDIRECT_URL + ADMIN_PORADNA_URL + UZIVATELE_URL);
        return mv;
    }

    @GetMapping(SMAZAT_URL + ID_PATH_VAR)
    public ModelAndView smazat(@PathVariable long id) {
        ModelAndView mv = new ModelAndView();
        Optional<User> userOptional = userService.findById(id);

        if (!userOptional.isPresent()) {
            mv.addObject(MESSAGE_ERROR, "Uživatel s id: " + id + " neexistuje.");
            mv.setViewName(REDIRECT_URL + ADMIN_PORADNA_URL + UZIVATELE_URL);
            return mv;
        }

        userService.delete(userOptional.get());
        mv.addObject(MESSAGE_SUCCESS, "Uživatel " + userOptional.get().getUsername() + " byl vymazán.");
        mv.setViewName(REDIRECT_URL + ADMIN_PORADNA_URL + UZIVATELE_URL);
        return mv;
    }
}
