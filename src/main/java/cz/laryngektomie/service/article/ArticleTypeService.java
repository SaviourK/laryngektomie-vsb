package cz.laryngektomie.service.article;

import cz.laryngektomie.model.article.ArticleType;
import cz.laryngektomie.repository.article.ArticleTypeRepository;
import cz.laryngektomie.service.ServiceBase;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ArticleTypeService extends ServiceBase<ArticleType> {

    private final ArticleTypeRepository articleTypeRepository;

    public ArticleTypeService(ArticleTypeRepository articleTypeRepository) {
        super(articleTypeRepository);
        this.articleTypeRepository = articleTypeRepository;
    }

    public Optional<ArticleType> findByName(String name) {
        return articleTypeRepository.findByName(name);
    }
}
