package org.andresoviedo.gdfao.drive.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "drive_authorization")
public class DriveAuthorization {

    @Id
    private String id;

    public DriveAuthorization() {
    }

    public DriveAuthorization(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
