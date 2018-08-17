package org.andresoviedo.gdfao.security.model;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Size;

@Entity
public class Authority implements GrantedAuthority {

    /**
     * Authorities are saved by spring-session in database
     */
    private static final long serialVersionUID = 1;

    @Id
    @Size(max = 50)
    private String id;

    public Authority() {
    }

    public Authority(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getAuthority() {
        return id;
    }

    @Override
    public String toString() {
        return "Authority{" +
                "id='" + id + '\'' +
                '}';
    }
}
