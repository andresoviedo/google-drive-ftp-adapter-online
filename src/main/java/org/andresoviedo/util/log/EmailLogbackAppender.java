package org.andresoviedo.util.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import org.andresoviedo.util.email.EmailSender;
import org.andresoviedo.util.email.MailInfoBean;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class EmailLogbackAppender extends AppenderBase<ILoggingEvent> {

    private final Timer sendEmailTimer = new Timer(true);

    private final List<String> messages = new ArrayList<>();

    private final MailInfoBean mailInfo;

    public EmailLogbackAppender(MailInfoBean mailInfo, long period) {
        this.mailInfo = mailInfo;
        sendEmailTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    sendEmail();
                } catch (Exception e) {
                    System.err.println("Email alert sending error: "+e.getMessage());
                }
            }
        }, 0, period);
    }

    @Override
    protected void append(ILoggingEvent iLoggingEvent) {
        if (iLoggingEvent.getLevel().isGreaterOrEqual(Level.WARN)){
            synchronized (messages) {
                messages.add(iLoggingEvent.getMessage());
            }
            checkSend();
        }
    }

    private void checkSend(){
        int size = 0;
        synchronized (messages) {
            size = messages.size();
        }
        if (size >= 100){
            try {
                sendEmail();
            } catch (MessagingException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public void flush() throws MessagingException {
        sendEmail();
    }

    private synchronized void sendEmail() throws MessagingException {
        if (messages.isEmpty()){
            return;
        }

        System.out.println("Sending email alert...");
        final StringBuilder msg = new StringBuilder("<html><body>");
        synchronized (messages){
            messages.forEach(m->msg.append(m).append("<br>"));
            messages.clear();
        }
        msg.append("</body></html>");
        MailInfoBean mail = mailInfo.clone();
        mail.setMessage(msg.toString());
        mail.setHTMLMessage(true);
        System.out.println("Sending...");
        EmailSender.send(mail);
        System.out.println("Alert email sent");
    }
}
