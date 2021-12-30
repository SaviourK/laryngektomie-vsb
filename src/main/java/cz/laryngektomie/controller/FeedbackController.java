package cz.laryngektomie.controller;

import cz.laryngektomie.model.feedback.Feedback;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

import static cz.laryngektomie.helper.Const.*;
import static cz.laryngektomie.helper.UrlConst.KONTAKT_URL;
import static cz.laryngektomie.helper.UrlConst.REDIRECT_URL;

@Controller
@RequestMapping(KONTAKT_URL)
public class FeedbackController {

    public static final String KONTAKT_TEXT = "kontakt";

    //private EmailService emailService;

    /*public FeedbackController(EmailService emailService) {
        this.emailService = emailService;
    }*/

    @GetMapping()
    public ModelAndView kontakt(@ModelAttribute(FEEDBACK) Feedback feedback) {
        ModelAndView mv = new ModelAndView(KONTAKT_TEXT);
        mv.addObject(ACTION, KONTAKT_TEXT);
        return mv;
    }

    @PostMapping()
    public ModelAndView sendFeedback(@Valid @ModelAttribute(FEEDBACK) Feedback feedback, BindingResult result) {
        ModelAndView mv = new ModelAndView();
        if (result.hasErrors()) {
            mv.setViewName(KONTAKT_TEXT);
            mv.addObject(MESSAGE_ERROR, "Špatně vyplněné některé pole.");
            return mv;
        }

        // Create an email instance
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("vitezslav.kanok@gmail.com");
        mailMessage.setTo(feedback.getEmail());
        mailMessage.setSubject("Nová zpráva od " + feedback.getName());
        mailMessage.setText(String.format("Zpráva od %s,\r\n Emailova adresa: %s,\r\n Zpráva:\r\n %s", feedback.getName(), feedback.getEmail(), feedback.getFeedback()));
        // Send mail
        //emailService.sendEmail(mailMessage);
        mv.setViewName(REDIRECT_URL + KONTAKT_URL);
        mv.addObject(MESSAGE_SUCCESS, "Děkujeme za zprávu " + feedback.getName() + ".");
        return mv;
    }
}
