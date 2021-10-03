package cz.laryngektomie.repository.news;

import cz.laryngektomie.model.news.News;
import cz.laryngektomie.repository.IRepositoryBase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NewsRepository extends IRepositoryBase<News> {

    List<News> findFirst3ByOrderByCreateDateTimeDesc();

    Optional<News> findByUrl(String url);

    Page<News> findAllByTextContainingIgnoreCase(String name, Pageable pageable);

    Page<News> findAllByNameOrTextOrNewsTypeNameContainingIgnoreCase(String name, String text, String newsTypeName, Pageable pageable);

    Page<News> findAllByNewsTypeName(String name, Pageable pageable);

    Optional<News> findFirstByNewsTypeNameOrderByCreateDateTimeDesc(String name);

    Optional<News> findByName(String name);
}
