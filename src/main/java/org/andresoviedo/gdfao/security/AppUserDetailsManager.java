package org.andresoviedo.gdfao.security;

import org.andresoviedo.gdfao.security.model.User;
import org.andresoviedo.gdfao.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;

import javax.transaction.Transactional;
import java.util.logging.Logger;

public class AppUserDetailsManager implements UserDetailsManager {

    private static Logger logger = Logger.getLogger(AppUserDetailsManager.class.getName());

    @Autowired
    private UserRepository userRepository;

    private AuthenticationManager authenticationManager;


    /**
     * Set {@link AuthenticationManager} to reauthenticate user when changing password: {@link #changePassword(String, String)}
     *
     * @param authenticationManager the authentication manager
     */
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void createUser(UserDetails user) {
        userRepository.save((User) user);
    }

    @Override
    public void updateUser(UserDetails user) {
        userRepository.save((User) user);
    }

    @Override
    public void deleteUser(String username) {
        userRepository.deleteByUsername(username);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        Authentication currentUser = SecurityContextHolder.getContext()
                .getAuthentication();

        if (currentUser == null) {
            // This would indicate bad coding somewhere
            throw new AccessDeniedException(
                    "Can't change password as no Authentication object found in context "
                            + "for current user.");
        }

        String username = currentUser.getName();

        // If an authentication manager has been set, re-authenticate the user with the
        // supplied password.
        if (authenticationManager != null) {
            logger.info("Reauthenticating user '" + username
                    + "' for password change request.");

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    username, oldPassword));
        } else {
            logger.fine("No authentication manager set. Password won't be re-checked.");
        }

        logger.info("Changing password for user '" + username + "'");

        User user = (User) currentUser.getDetails();
        user.setPassword(newPassword);
        userRepository.save(user);

        logger.info("Reauthenticating user '" + username + "'");
        UsernamePasswordAuthenticationToken newAuthentication = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities());
        newAuthentication.setDetails(user);
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);
    }

    @Override
    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            logger.warning("No user found with username '" + username + "'");
            throw new UsernameNotFoundException("No user found with username '" + username + "'");
        }
        // we initialize roles because spring ask for them out of this transactional method
        user.getAuthorities().size();
        return user;
    }
}
