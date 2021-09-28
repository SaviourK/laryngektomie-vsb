package cz.laryngektomie.model.security;

import cz.laryngektomie.model.EntityBase;
import cz.laryngektomie.model.forum.Post;
import cz.laryngektomie.model.news.Image;
import cz.laryngektomie.model.forum.Topic;


import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
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


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "users_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id", referencedColumnName = "id")
    private Image image;


    @OneToMany
    @JoinColumn(name = "users_id")
    private Collection<Topic> topics;

    @OneToMany
    @JoinColumn(name = "users_id")
    private Collection<Post> posts;


    public User() {
        super();
        this.enabled = true;
        this.tokenExpired = false;

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

    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
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

    //Methods
    @Override
    public String toString() {
        return username;
    }

    public String rolesString() {
        StringBuilder rolesString = new StringBuilder();
        for (Role r : roles) {
            rolesString.append(r.getNameCZ());
        }
        return rolesString.toString().trim();
    }

    public long getFirstRoleId() {
        return roles.iterator().next().getId();

    }

    public String getRole(){
        for (Role r : roles) {
            return r.getNameCZ();
        }
        return "Anonym";
    }


    public void addRole(Role role) {
        if (roles == null) {
            roles = new ArrayList<>();
        }
        roles.add(role);

    }
}
