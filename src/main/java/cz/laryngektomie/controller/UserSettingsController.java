package cz.laryngektomie.controller;

import cz.laryngektomie.model.article.Image;
import cz.laryngektomie.model.security.User;
import cz.laryngektomie.service.article.ImageService;
import cz.laryngektomie.service.security.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.security.Principal;

import static cz.laryngektomie.helper.Const.*;
import static cz.laryngektomie.helper.UrlConst.*;

@Controller
@RequestMapping(NASTAVENI_URL)
public class UserSettingsController {

    private final UserService userService;
    private final ImageService imageService;

    public UserSettingsController(UserService userService, ImageService imageService) {
        this.userService = userService;
        this.imageService = imageService;
    }

    @GetMapping(ZMENIT_HESLO_URL)
    public ModelAndView zmenitHesloGet(Principal principal) {

        ModelAndView mv = new ModelAndView();

        User user = userService.findByUsername(principal.getName());

        if (user == null) { // Token found in DB
            mv.addObject(MESSAGE_ERROR, "Nejste přihlášen.");
            mv.setViewName(REGISTRACE_URL + PRIHLASENI_URL);
            return mv;
        }

        mv.setViewName(NASTAVENI_URL + ZMENIT_HESLO_URL);
        return mv;
    }

    // Process reset password form
    @PostMapping(ZMENIT_HESLO_URL)
    public ModelAndView obnovitHesloPost(@ModelAttribute("oldPassword") String oldPassword, @ModelAttribute("password") String password, @ModelAttribute("matchingPassword") String matchingPassword, Principal principal) {

        ModelAndView mv = new ModelAndView();

        User user = userService.findByUsername(principal.getName());

        if (userService.matchPassword(oldPassword, user.getPassword())) {
            if (password.equals(matchingPassword)) {
                user.setPassword(userService.encode(password));
            } else {
                mv.addObject(MESSAGE_ERROR, "Vaše hesla se neshodují");
                mv.setViewName(REDIRECT_URL + NASTAVENI_URL + ZMENIT_HESLO_URL);
                return mv;
            }

            userService.saveOrUpdate(user);
            mv.setViewName(REDIRECT_URL + PORADNA_URL);
            mv.addObject(MESSAGE_SUCCESS, "Vaše heslo bylo změněno");
            return mv;
        } else {
            mv.addObject(MESSAGE_ERROR, "Špatné staré heslo");
            mv.setViewName(REDIRECT_URL + NASTAVENI_URL + ZMENIT_HESLO_URL);
            return mv;
        }
    }

    @GetMapping(ZMENIT_FOTKU_URL)
    public ModelAndView zmenitFotkuGet(Principal principal) {
        ModelAndView mv = new ModelAndView(NASTAVENI_URL + ZMENIT_FOTKU_URL);

        User user = userService.findByUsername(principal.getName());
        if (user == null) {
            mv.addObject(MESSAGE_ERROR, "Musíte se přihlásit");
            mv.setViewName(REDIRECT_URL + HOME_URL);
            return mv;
        }

        mv.addObject("userImage", user.getImage());
        return mv;
    }

    @PostMapping(ZMENIT_FOTKU_URL)
    public ModelAndView zmenitFotkuPost(@RequestParam(FILE) MultipartFile file, Principal principal) throws IOException {
        ModelAndView mv = new ModelAndView();

        User user = userService.findByUsername(principal.getName());
        if (user == null) {
            mv.addObject(MESSAGE_ERROR, "Musíte se přihlásit");
            mv.setViewName(REDIRECT_URL + HOME_URL);
            return mv;
        }

        Image oldImage = user.getImage();
        if (file != null) {
            Image image = imageService.saveImage(file);
            if (image != null) {
                user.setImage(image);
            } else {
                mv.addObject(MESSAGE_ERROR, "Nevybrali jste obrázek");
                mv.setViewName(REDIRECT_URL + NASTAVENI_URL + ZMENIT_FOTKU_URL);
                return mv;
            }
        }

        userService.saveOrUpdate(user);
        if (oldImage != null) {
            imageService.delete(oldImage);
        }
        mv.addObject(MESSAGE_SUCCESS, "Fotka byla změněna.");
        mv.setViewName(REDIRECT_URL + PORADNA_URL);
        return mv;
    }
}
