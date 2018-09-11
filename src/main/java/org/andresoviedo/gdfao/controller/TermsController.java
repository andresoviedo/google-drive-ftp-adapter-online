package org.andresoviedo.gdfao.controller;

import org.andresoviedo.gdfao.security.model.UserDetails;
import org.andresoviedo.gdfao.security.repository.AuthoritiesRepository;
import org.andresoviedo.gdfao.security.repository.UserDetailsRepository;
import org.andresoviedo.gdfao.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
public class TermsController {

    private static Logger logger = Logger.getLogger(TermsController.class.getName());

    @Autowired(required = false)
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserDetailsRepository userDetailsRepository;
    @Autowired
    private AuthoritiesRepository authoritiesRepository;

    @RequestMapping(value = "/terms", method = RequestMethod.GET)
    public String terms(){
        return "terms";
    }

    @Transactional
    @RequestMapping(value = "/terms", method = RequestMethod.POST)
    public String accept(Principal principal, @RequestParam(name="terms") boolean isTerms) {
        logger.info("Processing accepting terms request...");

        try {
            final String username = principal.getName();
            final String errorCode;
            if (!isTerms) {
                logger.warning("Attempt to register without accepting terms");
                errorCode = "terms";
            } else {
                errorCode = null;
            }

            if (errorCode != null) {
                logger.info("Redirecting user...");
                return "redirect:/terms?code="+errorCode;
            }

            // everything is ok, let's register the user
            logger.info("Accepting terms " + username);

            UserDetails userDetails = userDetailsRepository.findByUsername(username);
            for (UserDetails details : userDetailsRepository.findByEmail(userDetails.getEmail())) {
                details.setTerms(isTerms);
                userDetailsRepository.save(details);
            }

            logger.info("Successful terms: " + username);
            return "redirect:/user?code=ok";

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error accepting terms for user", e);
            return "redirect:/?error";
        }
    }
}
