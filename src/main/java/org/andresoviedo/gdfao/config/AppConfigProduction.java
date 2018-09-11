package org.andresoviedo.gdfao.config;

import ch.qos.logback.classic.LoggerContext;
import org.andresoviedo.util.email.MailInfoBean;
import org.andresoviedo.util.log.EmailLogbackAppender;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;

import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

@Order(1)
@Profile("production")
@Configuration
public class AppConfigProduction implements CommandLineRunner {

    private static Log logger = LogFactory.getLog(AppConfigProduction.class);

    @Autowired
    private EmailLogbackAppender emailAlertAppender;

    @Bean
    public MailInfoBean mailInfo(){
        final MailInfoBean mailInfo = new MailInfoBean("smtp.1and1.es", 587, "ftpdrive@andresoviedo.org",
                System.getProperty("PASSWORD"), "\"Google Drive FTP Adapter\" <ftpdrive@andresoviedo.org>",
                "ftpdrive@andresoviedo.org", "Google Drive FTP Drive Alert", null);
        mailInfo.setHTMLMessage(true);
        mailInfo.setUseTLS(true);
        return mailInfo;
    }

    @Bean
    public EmailLogbackAppender mailAlertAppender(MailInfoBean mailInfo){
        return new EmailLogbackAppender(mailInfo, TimeUnit.MINUTES.toMillis(60));
    }

    @Override
    public void run(String... args) {
        configureEmailLogAppender();
        logger.info("Email alert appender started");
    }

    /**
     * Send errors by email
     */
    private void configureEmailLogAppender() {
        if (System.getProperty("smtp.pass") == null){
            System.err.println("No smtp password. No emails will be sent");
            return;
        }

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        emailAlertAppender.setName("Email Appender");
        emailAlertAppender.setContext(context);
        emailAlertAppender.start();

        final ch.qos.logback.classic.Logger root = context.getLogger("ROOT");
        root.addAppender(emailAlertAppender);
    }

    @PreDestroy
    public void onDestroy() throws Exception {
        logger.info("Email alert appender stopped");
        emailAlertAppender.flush();
    }
}
