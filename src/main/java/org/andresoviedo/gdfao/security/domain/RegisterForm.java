package org.andresoviedo.gdfao.security.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public final class RegisterForm {

    @NotNull
    @Size(min = 5, max = 50)
    private String username;

    @NotNull
    @Size(min = 8, max = 2000)
    private String password;

    @NotNull
    @Size(min = 5, max = 50)
    private String email;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
