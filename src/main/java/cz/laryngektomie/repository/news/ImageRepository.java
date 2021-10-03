package cz.laryngektomie.repository.news;

import cz.laryngektomie.model.news.Image;
import cz.laryngektomie.repository.IRepositoryBase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ImageRepository extends IRepositoryBase<Image> {

    List<Image> findFirst3ByOrderByCreateDateTimeDesc();

    Page<Image> findAllBy(Pageable pageable);
}
