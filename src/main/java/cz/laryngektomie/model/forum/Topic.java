package cz.laryngektomie.model.forum;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.laryngektomie.helper.ForumHelper;
import cz.laryngektomie.model.EntityBase;
import cz.laryngektomie.model.security.User;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Collection;

@Entity
public class Topic extends EntityBase {

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    @NotBlank
    @Size(min = 2, max = 30, message = "Název musí mít délku 2-30 znaků.")
    private String name;

    @NotBlank
    @Size(min = 5, max = 1000, message = "Text musí mít délku 5-1000 znaků.")
    @Column(length = 1500)
    private String text;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "users_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL)
    private Collection<Post> posts;

    public Topic() {
        super();
    }

    public Topic(String name, String text, User user, Category category) {
        super();
        this.name = name;
        this.text = text;
        this.user = user;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Collection<Post> getPosts() {
        return posts;
    }

    public void setPosts(Collection<Post> posts) {
        this.posts = posts;
    }

    public String getDescription() {
        return ForumHelper.getDescription(this.text, 25);
    }
}
