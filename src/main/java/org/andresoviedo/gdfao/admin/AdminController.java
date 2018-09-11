package org.andresoviedo.gdfao.admin;

import org.andresoviedo.gdfao.admin.domain.EmailCommForm;
import org.andresoviedo.gdfao.security.model.UserDetails;
import org.andresoviedo.gdfao.security.repository.UserDetailsRepository;
import org.andresoviedo.util.email.EmailSender;
import org.andresoviedo.util.email.MailInfoBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static Logger logger = Logger.getLogger(AdminController.class.getName());

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    private MailInfoBean mailInfo;

    @RequestMapping({"", "/"})
    public String index(Authentication authentication) {
        return "admin";
    }

    @RequestMapping(value="/email", method = RequestMethod.GET)
    public String email(){
        return "email";
    }

    @RequestMapping(value = "/email", method = RequestMethod.POST)
    public String sendEmail(@Valid EmailCommForm commForm){
        logger.info("Processing sending email request...");
        MailInfoBean mail = mailInfo.clone();
        if (commForm.getEmailTo() != null && commForm.getEmailTo().length() > 0){
            mail.setTo(commForm.getEmailTo());
        } else {
            List<UserDetails> allUsers = userDetailsRepository.findAll();
            logger.info("Found "+allUsers.size()+" users");
            if (allUsers.isEmpty()){
                return "redirect:/admin/email?code=no";
            } else {
                Set<String> emails = new HashSet<>();
                allUsers.forEach((d)->emails.add(d.getEmail()));
                emails.add("ftpdrive@andresoviedo.org");
                StringBuilder emailsBcc = new StringBuilder();
                emails.forEach((e)->emailsBcc.append(e).append(";"));
                mail.setBCC(emailsBcc.toString());
            }
        }

        mail.setSubject(commForm.getSubject());
        mail.setMessage(commForm.getMessage());
        mail.setHTMLMessage(true);
        logger.info("Sending email...");
        try {
            EmailSender.send(mail);
            logger.info("Email sent");
        } catch (MessagingException e) {
            logger.log(Level.SEVERE,"Error sending email: "+e.getMessage(), e);
            return "redirect:/admin/email?error";
        }
        return "redirect:/admin/email?code=ok";
    }
}