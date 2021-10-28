package cz.laryngektomie.service.forum;

import cz.laryngektomie.model.dto.TopicOrPost;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ForumService {

    @PersistenceContext
    private EntityManager entityManager;

    public List<TopicOrPost> getTopicOrPostList() {
        Query nativeQuery = entityManager.createNativeQuery("" +
                "SET rowCount 10\n" +
                "SELECT tonly.id AS topic_id,\n" +
                "       tonly.name AS topic_name,\n" +
                "       tonly.create_date_time AS created_date_time,\n" +
                "       u.username AS author,\n" +
                "       tonly.text AS text,\n" +
                "       c.name AS category_name\n" +
                "FROM topic tonly\n" +
                "JOIN users u ON tonly.user_id = u.id\n" +
                "JOIN category c ON tonly.category_id = c.id\n" +
                "UNION\n" +
                "SELECT tpost.id AS topic_id,\n" +
                "       tpost.name AS topic_name,\n" +
                "       p.create_date_time AS created_date_time,\n" +
                "       u.username AS author,\n" +
                "       p.text AS text,\n" +
                "       c.name AS category_name\n" +
                "FROM topic tpost\n" +
                "JOIN post p ON tpost.id = p.topic_id\n" +
                "JOIN users u ON p.user_id = u.id\n" +
                "JOIN category c ON tpost.category_id = c.id\n" +
                "ORDER BY created_date_time DESC");
        List<Object[]> res = nativeQuery.getResultList();
        List<TopicOrPost> topicOrPostList = new ArrayList<>();
        for (Object[] r : res) {
            topicOrPostList.add(new TopicOrPost(((BigInteger) r[0]).longValue(), (String) r[1], ((Timestamp) r[2]).toLocalDateTime(), (String) r[3], (String)r[4], (String) r[5]));
        }
        return topicOrPostList;
    }
}
