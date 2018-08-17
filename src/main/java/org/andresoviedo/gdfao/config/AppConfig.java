package org.andresoviedo.gdfao.config;

import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.andresoviedo.gdfao.security.model.Authority;
import org.andresoviedo.gdfao.security.model.User;
import org.andresoviedo.gdfao.security.model.UserDetails;
import org.andresoviedo.gdfao.security.repository.AuthoritiesRepository;
import org.andresoviedo.gdfao.security.repository.UserDetailsRepository;
import org.andresoviedo.gdfao.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Configuration
public class AppConfig extends WebSecurityConfigurerAdapter implements CommandLineRunner {

    private static Logger logger = Logger.getLogger(AppConfig.class.getName());

    @Autowired
    private DataSource dataSource;

    @Autowired
    private AuthoritiesRepository authoritiesRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public LayoutDialect layoutDialect() {
        return new LayoutDialect();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // TODO: reenable csrf
        // TODO: frame options

        http.csrf().disable().headers().frameOptions().disable()

                .and().authorizeRequests()

                // administration
                .antMatchers("/h2-console/**").hasAnyRole("ADMIN")

                // app
                .antMatchers("/admin/**").hasAnyRole("ADMIN")
                .antMatchers("/user/**").hasAnyRole("USER")
                .antMatchers("/account/**").hasAnyRole("USER")

                // google drive
                .antMatchers("/drive-login/**").hasAnyRole("USER")
                .antMatchers("/drive-login-oauth2callback/**").hasAnyRole("USER")
                .antMatchers("/google-drive/**").hasAnyRole("USER")

                .and().
                formLogin().loginPage("/login").defaultSuccessUrl("/login").failureUrl("/?error=auth")

                .and()
                .logout().logoutSuccessUrl("/?logout");

        logger.info("Http security configured");
    }

    @Transactional
    @Override
    public void run(String... args) {

        try {
            Authority userAuthority = authoritiesRepository.findById(AuthoritiesRepository.ROLE_USER);
            if (userAuthority == null) {
                logger.info("Registering role " + AuthoritiesRepository.ROLE_USER);
                userAuthority = authoritiesRepository.save(new Authority(AuthoritiesRepository.ROLE_USER));
            }
            Authority adminAuthority = authoritiesRepository.findById(AuthoritiesRepository.ROLE_ADMIN);
            if (adminAuthority == null) {
                logger.info("Registering role " + AuthoritiesRepository.ROLE_ADMIN);
                adminAuthority = authoritiesRepository.save(new Authority(AuthoritiesRepository.ROLE_ADMIN));
            }
            User admin = userRepository.findByUsername("admin");
            if (admin == null) {

                List<Authority> adminAuthorities = new ArrayList<>();
                adminAuthorities.add(userAuthority);
                adminAuthorities.add(adminAuthority);

                logger.info("Registering admin user...");
                User user = new User("admin", passwordEncoder.encode("changeit"), true, adminAuthorities);
                user = userRepository.save(user);

                logger.info("Registering admin details...");
                UserDetails userDetails = new UserDetails(user, "admin@example.com");
                userDetailsRepository.save(userDetails);
                logger.severe("Admin user 'admin' with password 'changeit' has benn registered. Please change it.");
            } else if (passwordEncoder.matches("changeit", admin.getPassword())) {
                logger.severe("You have not yet changed admin password 'changeit'. Please change it..");
                System.err.println("You have not yet changed admin password 'changeit'. Please change it..");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    // https://github.com/spring-projects/spring-session/issues/529
}
