package cz.laryngektomie.service.forum;

import cz.laryngektomie.dto.forum.TopicOrPost;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
public class ForumService {

    private static final String TOP_10_POST_OR_TOPIC_QUERY = "SELECT TOP 3 \n" +
            "    together.topic_id,\n" +
            "\ttogether.topic_name,\n" +
            "\ttogether.create_date_time,\n" +
            "\ttogether.author,\n" +
            "\ttogether.text,\n" +
            "\ttogether.category_name\n" +
            "FROM (\n" +
            "\tSELECT tonly.id AS topic_id,\n" +
            "\ttonly.name AS topic_name,\n" +
            "\ttonly.create_date_time AS create_date_time,\n" +
            "\tu.username AS author,\n" +
            "\ttonly.text AS text,\n" +
            "\tc.name AS category_name\n" +
            "\tFROM topic tonly\n" +
            "\tJOIN users u ON tonly.user_id = u.id\n" +
            "\tJOIN category c ON tonly.category_id = c.id\n" +
            "\tWHERE tonly.id IN \n" +
            "\t\t(SELECT TOP 3 top_t.id \n" +
            "\t\tfrom topic top_t\n" +
            "\t\tORDER BY top_t.create_date_time DESC) \n" +
            "UNION\n" +
            "\tSELECT tpost.id AS topic_id,\n" +
            "\ttpost.name AS topic_name,\n" +
            "\tp.create_date_time AS create_date_time,\n" +
            "\tu.username AS author,\n" +
            "\tp.text AS text,\n" +
            "\tc.name AS category_name\n" +
            "\tFROM topic tpost\n" +
            "\tJOIN post p ON tpost.id = p.topic_id\n" +
            "\tJOIN users u ON p.user_id = u.id\n" +
            "\tJOIN category c ON tpost.category_id = c.id\n" +
            "\tWHERE p.id IN \n" +
            "\t\t(SELECT TOP 3 top_p.id\n" +
            "\t\tFROM post top_p \n" +
            "\t\tORDER BY top_p.create_date_time DESC)) as together";

    @PersistenceContext
    private EntityManager entityManager;

    public List<TopicOrPost> getTopicOrPostList() {
        Query nativeQuery = entityManager.createNativeQuery(TOP_10_POST_OR_TOPIC_QUERY);
        List<Object[]> res = nativeQuery.getResultList();
        List<TopicOrPost> topicOrPostList = new ArrayList<>();
        for (Object[] r : res) {
            topicOrPostList.add(new TopicOrPost(((BigInteger) r[0]).longValue(), (String) r[1], ((Timestamp) r[2]).toLocalDateTime(), (String) r[3], (String) r[4], (String) r[5]));
        }
        return topicOrPostList;
    }
}
