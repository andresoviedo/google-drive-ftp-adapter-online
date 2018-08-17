package org.andresoviedo.gdfao.security.controller;

import org.andresoviedo.gdfao.security.repository.AuthoritiesRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.logging.Logger;

@Controller
public class LoginController {

    private static Logger logger = Logger.getLogger(LoginController.class.getName());

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Authentication authentication) {
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken || authentication.getAuthorities() == null) {
            logger.info("----------------- login guest -----------------");
            return "login";
        } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(AuthoritiesRepository.ROLE_ADMIN))) {
            logger.info("----------------- logged admin -----------------");
            return "redirect:/admin";
        } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(AuthoritiesRepository.ROLE_USER))) {
            logger.info("----------------- logged user -----------------");
            return "redirect:/user";
        }
        throw new RuntimeException("this should never happen");
    }
}
