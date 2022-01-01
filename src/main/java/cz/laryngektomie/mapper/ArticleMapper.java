package cz.laryngektomie.mapper;

import cz.laryngektomie.dto.ArticleDto;
import cz.laryngektomie.model.article.Article;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

import static cz.laryngektomie.helper.Const.*;

@Mapper(componentModel = SPRING)
public interface ArticleMapper {


    @Mapping(source = ENTITY_DOT + CREATE_DATE_TIME, target = CREATE_DATE_TIME,
            dateFormat = MAPPER_DATE_TIME_FORMAT)
    @Mapping(source = ENTITY_DOT + UPDATE_DATE_TIME, target = UPDATE_DATE_TIME,
            dateFormat = MAPPER_DATE_TIME_FORMAT)
    @Mapping(source = ENTITY_DOT + ARTICLE_TYPE + DOT + ID, target = ARTICLE_TYPE_ID)
    @Mapping(source = ENTITY_DOT + USER + DOT + ID, target = USER_ID)
    ArticleDto entityToDto(Article entity);

    @Mapping(source = DTO_DOT + ARTICLE_TYPE_ID, target = ARTICLE_TYPE + DOT + ID)
    @Mapping(source = DTO_DOT + USER_ID, target = USER + DOT + ID)
    Article dtoToEntity(ArticleDto dto);

    List<ArticleDto> entityListToDtoList(List<Article> employees);
}
