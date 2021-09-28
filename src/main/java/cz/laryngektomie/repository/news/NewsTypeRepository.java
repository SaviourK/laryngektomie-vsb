package cz.laryngektomie.repository.news;

import cz.laryngektomie.model.news.NewsType;
import cz.laryngektomie.repository.IRepositoryBase;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NewsTypeRepository extends IRepositoryBase<NewsType> {


    Optional<NewsType> findByName(String name);
}
