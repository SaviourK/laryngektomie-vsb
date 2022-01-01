package cz.laryngektomie.service.article;

import cz.laryngektomie.helper.Const;
import cz.laryngektomie.helper.ForumHelper;
import cz.laryngektomie.model.article.Article;
import cz.laryngektomie.repository.article.ArticleRepository;
import cz.laryngektomie.service.ServiceBase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ArticleService extends ServiceBase<Article> {

    private final ArticleRepository articleRepository;

    public ArticleService(ArticleRepository articleRepository) {
        super(articleRepository);
        this.articleRepository = articleRepository;
    }

    @Override
    public Article saveOrUpdate(Article article) {
        article.setUrl(ForumHelper.makeFriendlyUrl(article.getName()));
        return articleRepository.save(article);
    }

    public List<Article> findFirst3ByOrderByCreateDateTimeDesc() {
        return articleRepository.findFirst3ByOrderByCreateDateTimeDesc();
    }

    public Optional<Article> findByUrl(String url) {
        return articleRepository.findByUrl(url);
    }

    public Page<Article> findAllSearch(int page, int itemsOnPage, String sortBy, boolean asc, String query) {
        Pageable paging;
        if (asc) {
            paging = PageRequest.of(page - 1, itemsOnPage, Sort.by(sortBy).ascending());
        } else {
            paging = PageRequest.of(page - 1, itemsOnPage, Sort.by(sortBy).descending());
        }
        return articleRepository.findAllByNameOrTextOrArticleTypeNameContainingIgnoreCase(query, query, query, paging);
    }

    public Optional<Article> findLastNewsletter() {
        return articleRepository.findFirstByArticleTypeNameOrderByCreateDateTimeDesc(Const.ARTICLE_TYPE_NEWSLETTER);
    }

    public Optional<Article> findByName(String name) {
        return articleRepository.findByName(name);
    }
}
