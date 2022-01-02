package cz.laryngektomie.repository.jdbc.row_mapper;

import cz.laryngektomie.model.article.Article;
import cz.laryngektomie.model.article.ArticleType;
import cz.laryngektomie.model.security.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class ArticleRowMapper implements RowMapper<Article> {

    @Override
    public Article mapRow(ResultSet resultSet, int i) throws SQLException {
        Article article = new Article();
        article.setId(resultSet.getLong("a_id"));
        article.setCreateDateTime(resultSet.getObject("create_date_time", LocalDateTime.class));
        article.setUpdateDateTime(resultSet.getObject("update_date_time", LocalDateTime.class));
        article.setName(resultSet.getString("a_name"));
        article.setText(resultSet.getString("text"));
        article.setUrl(resultSet.getString("url"));

        User user = new User();
        user.setId(resultSet.getLong("u_id"));
        user.setUsername(resultSet.getString("username"));
        article.setUser(user);

        ArticleType articleType = new ArticleType();
        articleType.setId(resultSet.getLong("at_id"));
        articleType.setName(resultSet.getString("at_name"));
        article.setArticleType(articleType);

        return article;
    }
}
