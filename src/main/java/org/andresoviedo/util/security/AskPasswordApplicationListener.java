package org.andresoviedo.util.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;

import java.awt.*;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class AskPasswordApplicationListener implements ApplicationListener {

    private static Log logger = LogFactory.getLog(AskPasswordApplicationListener.class);

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationStartingEvent) {
            if (Arrays.asList(((ApplicationStartingEvent) event).getArgs()).contains("--password")) {
                String password;
                if (System.console() != null) {
                    Console console = System.console();
                    password = new String(console.readPassword("Password: "));
                }
                else if (!GraphicsEnvironment.isHeadless()){
                    AskPasswordDialog passwordDialog = new AskPasswordDialog();
                    password = passwordDialog.show();
                } else {
                    InputStream in=System.in;
                    int max=50;
                    byte[] b=new byte[max];

                    int l= 0;
                    try {
                        System.out.print("Password: ");
                        l = in.read(b);
                        l--;//last character is \n
                        if (l>0) {
                            byte[] e=new byte[l];
                            System.arraycopy(b,0, e, 0, l);
                            password = new String(e);
                        } else {
                            password = "";
                        }
                    } catch (IOException e) {
                        password = null;
                    }

                }
                if (password == null || password.length() == 0){
                    logger.warn("No password set");
                }
                System.setProperty("PASSWORD", password);
            }
        }
    }
}
