package org.andresoviedo.gdfao.security.controller;

import org.andresoviedo.gdfao.security.domain.RegisterForm;
import org.andresoviedo.gdfao.security.model.User;
import org.andresoviedo.gdfao.security.model.UserDetails;
import org.andresoviedo.gdfao.security.repository.AuthoritiesRepository;
import org.andresoviedo.gdfao.security.repository.UserDetailsRepository;
import org.andresoviedo.gdfao.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
public class RegisterController {

    private static Logger logger = Logger.getLogger(RegisterController.class.getName());

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired(required = false)
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserDetailsRepository userDetailsRepository;
    @Autowired
    private AuthoritiesRepository authoritiesRepository;

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String register(Authentication authentication) {
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken || authentication.getAuthorities() == null) {
            logger.info("----------------- register guest -----------------");
            return "register";
        } else if (authentication.getAuthorities().stream().anyMatch(a -> ((GrantedAuthority) a).getAuthority().equals(AuthoritiesRepository.ROLE_ADMIN))) {
            logger.info("----------------- registered admin -----------------");
            return "redirect:/admin";
        } else if (authentication.getAuthorities().stream().anyMatch(a -> ((GrantedAuthority) a).getAuthority().equals(AuthoritiesRepository.ROLE_USER))) {
            logger.info("----------------- registered user -----------------");
            return "redirect:/user";
        }
        return "register";
    }

    @Transactional
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    // public String register(HttpServletRequest request, @RequestParam(required = true) String username, @RequestParam("password") String password, @RequestParam("email") String email) {
    public String register(@Valid RegisterForm registerForm) {

        logger.info("Processing registration request...");

        final String username = registerForm.getUsername();
        final String password = registerForm.getPassword();
        final String email = registerForm.getEmail();

        try {
            final String errorCode;
            if (username.equalsIgnoreCase(password)) {
                logger.warning("Attempt to register with username=password");
                errorCode = "invalid-password";
            } else if (username.toLowerCase().contains(password.toLowerCase())) {
                logger.warning("Attempt to register with username containing password");
                errorCode = "invalid-password";
            } else if (userRepository.existsByUsername(username)) {
                logger.warning("Attempt to register with already registered username");
                errorCode = "username";
            } else if (userDetailsRepository.existsByEmail(email)) {
                logger.warning("Attempt to register with already registered email");
                errorCode = "email";
            } else {
                errorCode = null;
            }

            if (errorCode != null) {
                logger.info("Redirecting user...");
                return "redirect:/register?code="+errorCode;
            }

            // everything is ok, let's register the user
            logger.info("Registering user " + username);

            User user = new User(username, passwordEncoder.encode(password), true,
                    Collections.singletonList(authoritiesRepository.findById(AuthoritiesRepository.ROLE_USER)));
            user = userRepository.save(user);

            UserDetails userDetails = new UserDetails(user, email);
            userDetailsRepository.save(userDetails);

            // authenticate to save in session by security filter (HttpSessionSecurityContextRepository)
            if (authenticationManager != null) {
                UsernamePasswordAuthenticationToken userPassAuthToken
                        = new UsernamePasswordAuthenticationToken(user, password);
                Authentication authentication = authenticationManager.authenticate(userPassAuthToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            logger.info("Successful registration: " + username);
            return "redirect:/login";

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error registering user", e);
            return "redirect:/?error";
        }
    }
}
