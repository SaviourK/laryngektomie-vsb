package cz.laryngektomie.model.feedback;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Feedback {
    @NotNull
    @Size(min = 3, max = 50, message = "Vaše jméno musí mít délku alespoň 3 znaky.")
    private String name;

    @NotNull
    @Email
    private String email;

    @NotNull
    @Size(min = 10, max = 500, message = "Zpráva musí byt v rozsahu od 10 do 500 znaků.")
    private String feedback;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
