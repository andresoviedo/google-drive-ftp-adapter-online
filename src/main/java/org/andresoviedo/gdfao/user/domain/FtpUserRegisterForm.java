package org.andresoviedo.gdfao.user.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public final class FtpUserRegisterForm {

    @NotNull
    @Size(min = 5, max = 50)
    private String username;

    @NotNull
    @Size(min = 8, max = 50)
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
