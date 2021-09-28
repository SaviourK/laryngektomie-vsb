package cz.laryngektomie.controller;

import cz.laryngektomie.model.news.Image;
import cz.laryngektomie.model.security.User;
import cz.laryngektomie.service.news.ImageService;
import cz.laryngektomie.service.security.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.security.Principal;

@Controller
@RequestMapping("nastaveni")
public class UserSettingsController {

    private UserService userService;
    private ImageService imageService;

    public UserSettingsController(UserService userService, ImageService imageService) {
        this.userService = userService;
        this.imageService = imageService;
    }

    @GetMapping("/zmenit-heslo")
    public ModelAndView zmenitHesloGet(Principal principal) {

        ModelAndView mv = new ModelAndView();

        User user = userService.findByUsername(principal.getName());

        if (user == null) { // Token found in DB
            mv.addObject("messageError", "Nejste přihlášen.");
            mv.setViewName("redirect:prihlaseni");
            return mv;
        }

        mv.setViewName("nastaveni/zmenit-heslo");
        return mv;
    }

    // Process reset password form
    @PostMapping("/zmenit-heslo")
    public ModelAndView obnovitHesloPost(@ModelAttribute("oldPassword") String oldPassword, @ModelAttribute("password") String password, @ModelAttribute("matchingPassword") String matchingPassword, Principal principal) {

        ModelAndView mv = new ModelAndView();

        User user = userService.findByUsername(principal.getName());

        if(userService.matchPassword(oldPassword, user.getPassword())) {
            if(password.equals(matchingPassword)){
                user.setPassword(userService.encode(password));
            } else {
                mv.addObject("messageError", "Vaše hesla se neshodují");
                mv.setViewName("redirect:/nastaveni/zmenit-heslo");
                return mv;
            }

            userService.saveOrUpdate(user);
            mv.setViewName("redirect:/poradna");
            mv.addObject("messageSuccess", "Vaše heslo bylo změněno");
            return mv;
        }else {
            mv.addObject("messageError", "Špatné staré heslo");
            mv.setViewName("redirect:/nastaveni/zmenit-heslo");
            return mv;
        }

    }


    @GetMapping("/zmenit-fotku")
    public ModelAndView zmenitFotkuGet(Principal principal) {
        ModelAndView mv = new ModelAndView("nastaveni/zmenit-fotku");

        User user = userService.findByUsername(principal.getName());
        if (user == null) {
            mv.addObject("messageError", "Musíte se přihlásit");
            mv.setViewName("redirect:/");
            return mv;
        }

        mv.addObject("userImage", user.getImage());
        return mv;
    }

    @PostMapping("/zmenit-fotku")
    public ModelAndView zmenitFotkuPost(@RequestParam("file") MultipartFile file, Principal principal) throws IOException {
        ModelAndView mv = new ModelAndView();

        User user = userService.findByUsername(principal.getName());
        if (user == null) {
            mv.addObject("messageError", "Musíte se přihlásit");
            mv.setViewName("redirect:/");
            return mv;
        }

        Image oldImage = user.getImage();
        if (file != null) {
            Image image = imageService.saveImage(file);
            if(image != null) {
                user.setImage(image);

            }else {
                mv.addObject("messageError", "Nevybrali jste obrázek");
                mv.setViewName("redirect:/nastaveni/zmenit-fotku");
                return mv;
            }
        }


        userService.saveOrUpdate(user);
        if(oldImage != null) {
            imageService.delete(oldImage);
        }
        mv.addObject("messageSuccess", "Fotka byla změněna.");
        mv.setViewName("redirect:/poradna");
        return mv;
    }
}
