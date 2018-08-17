package org.andresoviedo.gdfao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@SpringBootApplication
public class Main {

    private static Log logger = LogFactory.getLog(Main.class);

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @PostConstruct
    public void postConstruct() {
        logger.info("Application started");
    }

    @PreDestroy
    public void preDestroy() throws Exception {
        logger.info("Application stopped");
    }
}
