package cz.laryngektomie.model.news;

import cz.laryngektomie.model.EntityBase;

import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
public class NewsType extends EntityBase {

    @NotBlank
    @Size(min = 3, max = 50, message = "Typ kategorie musí mít délku mezi 3 - 50 znaky.")
    private String name;

    public NewsType() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
