package org.andresoviedo.gdfao.drive;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import org.andresoviedo.gdfao.drive.repository.DriveAuthorizationRepository;
import org.andresoviedo.gdfao.security.repository.UserDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.security.Principal;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
@RequestMapping("/google-drive")
public class GoogleDriveController {

    private static Logger logger = Logger.getLogger(GoogleDriveController.class.getName());

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    private DriveAuthorizationRepository driveAuthorizationRepository;

    @Autowired
    private AuthorizationCodeFlow flow;

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String ungrant(Principal principal, @RequestParam(name="callback") String urlCallback){
        logger.info("Deleting authorization");
        try {

            final String email = userDetailsRepository.findByUsername(principal.getName()).getEmail();

            flow.getCredentialDataStore().delete(email);
            driveAuthorizationRepository.deleteById(email);
            return "redirect:"+urlCallback+"?code=ok";
        } catch (IOException e) {
            logger.log(Level.SEVERE,"Problem deleting authorization",e);
            return "redirect:"+urlCallback+"?error";
        }
    }
}
