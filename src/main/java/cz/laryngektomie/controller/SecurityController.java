package cz.laryngektomie.controller;

import cz.laryngektomie.helper.Const;
import cz.laryngektomie.model.article.Image;
import cz.laryngektomie.model.security.User;
import cz.laryngektomie.model.security.UserRole;
import cz.laryngektomie.security.UserPrincipal;
import cz.laryngektomie.service.article.ImageService;
import cz.laryngektomie.service.security.UserService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static cz.laryngektomie.helper.Const.*;
import static cz.laryngektomie.helper.UrlConst.*;

@Controller
public class SecurityController {

    private static final String PRIHLASENI_ACTION = "prihlaseni";
    private static final String PRIHLASENI_TITLE = "Přihlášení";
    private final UserService userService;
    private final ImageService imageService;
    //private final EmailService emailService;

    /*@Autowired
    private ApplicationEventPublisher eventPublisher;*/

    public SecurityController(UserService userService, ImageService imageService) {
        this.userService = userService;
        this.imageService = imageService;
        //this.emailService = emailService;

    }

    @GetMapping(PRIHLASENI_URL)
    public ModelAndView prihlaseni(@ModelAttribute(USER) User user) {
        ModelAndView mv = new ModelAndView(SECURITY_URL + PRIHLASENI_URL);
        mv.addObject(ACTION, PRIHLASENI_ACTION);
        mv.addObject(TITLE, PRIHLASENI_TITLE);
        return mv;
    }

    @GetMapping(DEFAULT_URL)
    public String defaultAfterLogin() {
        Collection<? extends GrantedAuthority> authorities;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        authorities = auth.getAuthorities();
        String myRole = authorities.toArray()[0].toString();
        if (myRole.equals(UserPrincipal.ROLE_PREFIX + Const.ROLE_ADMIN)) {
            return REDIRECT_URL + ADMIN_URL + CLANKY_URL;
        }
        return REDIRECT_URL + PORADNA_URL;
    }


    @GetMapping(REGISTRACE_URL)
    public ModelAndView registraceGet(@ModelAttribute("user") User user) {
        ModelAndView mv = new ModelAndView(SECURITY_URL + REGISTRACE_URL);
        mv.addObject(ACTION, PRIHLASENI_ACTION);
        mv.addObject(TITLE, PRIHLASENI_TITLE);
        mv.addObject(USER, new User());
        return mv;
    }


    @PostMapping(REGISTRACE_URL)
    public ModelAndView registracePost(@Valid @ModelAttribute(USER) User user, @RequestParam(FILE) MultipartFile file, BindingResult result) throws IOException {
        ModelAndView mv = new ModelAndView();
        if (result.hasErrors()) {
            mv.setViewName(SECURITY_URL + REGISTRACE_URL);
            mv.addObject(MESSAGE_ERROR, SPATNE_VYPLNENA_POLE_ERROR_MSG);
            mv.addObject(user);
            return mv;
        }

        if (userService.findByUsername(user.getUsername()) != null) {
            mv.setViewName(SECURITY_URL + REGISTRACE_URL);
            mv.addObject(MESSAGE_ERROR, "Uživatelské jméno obsazeno");
            mv.addObject(user);
            return mv;

        }

        if (userService.findByEmail(user.getEmail()).isPresent()) {
            mv.setViewName(SECURITY_URL + REGISTRACE_URL);
            mv.addObject(MESSAGE_ERROR, "Email obsazen");
            mv.addObject(user);

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
        user.setMatchingPassword(userService.encode(user.getMatchingPassword()));
        user.setRole(UserRole.USER);
        user.setAboutUs(false);
        userService.saveOrUpdate(user);
        mv.addObject(MESSAGE_SUCCESS, "Uživatel " + user.getUsername() + " byl vytvořen.");
        mv.addObject("user", user);
        mv.setViewName(REDIRECT_URL + PRIHLASENI_URL);
        return mv;
    }

    @GetMapping(ZAPOMENUTE_HESLO_URL)
    public String zapomenuteHesloGet() {
        return SECURITY_URL + ZAPOMENUTE_HESLO_URL;
    }

    // Process form submission from forgotPassword page
    @PostMapping(ZAPOMENUTE_HESLO_URL)
    public ModelAndView zapomenuteHesloPost(ModelAndView modelAndView, @RequestParam(EMAIL) String userEmail, HttpServletRequest request) {

        // Lookup user in database by e-mail
        Optional<User> optional = userService.findByEmail(userEmail);

        if (!optional.isPresent()) {
            modelAndView.addObject(MESSAGE_ERROR, "Uživatel s tímto emailem neexistuje.");
        } else {

            // Generate random 36-character string token for reset password
            User user = optional.get();
            user.setResetToken(UUID.randomUUID().toString());

            // Save token to database
            userService.saveOrUpdate(user);

            String appUrl = request.getScheme() + "://" + request.getServerName();

            // Email message
            SimpleMailMessage passwordResetEmail = new SimpleMailMessage();
            passwordResetEmail.setFrom("vitezslav.kanok@gmail.com");
            passwordResetEmail.setTo(user.getEmail());
            passwordResetEmail.setSubject("Požadavek na změnu hesla.");
            passwordResetEmail.setText("Pro změnu heslo klikněte zde\n" + appUrl
                    + "/obnovit-heslo?token=" + user.getResetToken());

            //emailService.sendEmail(passwordResetEmail);

            // Add success message to view
            modelAndView.addObject(MESSAGE_SUCCESS, "Odkaz na obnovu hesla byl odeslán na email " + userEmail);
        }

        modelAndView.setViewName(SECURITY_URL + PRIHLASENI_URL);
        return modelAndView;
    }

    @GetMapping(OBNOVIT_HESLO_URL)
    public ModelAndView obnovitHesloGet(ModelAndView modelAndView, @RequestParam(TOKEN) String token) {

        Optional<User> user = userService.findUserByResetToken(token);

        if (user.isPresent()) { // Token found in DB
            modelAndView.addObject("resetToken", token);
        } else { // Token not found in DB
            modelAndView.addObject(MESSAGE_ERROR, "Oops!   Špatný odkaz na obnovu hesla.");
            modelAndView.setViewName(REDIRECT_URL + PRIHLASENI_URL);
            return modelAndView;
        }

        modelAndView.setViewName(SECURITY_URL + OBNOVIT_HESLO_URL);
        return modelAndView;
    }

    @PostMapping(OBNOVIT_HESLO_URL)
    public ModelAndView obnovitHesloPost(ModelAndView modelAndView, @RequestParam Map<String, String> requestParams, RedirectAttributes redir) {

        // Find the user associated with the reset token
        Optional<User> user = userService.findUserByResetToken(requestParams.get(TOKEN));

        // This should always be non-null but we check just in case
        if (user.isPresent()) {

            User resetUser = user.get();

            // Set new password
            resetUser.setPassword(userService.encode(requestParams.get("password")));

            // Set the reset token to null so it cannot be used again
            resetUser.setResetToken(null);

            // Save user
            userService.saveOrUpdate(resetUser);

            // In order to set a model attribute on a redirect, we must use
            // RedirectAttributes
            redir.addFlashAttribute(MESSAGE_SUCCESS, "Úspěšně jste změnili své heslo.  Nyní se můžete přihlásit.");

            modelAndView.setViewName(REDIRECT_URL + PRIHLASENI_URL);
            return modelAndView;

        } else {
            modelAndView.addObject(MESSAGE_ERROR, "Oops!  Špatný odkaz na obnovu hesla.");
            modelAndView.setViewName(SECURITY_URL + PRIHLASENI_URL);
        }

        return modelAndView;
    }

    // Going to reset page without a token redirects to login page
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ModelAndView handleMissingParams(MissingServletRequestParameterException ex) {
        return new ModelAndView(REDIRECT_URL + PRIHLASENI_URL);
    }
}
