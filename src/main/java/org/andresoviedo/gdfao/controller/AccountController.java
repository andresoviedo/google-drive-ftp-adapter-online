package org.andresoviedo.gdfao.controller;

import org.andresoviedo.gdfao.drive.repository.DriveAuthorizationRepository;
import org.andresoviedo.gdfao.security.model.UserDetails;
import org.andresoviedo.gdfao.security.repository.UserDetailsRepository;
import org.andresoviedo.gdfao.security.repository.UserRepository;
import org.andresoviedo.gdfao.user.repository.FtpUsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.security.Principal;
import java.util.List;
import java.util.logging.Logger;

@Controller
@RequestMapping("/account")
public class AccountController {

    private static Logger logger = Logger.getLogger(AccountController.class.getName());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    private DriveAuthorizationRepository driveAuthorizationRepository;

    @Autowired
    private FtpUsersRepository ftpUsersRepository;

    @RequestMapping(value = {"","/"}, method = RequestMethod.GET)
    public String login(Principal principal, Model model) {

        logger.info("Returning account page");

        final UserDetails userDetails = userDetailsRepository.findByUsername(principal.getName());

        model.addAttribute("userDetails",userDetails);

        return "account";
    }

    @Transactional
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String delete(HttpServletRequest request, Principal principal){

        logger.info("Deleting account...");
        UserDetails userDetails = userDetailsRepository.findByUsername(principal.getName());

        if (ftpUsersRepository.existsById(userDetails.getEmail())) {
            ftpUsersRepository.deleteById(userDetails.getEmail());
        }

        if (driveAuthorizationRepository.existsById(userDetails.getEmail())) {
            driveAuthorizationRepository.deleteById(userDetails.getEmail());
        }

        final List<UserDetails> allUserDetails = userDetailsRepository.findByEmail(userDetails.getEmail());
        userDetailsRepository.deleteByEmail(userDetails.getEmail());
        for (UserDetails ud : allUserDetails){
            userRepository.deleteByUsername(ud.getUsername());
        }

        request.getSession().invalidate();

        return "redirect:/?action=logout";
    }
}
