/*
package org.andresoviedo.gdfao.security.model;

import javax.persistence.*;

@Entity
@Table(name = "group_authorities", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"group_id", "authority"})
})
public class GroupAuthorities {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id ", nullable = false)
    private Group group;

    @ManyToOne
    @JoinColumn(name = "authority", nullable = false)
    private Authority authority;

    public GroupAuthorities() {
    }

    public GroupAuthorities(Group group, Authority authority) {
        this.group = group;
        this.authority = authority;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Authority getAuthority() {
        return authority;
    }

    public void setAuthority(Authority authority) {
        this.authority = authority;
    }
}
*/
