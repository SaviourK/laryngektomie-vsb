package cz.laryngektomie.model.security;

import cz.laryngektomie.model.EntityBase;
import cz.laryngektomie.model.forum.Post;
import cz.laryngektomie.model.forum.Topic;
import cz.laryngektomie.model.news.Image;

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
    private String username;

    @NotBlank
    @Size(min = 3, max = 500, message = "Heslo musí mít délku mezi minimálně 3 znaky.")
    private String password;

    private String matchingPassword;

    @NotBlank
    @Size(min = 2, max = 50, message = "Jméno musí mít délku mezi 2 - 50 znaky.")
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 50, message = "Příjmení musí mít délku mezi 2 - 50 znaky.")
    private String lastName;

    @NotBlank
    @Email(message = "Špatně zadaný email")
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

    private String role;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id", referencedColumnName = "id")
    private Image image;

    @OneToMany
    @JoinColumn(name = "users_id")
    private Collection<Topic> topics;

    @OneToMany
    @JoinColumn(name = "users_id")
    private Collection<Post> posts;

    private int postCount;

    private int newsCount;

    public User() {
        super();
        this.enabled = true;
        this.tokenExpired = false;
        this.postCount = 0;
        this.newsCount = 0;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
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

    public int getNewsCount() {
        return newsCount;
    }

    public void setNewsCount(int newsCount) {
        this.newsCount = newsCount;
    }

    @Override
    public String toString() {
        return username;
    }
}
