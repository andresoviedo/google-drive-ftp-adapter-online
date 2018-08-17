package org.andresoviedo.gdfao.drive;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeCallbackServlet;
import com.google.api.client.http.GenericUrl;
import org.andresoviedo.gdfao.drive.repository.DriveAuthorizationRepository;
import org.andresoviedo.gdfao.drive.model.DriveAuthorization;
import org.andresoviedo.gdfao.security.model.UserDetails;
import org.andresoviedo.gdfao.security.repository.UserDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.logging.Logger;

@Service("drive-login-oauth2callback")
public class GoogleAuthorizationCallbackServlet extends AbstractAuthorizationCodeCallbackServlet {

    private static Logger logger = Logger.getLogger(GoogleAuthorizationCallbackServlet.class.getName());

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    private AuthorizationCodeFlow flow;

    @Autowired
    private DriveAuthorizationRepository driveAuthorizationRepository;


    @Override
    protected void onSuccess(HttpServletRequest req, HttpServletResponse resp, Credential credential)
            throws ServletException, IOException {

        // authorized drive access
        logger.info("Authorized drive access");

        final Principal principal = req.getUserPrincipal();
        final UserDetails userDetails = userDetailsRepository.findByUsername(principal.getName());

        if (!driveAuthorizationRepository.existsById(userDetails.getEmail())){
            logger.info("Saving authorization");
            DriveAuthorization authorization = new DriveAuthorization(userDetails.getEmail());
            driveAuthorizationRepository.save(authorization);
        }

        String callback = (String) req.getSession().getAttribute("drive-login-callback");
        req.getSession().removeAttribute("drive-login-callback");
        if (callback == null){
            callback = "/";
        }
        logger.info("Redirecting to "+callback+"...");
        resp.sendRedirect(callback+"?code=ok");
    }

    @Override
    protected void onError(
            HttpServletRequest req, HttpServletResponse resp, AuthorizationCodeResponseUrl errorResponse)
            throws ServletException, IOException {
        // handle error
        logger.severe("drive authorization callback error");
        String callback = (String) req.getSession().getAttribute("drive-login-callback");
        req.getSession().removeAttribute("drive-login-callback");
        resp.sendRedirect(callback+"?error");
    }

    @Override
    protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
        GenericUrl url = new GenericUrl(req.getRequestURL().toString());
        url.setRawPath("/drive-login-oauth2callback/");
        return url.build();
    }

    @Override
    protected AuthorizationCodeFlow initializeFlow() throws IOException {
        return flow;
    }

    @Override
    protected String getUserId(HttpServletRequest req) throws ServletException, IOException {
        final Principal principal = req.getUserPrincipal();

        final UserDetails userDetails = userDetailsRepository.findByUsername(principal.getName());

        return userDetails.getEmail();
    }
}