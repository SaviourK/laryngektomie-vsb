package cz.laryngektomie.service.news;

import cz.laryngektomie.model.forum.Category;
import cz.laryngektomie.model.news.NewsType;
import cz.laryngektomie.repository.IRepositoryBase;
import cz.laryngektomie.repository.news.NewsTypeRepository;
import cz.laryngektomie.service.ServiceBase;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NewsTypeService extends ServiceBase<NewsType> {

    private NewsTypeRepository newsTypeRepository;

    public NewsTypeService(NewsTypeRepository newsTypeRepository) {
        super(newsTypeRepository);
        this.newsTypeRepository = newsTypeRepository;
    }

    public Optional<NewsType> findByName(String name){
        return newsTypeRepository.findByName(name);
    }

}
