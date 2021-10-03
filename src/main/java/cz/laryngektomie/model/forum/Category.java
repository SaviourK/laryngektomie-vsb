package cz.laryngektomie.model.forum;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.laryngektomie.model.EntityBase;
import cz.laryngektomie.model.security.User;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Collection;

@Entity
public class Category extends EntityBase {

    @NotBlank
    @Size(min = 3, max = 50, message = "Název kategorie musí mít délku mezi 3 - 50 znaky.")
    private String name;

    private String url;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private Collection<Topic> topics;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "users_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private User user;

    @ManyToMany
    @JoinTable(
            name = "category_admin",
            joinColumns = @JoinColumn(
                    name = "category_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"))
    private Collection<User> categoryAdmins;


    public Category() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Collection<Topic> getTopics() {
        return topics;
    }

    public void setTopics(Collection<Topic> topics) {
        this.topics = topics;
    }

    public Collection<User> getCategoryAdmins() {
        return categoryAdmins;
    }

    public void setCategoryAdmins(Collection<User> categoryAdmins) {
        this.categoryAdmins = categoryAdmins;
    }
}
