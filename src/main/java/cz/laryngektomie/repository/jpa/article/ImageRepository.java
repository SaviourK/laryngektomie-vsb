package cz.laryngektomie.repository.jpa.article;

import cz.laryngektomie.model.article.Image;
import cz.laryngektomie.repository.jpa.IRepositoryBase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ImageRepository extends IRepositoryBase<Image> {

    List<Image> findFirst3ByOrderByCreateDateTimeDesc();

    Page<Image> findAllBy(Pageable pageable);
}
