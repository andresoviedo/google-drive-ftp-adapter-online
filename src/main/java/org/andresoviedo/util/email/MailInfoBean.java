package org.andresoviedo.util.email;

import java.io.File;

public class MailInfoBean {

    private String host;
    private int port;
    private String user;
    private String pass;

    private String from;
    private String to;
    private String cc;
    private String bcc;
    private String subject;
    private String message;
    private String messageEncoding;
    private File[] files;

    private boolean useTLS;

    private boolean isHTMLMessage;

    public MailInfoBean() {
    }

    public MailInfoBean(String host, String from, String to, String subject, String message) {
        this(host, 25, null, null, from, to, subject, message);
    }

    public MailInfoBean(String host, String user, String pass, String from, String to, String subject, String message) {
        this(host, 25, user, pass, from, to, subject, message);
    }

    public MailInfoBean(String host, int port, String from, String to, String subject, String message) {
        this(host, port, null, null, from, to, subject, message);
    }

    public MailInfoBean(String host, int port, String user, String pass, String from, String to, String subject, String message) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.pass = pass;
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.message = message;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getCC() {
        return cc;
    }

    public void setCC(String cc) {
        this.cc = cc;
    }

    public String getBCC() {
        return bcc;
    }

    public void setBCC(String bcc) {
        this.bcc = bcc;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setHTMLMessage(boolean isHTMLMessage) {
        this.isHTMLMessage = isHTMLMessage;
    }

    public boolean isHTMLMessage() {
        return isHTMLMessage;
    }

    public File[] getFiles() {
        return files;
    }

    public void setFiles(File[] files) {
        this.files = files;
    }

    public boolean isUseTLS() {
        return useTLS;
    }

    public void setUseTLS(boolean useTLS) {
        this.useTLS = useTLS;
    }

    public String getMessageEncoding() {
        return messageEncoding;
    }

    public void setMessageEncoding(String messageEncoding) {
        this.messageEncoding = messageEncoding;
    }

    @Override
    public MailInfoBean clone(){
        MailInfoBean mailInfoBean = new MailInfoBean(host, port, user, pass, from, to, subject, message);
        mailInfoBean.setUseTLS(useTLS);
        return mailInfoBean;
    }

}