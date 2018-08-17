package org.andresoviedo.gdfao.security.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.session.SessionRepository;
import org.springframework.session.web.http.CookieHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;
import org.springframework.session.web.http.SessionRepositoryFilter;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Wrap {@link SessionRepository} to catch session serialization exceptions
 */
@Component
@Order(SessionRepositoryFilter.DEFAULT_ORDER - 1)
public class SessionDeserializationFailsafeFilter implements Filter {

    private static Logger logger = Logger.getLogger(SessionDeserializationFailsafeFilter.class.getName());
    private final HttpSessionIdResolver sessionIdResolver = new CookieHttpSessionIdResolver();
    @Autowired
    private SessionRepository<?> sessionRepository;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("SessionDeserializationFailsafeFilter enabled");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } catch (ConversionFailedException e) {
            if (e.getMessage() != null && e.getMessage().contains("local class incompatible: stream classdesc serialVersionUID")) {
                for (String cookieId : sessionIdResolver.resolveSessionIds((HttpServletRequest) request)) {
                    logger.info("Deleting session " + cookieId);
                    sessionRepository.deleteById(cookieId);
                }
            }
            ((HttpServletRequest) request).getSession();
            ((HttpServletResponse) response).sendRedirect("/");
        }
    }

    @Override
    public void destroy() {

    }
}