package cz.laryngektomie.repository.forum;

import cz.laryngektomie.model.forum.Post;
import cz.laryngektomie.model.news.News;
import cz.laryngektomie.repository.IRepositoryBase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PostRepository extends IRepositoryBase<Post> {

    Page<Post> findAllByTopicId(long id, Pageable pageable);


    List<Post> findByTopicId(long id);

    Page<Post> findByTopicId(long id, Pageable pageable);

    Page<Post> findAllByTextContainingIgnoreCase(String name, Pageable pageable);

}
