/*
package org.andresoviedo.gdfao.security.model;

import javax.persistence.*;

@Entity
@Table(name = "group_members", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"group_id", "username"})})
public class GroupMembers {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id ", nullable = false)
    private Group group;

    @ManyToOne
    @JoinColumn(name = "username", nullable = false)
    private User user;

    public GroupMembers() {
    }

    public GroupMembers(String id, User user, Group group) {
        this.id = id;
        this.user = user;
        this.group = group;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
}
*/
