package org.andresoviedo.gdfao.drive;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeServlet;
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
import java.util.logging.Logger;

@Service("drive-login")
public class GoogleAuthorizationServlet extends AbstractAuthorizationCodeServlet {

    private static Logger logger = Logger.getLogger(GoogleAuthorizationServlet.class.getName());

    @Autowired
    private AuthorizationCodeFlow flow;

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    private DriveAuthorizationRepository driveAuthorizationRepository;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        // authorized drive access
        logger.info("Authorized drive access");

        final Principal principal = request.getUserPrincipal();
        final UserDetails userDetails = userDetailsRepository.findByUsername(principal.getName());

        if (!driveAuthorizationRepository.existsById(userDetails.getEmail())){
            logger.info("Saving authorization");
            DriveAuthorization authorization = new DriveAuthorization(userDetails.getEmail());
            driveAuthorizationRepository.save(authorization);
        }

        String callback = request.getParameter("callback");
        if (callback == null){
            callback = "/";
        }
        logger.info("Redirecting to "+callback+"...");
        response.sendRedirect(callback+"?code=ok");
    }

    @Override
    protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
        GenericUrl url = new GenericUrl(req.getRequestURL().toString());
        url.setRawPath("/drive-login-oauth2callback/");
        req.getSession().setAttribute("drive-login-callback",req.getParameter("callback"));
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