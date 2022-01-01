package cz.laryngektomie.controller.rest;

import cz.laryngektomie.dto.ApiResponse;
import cz.laryngektomie.dto.ArticleDto;
import cz.laryngektomie.mapper.ArticleMapper;
import cz.laryngektomie.model.article.Article;
import cz.laryngektomie.service.article.ArticleService;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static cz.laryngektomie.helper.UrlConst.*;

@Api
@RestController
@RequestMapping(API_URL + ARTICLES_URL)
public class ArticleRestController {

    private static final Logger logger = LoggerFactory.getLogger(ArticleRestController.class);

    private final ArticleService articleService;
    private final ArticleMapper articleMapper;

    public ArticleRestController(ArticleService articleService, ArticleMapper articleMapper) {
        this.articleService = articleService;
        this.articleMapper = articleMapper;
    }

    @GetMapping
    public List<ArticleDto> getAll() {
        return articleMapper.entityListToDtoList(articleService.findAll());
    }

    @PostMapping()
    public ResponseEntity<ArticleDto> create(@RequestBody ArticleDto articleDto) {
        try {
            final Article article = articleService.saveOrUpdate(articleMapper.dtoToEntity(articleDto));
            return ResponseEntity.ok(articleMapper.entityToDto(article));
        } catch (Exception e) {
            return resolveException(e);
        }
    }

    @GetMapping(ID_PATH_VAR)
    public ResponseEntity<ArticleDto> findById(@PathVariable long id) {
        try {
            Article article = articleService.findById(id).orElse(null);
            if (article == null) {
                return resolveNotFound(id);
            }
            return ResponseEntity.ok(articleMapper.entityToDto(article));
        } catch (Exception e) {
            return resolveException(e);
        }
    }

    @PutMapping(ID_PATH_VAR)
    public ResponseEntity<ArticleDto> update(@RequestBody ArticleDto articleDto) {
        try {
            Optional<Article> articleOptional = articleService.findById(articleDto.getId());
            if (articleOptional.isPresent()) {
                Article article = articleMapper.dtoToEntity(articleDto);
                article = articleService.saveOrUpdate(article);
                return ResponseEntity.ok(articleMapper.entityToDto(article));
            } else {
                return resolveNotFound(articleDto.getId());
            }
        } catch (Exception e) {
            return resolveException(e);
        }
    }

    @DeleteMapping(ID_PATH_VAR)
    public ResponseEntity<ApiResponse> delete(@PathVariable long id) {
        try {
            Optional<Article> articleOptional = articleService.findById(id);
            if (articleOptional.isPresent()) {
                articleService.delete(articleOptional.get());
                return ResponseEntity.ok(new ApiResponse(Boolean.TRUE, "Article deleted with id " + id));
            } else {
                return resolveNotFound(id);
            }
        } catch (Exception e) {
            return resolveException(e);
        }
    }

    private <T> ResponseEntity<T> resolveNotFound(long id) {
        logger.info("Article with id {} not found", id);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    private <T> ResponseEntity<T> resolveException(Exception e) {
        logger.error(e.getMessage(), e);
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
