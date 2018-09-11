package org.andresoviedo.gdfao.admin.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public final class EmailCommForm {

    private String emailTo;

    @NotNull
    @Size(min = 5, max = 100)
    private String subject;

    @NotNull
    @Size(min = 50, max = 1000)
    private String message;

    public String getEmailTo() {
        return emailTo;
    }

    public void setEmailTo(String emailTo) {
        this.emailTo = emailTo;
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
}
