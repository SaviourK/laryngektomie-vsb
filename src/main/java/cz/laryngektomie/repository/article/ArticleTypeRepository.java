package cz.laryngektomie.repository.article;

import cz.laryngektomie.model.article.ArticleType;
import cz.laryngektomie.repository.IRepositoryBase;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArticleTypeRepository extends IRepositoryBase<ArticleType> {

    Optional<ArticleType> findByName(String name);
}
