package org.andresoviedo.gdfao.user;

import org.andresoviedo.gdfao.drive.repository.DriveAuthorizationRepository;
import org.andresoviedo.gdfao.security.model.UserDetails;
import org.andresoviedo.gdfao.security.repository.UserDetailsRepository;
import org.andresoviedo.gdfao.security.repository.UserRepository;
import org.andresoviedo.gdfao.user.domain.FtpUserRegisterForm;
import org.andresoviedo.gdfao.user.model.FtpUser;
import org.andresoviedo.gdfao.user.repository.FtpUsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.security.Principal;
import java.util.logging.Logger;

@Controller
@RequestMapping("/user")
public class UserController {

    private static Logger logger = Logger.getLogger(UserController.class.getName());

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    private DriveAuthorizationRepository authorizationRepository;

    @Autowired
    private FtpUsersRepository ftpUsersRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @RequestMapping({"", "/"})
    public String user(Principal principal, Model model) {

        final UserDetails userDetails = userDetailsRepository.findByUsername(principal.getName());
        final String email = userDetails.getEmail();

        model.addAttribute("drive_auth",authorizationRepository.findById(email));


        model.addAttribute("ftp_user",ftpUsersRepository.findById(email));

        return "user";
    }

    @RequestMapping(value = "/addftpuser", method = RequestMethod.POST)
    public String addftpuser(@Valid FtpUserRegisterForm userRegisterForm, Principal principal, Model model){

        final UserDetails userDetails = userDetailsRepository.findByUsername(principal.getName());
        final String email = userDetails.getEmail();

        if (ftpUsersRepository.existsById(email)){
            logger.info("FTP user already registered for principal");
            //model.addAttribute("ftp_user",ftpUsersRepository.findById(principal.getName()));
            return "redirect:/user?code=ftp_user_already_exists";
        }

        if (ftpUsersRepository.existsByFtpusername(userRegisterForm.getUsername())){
            logger.info("FTP user already exists");
            return "redirect:/user?code=ftp_user_already_exists";
        }

        FtpUser ftpUser = new FtpUser(email, userRegisterForm.getUsername(), passwordEncoder.encode(userRegisterForm.getPassword()));
        ftpUsersRepository.save(ftpUser);
        logger.info("FTP user saved to database");

        return "redirect:/user?code=ok";
    }

    @RequestMapping("/deleteftpuser")
    public String addftpuser(Principal principal, Model model) {

        final UserDetails userDetails = userDetailsRepository.findByUsername(principal.getName());
        final String email = userDetails.getEmail();

        if (ftpUsersRepository.existsById(email)) {
            ftpUsersRepository.deleteById(email);
            logger.info("FTP user deleted");
        }

        return "redirect:/user?code=ok";
    }
}
