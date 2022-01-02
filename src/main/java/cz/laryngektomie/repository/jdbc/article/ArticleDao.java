package cz.laryngektomie.repository.jdbc.article;

import cz.laryngektomie.helper.ForumHelper;
import cz.laryngektomie.model.article.Article;
import cz.laryngektomie.repository.jdbc.DaoBase;
import cz.laryngektomie.repository.jdbc.row_mapper.ArticleRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ArticleDao implements DaoBase<Article> {

    private final JdbcTemplate jdbcTemplate;
    private final ArticleRowMapper articleRowMapper;

    private static final String INSERT_QUERY = "" +
            "INSERT INTO article " +
            "(name, text, url ,article_type_id, user_id, create_date_time, update_date_time) " +
            "VALUES " +
            "(?, ?, ?, ?, ?, GETDATE(), GETDATE())";

    private static final String FIND_ALL_QUERY = "SELECT a.id AS a_id, a.create_date_time, a.update_date_time, a.name AS a_name, a.text, a.url, u.id AS u_id, u.username, at.id AS at_id, at.name AS at_name " +
            "FROM article a " +
            "JOIN users u ON a.user_id = u.id " +
            "JOIN article_type at ON a.article_type_id = at.id ";

    private static final String FIND_BY_ID_QUERY = FIND_ALL_QUERY + "WHERE a.id = ?";

    private static final String FIND_BY_URL_QUERY = FIND_ALL_QUERY + "WHERE url = ?";

    private static final String UPDATE_QUERY = "" +
            "UPDATE article " +
            "SET name = ?, text = ?, url = ?,article_type_id = ?, user_id = ?, update_date_time = GETDATE() " +
            "WHERE id = ?";

    private static final String DELETE_QUERY = "" +
            "DELETE FROM article " +
            "WHERE id = ?";

    public ArticleDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.articleRowMapper = new ArticleRowMapper();
    }

    @Override
    public void create(Article article) {
        article.setUrl(ForumHelper.makeFriendlyUrl(article.getName()));
        jdbcTemplate.update(INSERT_QUERY, article.getName(), article.getText(), article.getUrl(), article.getArticleType().getId(), article.getUser().getId());
    }

    @Override
    public List<Article> findAll() {
        return jdbcTemplate.query(FIND_ALL_QUERY, articleRowMapper);
    }

    @Override
    public Article findById(long id) {
        return jdbcTemplate.queryForObject(FIND_BY_ID_QUERY, articleRowMapper, id);
    }

    public Article findByUrl(String url) {
        return jdbcTemplate.queryForObject(FIND_BY_URL_QUERY, articleRowMapper, url);
    }

    @Override
    public void update(Article article) {
        article.setUrl(ForumHelper.makeFriendlyUrl(article.getName()));
        jdbcTemplate.update(UPDATE_QUERY, article.getName(), article.getText(), article.getUrl(), article.getArticleType().getId(), article.getUser().getId(), article.getId());
    }

    @Override
    public void delete(long id) {
        jdbcTemplate.update(DELETE_QUERY, id);
    }
}
