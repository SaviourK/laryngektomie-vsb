package cz.laryngektomie.model.forum;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.laryngektomie.helper.ForumHelper;
import cz.laryngektomie.model.EntityBase;
import cz.laryngektomie.model.security.User;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class Post extends EntityBase {


    @NotBlank
    @Size(min = 5, max = 1000, message = "Příspěvek musí mít délku 5-1000 znaků.")
    @Column(length = 1500)
    private String text;


    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "users_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private User user;


    @ManyToOne(fetch = FetchType.LAZY)
    private Topic topic;


    public Post() {
        super();
    }

    public Post(String text, User user, Topic topic) {
        super();
        this.text = text;
        this.user = user;
        this.topic = topic;
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

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }


    public String getDescription() {
        return ForumHelper.getDescription(this.text, 25);
    }
}
