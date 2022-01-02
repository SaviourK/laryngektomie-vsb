package cz.laryngektomie.repository.jpa.forum;

import cz.laryngektomie.model.forum.Post;
import cz.laryngektomie.repository.jpa.IRepositoryBase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends IRepositoryBase<Post> {

    Page<Post> findAllByTopicId(long id, Pageable pageable);

    //List<Post> findByTopicId(long id);

    Page<Post> findByTopicId(long id, Pageable pageable);

    Page<Post> findAllByTextContainingIgnoreCase(String name, Pageable pageable);
}
