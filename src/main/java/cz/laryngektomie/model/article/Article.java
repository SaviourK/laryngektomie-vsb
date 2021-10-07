package cz.laryngektomie.model.article;

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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;

@Entity
public class Article extends EntityBase {

    @NotBlank
    @Size(min = 3, max = 100, message = "Délka mezi 3 - 100 znaky")
    @Column(unique = true)
    @NotNull
    private String name;

    @Size(max = 150)
    @Column(unique = true)
    @NotNull
    private String url;

    @NotBlank
    @Column(columnDefinition = "varchar(MAX)")
    @NotNull
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
    @JoinColumn(name = "article_type_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private ArticleType articleType;

    public Article() {
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

    public ArticleType getArticleType() {
        return articleType;
    }

    public void setArticleType(ArticleType articleType) {
        this.articleType = articleType;
    }

    public String getDescriptionText() {
        return ForumHelper.getDescription(this.text, 200);
    }
}
