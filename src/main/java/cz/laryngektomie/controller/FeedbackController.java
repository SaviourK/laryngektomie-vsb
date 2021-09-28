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

@Controller
@RequestMapping("kontakt")
public class FeedbackController {

    //private EmailService emailService;

    /*public FeedbackController(EmailService emailService) {
        this.emailService = emailService;
    }*/

    @GetMapping()
    public ModelAndView kontakt(@ModelAttribute("feedback") Feedback feedback) {
        ModelAndView mv = new ModelAndView("kontakt");
        mv.addObject("action", "kontakt");
        return mv;
    }

    @PostMapping()
    public ModelAndView sendFeedback(@Valid @ModelAttribute("feedback") Feedback feedback, BindingResult result) {
        ModelAndView mv = new ModelAndView();
        if (result.hasErrors()) {
            mv.setViewName("kontakt");
            mv.addObject("messageError", "Špatně vyplněné některé pole.");
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
        mv.setViewName("redirect:kontakt");
        mv.addObject("messageSuccess", "Děkujeme za zprávu " + feedback.getName() + ".");
        return mv;
    }
}
