package cz.laryngektomie.repository.jpa.forum;


import cz.laryngektomie.model.forum.Category;
import cz.laryngektomie.repository.jpa.IRepositoryBase;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends IRepositoryBase<Category> {

    Optional<Category> findByName(String name);

    Optional<Category> findByUrl(String url);
}
