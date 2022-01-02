package cz.laryngektomie.repository.jpa.article;

import cz.laryngektomie.model.article.Article;
import cz.laryngektomie.repository.jpa.IRepositoryBase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends IRepositoryBase<Article> {

    List<Article> findFirst3ByOrderByCreateDateTimeDesc();

    Optional<Article> findByUrl(String url);

    Page<Article> findAllByTextContainingIgnoreCase(String name, Pageable pageable);

    Page<Article> findAllByNameOrTextOrArticleTypeNameContainingIgnoreCase(String name, String text, String articleTypeName, Pageable pageable);

    Page<Article> findAllByArticleTypeName(String name, Pageable pageable);

    Optional<Article> findFirstByArticleTypeNameOrderByCreateDateTimeDesc(String name);

    Optional<Article> findByName(String name);
}
