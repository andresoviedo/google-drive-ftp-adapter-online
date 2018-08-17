package org.andresoviedo.util.email;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.util.Date;
import java.util.Properties;

/**
 * EMailSender can send an e-mail with file attachments to many recipients through a host needed authentication.
 *
 * @author andresoviedo
 * @version 1.0
 */

public class EmailSender {

    public static void send(MailInfoBean mailInfo) throws MessagingException {

        assert (mailInfo.getHost() != null && mailInfo.getUser() != null && mailInfo.getPass() != null);
        assert (mailInfo.getFrom() != null && mailInfo.getTo() != null && mailInfo.getSubject() != null && mailInfo.getMessage() != null);

        // parse mail address
        String[] to = mailInfo.getTo().split(";");
        Address[] addrTo = new InternetAddress[to.length];
        for (int i = 0; i < addrTo.length; i++)
            addrTo[i] = new InternetAddress(to[i]);

        // prepare carbon copy addresses
        Address[] addrCC = null;
        if (mailInfo.getCC() != null) {
            String[] cc = mailInfo.getCC().split(";");
            addrCC = new InternetAddress[cc.length];
            for (int i = 0; i < cc.length; i++)
                addrCC[i] = new InternetAddress(cc[i]);
        }

        // prepare blind carbon copy addresses
        Address[] addrBCC = null;
        if (mailInfo.getBCC() != null) {
            String[] bcc = mailInfo.getBCC().split(";");
            addrBCC = new InternetAddress[bcc.length];
            for (int i = 0; i < bcc.length; i++)
                addrBCC[i] = new InternetAddress(bcc[i]);
        }

        // Set mail properties
        Properties p = new Properties();
        p.put("mail.smtp.host", mailInfo.getHost());
        p.put("mail.smtp.port", String.valueOf(mailInfo.getPort()));
        if (mailInfo.isUseTLS()) {
            p.put("mail.smtp.starttls.enable", "true");
            /*p.put("mail.smtp.socketFactory.port", String.valueOf(mailInfo.getPort()));
            p.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            p.put("mail.smtp.socketFactory.fallback", "false");*/
        }

        // Setup authenticator
        Authenticator auth = null;
        if (mailInfo.getUser() != null && mailInfo.getPass() != null) {
            // p.put("mail.user", mailInfo.getUser());
            p.put("mail.smtp.auth", "true");
            auth = new MailAuthenticator(mailInfo.getUser(), mailInfo.getPass());
        }

        // Get session
        Session session = Session.getInstance(p, auth);

        MimeMessage mm = new MimeMessage(session);
        mm.setFrom(new InternetAddress(mailInfo.getFrom()));
        mm.setSubject(mailInfo.getSubject());
        mm.setRecipients(Message.RecipientType.TO, addrTo);
        if (addrCC != null) {
            mm.setRecipients(Message.RecipientType.CC, addrCC);
        }
        if (addrBCC != null) {
            mm.setRecipients(Message.RecipientType.BCC, addrBCC);
        }
        mm.setSentDate(new Date());

        // Prepare contents
        Multipart mp = new MimeMultipart();

        // Message part
        MimeBodyPart textPart = new MimeBodyPart();
        if (mailInfo.getMessageEncoding() == null) {
            textPart.setText(mailInfo.getMessage());
        } else {
            textPart.setText(mailInfo.getMessage(), mailInfo.getMessageEncoding());
        }
        mp.addBodyPart(textPart);
        if (mailInfo.isHTMLMessage()) {
            textPart.setHeader("Content-Type", "text/html");
        }

        // Attachments part
        if (mailInfo.getFiles() != null) {
            int i = 0;
            for (File file : mailInfo.getFiles()) {
                MimeBodyPart filePart = new MimeBodyPart();
                FileDataSource fds = new FileDataSource(file);
                filePart.setDataHandler(new DataHandler(fds));
                filePart.setFileName(file.getName());
                filePart.setHeader("Content-Transfer Encoding", "BASE64");
                filePart.setHeader("Content-ID", "file" + i++);
                mp.addBodyPart(filePart);
            }
        }

        // Set mail content
        mm.setContent(mp);

        // Send mail
        Transport.send(mm);
    }

    /**
     * 1&1
     * smtp.1and1.es 587
     * @param args
     */
    public static void main(String[] args) {

        MailInfoBean mailInfo = new MailInfoBean("smtp.1and1.es", 587, "ftpdrive@andresoviedo.org", "secret", "\"Google Drive FTP Adapter\" <ftpdrive@andresoviedo.org>",
                "\"Andres Oviedo\" <andresoviedo@gmail.com>", "Hola test",
                "<html><body><h1>Email testing</h1></body></html>");
        mailInfo.setUseTLS(true);

        try {
            System.out.println("Sending mail...");
            EmailSender.send(mailInfo);
            System.out.println("Mail sent succesfully");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}