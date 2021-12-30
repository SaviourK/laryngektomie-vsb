package cz.laryngektomie.model.security;

import cz.laryngektomie.model.EntityBase;
import cz.laryngektomie.model.article.Image;
import cz.laryngektomie.model.forum.Post;
import cz.laryngektomie.model.forum.Topic;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;

@Entity
@Table(name = "users")
public class User extends EntityBase {

    @NotBlank
    @Size(min = 3, max = 50, message = "Přihlašovací jméno musí mít délku mezi 3 - 50 znaky.")
    @Column(unique = true)
    @NotNull
    private String username;

    @NotBlank
    @Size(min = 3, max = 500, message = "Heslo musí mít délku mezi minimálně 3 znaky.")
    @NotNull
    private String password;

    @Transient
    private String matchingPassword;

    @NotBlank
    @Size(min = 2, max = 50, message = "Jméno musí mít délku mezi 2 - 50 znaky.")
    @NotNull
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 50, message = "Příjmení musí mít délku mezi 2 - 50 znaky.")
    @NotNull
    private String lastName;

    @NotBlank
    @Email(message = "Špatně zadaný email")
    @Column(unique = true)
    @NotNull
    private String email;

    @NotNull
    private boolean enabled;

    @NotNull
    private boolean tokenExpired;

    private String resetToken;

    @NotNull
    private boolean aboutUs;

    @Size(max = 1500, message = "Maximální delka je 1500 znaků")
    private String aboutMe;

    @NotNull
    private UserRole role;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id", referencedColumnName = "id")
    private Image image;

    @OneToMany
    @JoinColumn(name = "user_id")
    private Collection<Topic> topics;

    @OneToMany
    @JoinColumn(name = "user_id")
    private Collection<Post> posts;

    private int topicCount;

    private int postCount;

    private int articleCount;

    public User() {
        super();
        this.enabled = true;
        this.tokenExpired = false;
        this.topicCount = 0;
        this.postCount = 0;
        this.articleCount = 0;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMatchingPassword() {
        return matchingPassword;
    }

    public void setMatchingPassword(String matchingPassword) {
        this.matchingPassword = matchingPassword;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isTokenExpired() {
        return tokenExpired;
    }

    public void setTokenExpired(boolean tokenExpired) {
        this.tokenExpired = tokenExpired;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public boolean isAboutUs() {
        return aboutUs;
    }

    public void setAboutUs(boolean aboutUs) {
        this.aboutUs = aboutUs;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Collection<Topic> getTopics() {
        return topics;
    }

    public void setTopics(Collection<Topic> topics) {
        this.topics = topics;
    }

    public Collection<Post> getPosts() {
        return posts;
    }

    public void setPosts(Collection<Post> posts) {
        this.posts = posts;
    }

    public int getPostCount() {
        return postCount;
    }

    public void setPostCount(int postCount) {
        this.postCount = postCount;
    }

    public int getArticleCount() {
        return articleCount;
    }

    public void setArticleCount(int articleCount) {
        this.articleCount = articleCount;
    }

    public int getTopicCount() {
        return topicCount;
    }

    public void setTopicCount(int topicCount) {
        this.topicCount = topicCount;
    }

    @Override
    public String toString() {
        return username;
    }
}
