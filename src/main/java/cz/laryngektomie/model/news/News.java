package cz.laryngektomie.model.news;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.laryngektomie.helper.ForumHelper;
import cz.laryngektomie.model.EntityBase;
import cz.laryngektomie.model.security.User;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Collection;

@Entity
public class News extends EntityBase {

    @NotBlank
    @Size(min = 3, max = 100, message = "Délka mezi 3 - 100 znaky")
    private String name;

    @Size(max = 150)
    private String url;

    @NotBlank
    @Column(columnDefinition = "varchar(MAX)")
    private String text;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "users_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private User user;

    @OneToMany(cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.IGNORE)
    private Collection<Image> images;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "news_type_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private NewsType newsType;

    public News() {
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

    public Collection<Image> getImages() {
        return images;
    }

    public void setImages(Collection<Image> images) {
        this.images = images;
    }

    public NewsType getNewsType() {
        return newsType;
    }

    public void setNewsType(NewsType newsType) {
        this.newsType = newsType;
    }

    public String getDescriptionText() {
        return ForumHelper.getDescription(this.text, 200);
    }
}
