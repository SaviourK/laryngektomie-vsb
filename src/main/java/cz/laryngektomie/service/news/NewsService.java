package cz.laryngektomie.service.news;

import cz.laryngektomie.helper.ForumHelper;
import cz.laryngektomie.model.forum.Category;
import cz.laryngektomie.model.news.News;
import cz.laryngektomie.model.security.User;
import cz.laryngektomie.repository.news.NewsRepository;
import cz.laryngektomie.service.ServiceBase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NewsService extends ServiceBase<News> {

    private NewsRepository newsRepository;

    public NewsService(NewsRepository newsRepository) {
        super(newsRepository);
        this.newsRepository = newsRepository;
    }

    @Override
    public void saveOrUpdate(News news) {
        news.setUrl(ForumHelper.makeFriendlyUrl(news.getName()));
        newsRepository.save(news);

    }

    public List<News> findFirst3ByOrderByCreateDateTimeDesc(){
        return newsRepository.findFirst3ByOrderByCreateDateTimeDesc();
    }

    public Optional<News> findByUrl(String url) {
        return newsRepository.findByUrl(url);
    }


    public Page<News> findAllSearch(int page, int itemsOnPage, String sortBy, boolean asc, String query) {
        Pageable paging;
        if(asc) {
            paging = PageRequest.of(page - 1, itemsOnPage, Sort.by(sortBy).ascending());
        }else {
            paging = PageRequest.of(page - 1, itemsOnPage, Sort.by(sortBy).descending());
        }


        return newsRepository.findAllByNameOrTextOrNewsTypeNameContainingIgnoreCase(query, query, query, paging);


    }

    public Optional<News> findLastNewsletter() {
        return newsRepository.findFirstByNewsTypeNameOrderByCreateDateTimeDesc("zpravodaj");
    }

    public Optional<News> findByName(String name) {
        return newsRepository.findByName(name);
    }
}
