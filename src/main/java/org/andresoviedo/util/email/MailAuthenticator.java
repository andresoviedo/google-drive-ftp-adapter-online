package org.andresoviedo.util.email;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

class MailAuthenticator extends Authenticator {
    private String _user = "";
    private String _pwd = "";

    public MailAuthenticator(String user, String pwd) {
        _user = user;
        _pwd = pwd;
    }

    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(_user, _pwd);
    }
}
