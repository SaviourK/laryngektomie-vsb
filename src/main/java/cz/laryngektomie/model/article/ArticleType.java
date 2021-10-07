package cz.laryngektomie.model.article;

import cz.laryngektomie.model.EntityBase;

import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
public class ArticleType extends EntityBase {

    @NotBlank
    @Size(min = 3, max = 50, message = "Typ kategorie musí mít délku mezi 3 - 50 znaky.")
    private String name;

    public ArticleType() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
