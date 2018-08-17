package org.andresoviedo.gdfao.user.model;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "ftp_users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"ftpusername"})})
public class FtpUser {

    @Id
    @Size(min = 5, max = 50)
    private String id;

    @Size(min = 5, max = 50)
    @Column(nullable = false)
    private String ftpusername;

    @Size(min = 8, max = 500)
    @Column(nullable = false)
    private String ftppassword;

    public FtpUser() {
    }

    public FtpUser(String id, String ftpusername, String ftppassword) {
        this.id = id;
        this.ftpusername = ftpusername;
        this.ftppassword = ftppassword;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFtpusername() {
        return ftpusername;
    }

    public void setFtpusername(String ftpusername) {
        this.ftpusername = ftpusername;
    }

    public String getFtppassword() {
        return ftppassword;
    }

    public void setFtppassword(String ftppassword) {
        this.ftppassword = ftppassword;
    }
}
