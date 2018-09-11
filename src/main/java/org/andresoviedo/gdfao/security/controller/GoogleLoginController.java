package org.andresoviedo.gdfao.security.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.andresoviedo.gdfao.security.SecurityConfig;
import org.andresoviedo.gdfao.security.model.User;
import org.andresoviedo.gdfao.security.model.UserDetails;
import org.andresoviedo.gdfao.security.repository.AuthoritiesRepository;
import org.andresoviedo.gdfao.security.repository.UserDetailsRepository;
import org.andresoviedo.gdfao.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/google")
public class GoogleLoginController {

    private static Logger logger = Logger.getLogger(GoogleLoginController.class.getName());

    private static final String CLIENT_ID = "275751503302-dl2bmgru2varjibm3vf12fk4m71onbib.apps.googleusercontent.com";

    private static final String SSO_GOOGLE_USERNAME_PREFIX = "GOOGLE-";

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthoritiesRepository authoritiesRepository;

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Transactional
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Map login(@RequestParam(value = "token", required = true) final String idTokenString) throws GeneralSecurityException, IOException {

        String validatedEmail = validateToken(idTokenString);

        if (validatedEmail == null){
            Map<String,String> ret = new HashMap<>();
            ret.put("status","ko");
            return ret;
        }

        final String ssoGoogleUsername = SecurityConfig.SSO_USERNAME_PREFIX+SSO_GOOGLE_USERNAME_PREFIX+validatedEmail;
        if (userRepository.existsByUsername(ssoGoogleUsername)){

            // google user previously registered
            logger.info("Google user already registered");

            // check token changes...
            User user = userRepository.findByUsername(ssoGoogleUsername);
            if (!passwordEncoder.matches(idTokenString,user.getPassword())){
                logger.info("Valid token. Updating user credentials...");
                user.setPassword(passwordEncoder.encode(idTokenString));
                userRepository.save(user);
            }

            Map<String,String> ret = new HashMap<>();
            ret.put("status","registered");
            ret.put("username",ssoGoogleUsername);
            // ret.put("email",validatedEmail);
            return ret;
        } else {

            // google user not registered
            logger.info("Google user not registered");

            // everything is ok, let's register the user
            User user = new User(ssoGoogleUsername, passwordEncoder.encode(idTokenString), true,
                Collections.singletonList(authoritiesRepository.findById(AuthoritiesRepository.ROLE_USER)));
            userRepository.save(user);

            UserDetails userDetails = new UserDetails(user, validatedEmail, true);
            userDetailsRepository.save(userDetails);

            // google user registered
            logger.info("Successful google user registration");

            Map<String,String> ret = new HashMap<>();
            ret.put("status","registered");
            ret.put("username",ssoGoogleUsername);
            return ret;
        }
    }

    private String validateToken(@RequestParam(value = "token", required = true) String idTokenString) throws GeneralSecurityException, IOException {
        logger.info("Validating idTokenString...");

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance())
                // Specify the CLIENT_ID of the app that accesses the backend:
                .setAudience(Collections.singletonList(CLIENT_ID))
                // Or, if multiple clients access the backend:
                //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
                .build();

        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken == null) {
            System.out.println("Invalid ID token.");
            return null;
        }

        GoogleIdToken.Payload payload = idToken.getPayload();
        String email = payload.getEmail();

        // Print user identifier
        //String userId = payload.getSubject();
        //System.out.println("User ID: " + userId);

        // Get profile information from payload
        /*boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
        String name = (String) payload.get("name");
        String pictureUrl = (String) payload.get("picture");
        String locale = (String) payload.get("locale");
        String familyName = (String) payload.get("family_name");
        String givenName = (String) payload.get("given_name");

        System.out.println("email: " + email);
        System.out.println("emailVerified: " + emailVerified);
        System.out.println("name: " + name);*/


        return email;
    }
}
