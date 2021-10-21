package cz.laryngektomie;

import com.github.javafaker.Faker;
import cz.laryngektomie.model.forum.Post;
import cz.laryngektomie.model.forum.Topic;
import cz.laryngektomie.model.security.User;
import cz.laryngektomie.service.forum.CategoryService;
import cz.laryngektomie.service.forum.TopicService;
import cz.laryngektomie.service.security.UserService;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceUnit;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Random;

@Component
public class CreateDataJob {

    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    private final CategoryService categoryService;
    private final UserService userService;
    private final TopicService topicService;

    public CreateDataJob(CategoryService categoryService, UserService userService, TopicService topicService) {
        this.categoryService = categoryService;
        this.userService = userService;
        this.topicService = topicService;
    }

    public void run() {
        bulkInsert();
    }

    public void bulkInsert() {
        EntityManager em = entityManagerFactory.createEntityManager();
        Session session = em.unwrap(Session.class);
        //Timestamp startTime = new Timestamp(System.currentTimeMillis());
        int records = 5000000;
        long start = System.currentTimeMillis();
        session.doWork(connection -> {
            int totalInserted = 0;
            connection.setAutoCommit(false);
            /*String compiledQuery = "INSERT INTO category(id, name, url, users_id)" +
                    " VALUES" + "(?, ?, ?, ?)";*/
            String compiledQuery = "INSERT INTO article_type(id, name)" +
                    " VALUES" + "(?, ?)";
            String articleType = "article_type";
            PreparedStatement preparedStatement = connection.prepareStatement(compiledQuery);
            for (long i = 100; i < records; i++) {
                preparedStatement.setLong(1, i);
                preparedStatement.setString(2, articleType+i);
                //preparedStatement.setTimestamp(2, startTime);
                //preparedStatement.setTimestamp(3, startTime);
                /*preparedStatement.setString(2, "name" + i);
                preparedStatement.setString(3, "name" + i);
                preparedStatement.setLong(4, 1L);*/
                preparedStatement.addBatch();
                if (i % 10000 == 0) {
                    totalInserted += preparedStatement.executeBatch().length;
                    connection.commit();
                    long end = System.currentTimeMillis();
                    System.out.println("total time taken = " + (end - start)/1000 + " s" + " total inserted " + totalInserted + " i =" + i);
                }
            }

            //long start = System.currentTimeMillis();
            int[] inserted = preparedStatement.executeBatch();
            long end = System.currentTimeMillis();

            System.out.println("total time taken to insert the batch = " + (end - start)/1000 + "s");

            preparedStatement.close();
            connection.close();

        });
    }

    private void fakeData() {

        EntityManager em = entityManagerFactory.createEntityManager();


        Faker faker = new Faker();
        int milion = 1000000;
        Instant starts = Instant.now();
        int count = 0;


        /*List<Category> categories = categoryService.findAll();
        int catSize = categories.size();*/
        List<User> userList = userService.findAll();
        int userSize = userList.size();

        List<Topic> topicList = topicService.findAll();
        int topicSize = topicList.size();

        Random random = new Random();
        em.setFlushMode(FlushModeType.COMMIT);
        em.getTransaction().begin();
        for (int i = 0; i < milion; i++) {
            em.persist(new Post(faker.book().title(), userList.get(random.nextInt(userSize)), topicList.get(random.nextInt(topicSize))));
            count++;
            if (count % 1000 == 0) {
                em.getTransaction().commit();
                System.out.println("Total: " + count);
                em.getTransaction().begin();
            }
        }
        em.getTransaction().commit();
        Instant ends = Instant.now();
        System.out.println(Duration.between(starts, ends));

/*

        for (int i = 0; i < 100; i++) {
            topicService.saveOrUpdate(new Topic(faker.commerce().productName(), faker.commerce().productName(), userList.get(random.nextInt(userSize)), categories.get(random.nextInt(catSize))));
        }
        List<Topic> topicList = topicService.findAll();
        int topicSize = topicList.size();
        for (int i = 0; i < 1000; i++) {
            postService.saveOrUpdate(new Post(faker.book().title(), userList.get(random.nextInt(userSize)), topicList.get(random.nextInt(topicSize))));
        }*/
    }
}
