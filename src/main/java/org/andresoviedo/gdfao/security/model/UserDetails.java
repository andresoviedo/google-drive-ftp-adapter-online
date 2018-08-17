package org.andresoviedo.gdfao.security.model;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "users_details")
public class UserDetails {

    @Id
    private String username;

    @Size(max = 100)
    private String email;

    @OneToOne
    @JoinColumn(name = "username")
    private User user;

    public UserDetails() {
    }

    public UserDetails(User user, String email) {
        this.username = user.getUsername();
        this.user = user;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
