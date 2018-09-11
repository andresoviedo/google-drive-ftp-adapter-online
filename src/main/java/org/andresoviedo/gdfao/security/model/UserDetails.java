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

    private Boolean terms;

    public UserDetails() {
    }

    public UserDetails(User user, String email, Boolean isTerms) {
        this.username = user.getUsername();
        this.user = user;
        this.email = email;
        this.terms = isTerms;
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

    public void setTerms(Boolean isTerms) {
        this.terms = isTerms;
    }

    public Boolean isTerms() {
        return terms != null && terms;
    }
}
