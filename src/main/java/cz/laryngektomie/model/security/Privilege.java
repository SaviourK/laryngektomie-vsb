package cz.laryngektomie.model.security;

import cz.laryngektomie.model.EntityBase;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotBlank;
import java.util.Collection;

@Entity
public class Privilege extends EntityBase {

    @NotBlank
    private String name;

    @ManyToMany(mappedBy = "privileges")
    private Collection<Role> roles;


    public Privilege() {
        super();
    }

    public Privilege(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }
}
