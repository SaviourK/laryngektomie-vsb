package cz.laryngektomie.repository.forum;

import cz.laryngektomie.model.forum.Topic;
import cz.laryngektomie.repository.IRepositoryBase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TopicRepository extends IRepositoryBase<Topic> {

    Optional<Topic> findByName(String name);

    List<Topic> findAllByOrderByCreateDateTimeDesc();

    Page<Topic> findByCategoryId(Long id, Pageable pageable);

    Page<Topic> findAllBy(Pageable pageable);

    Page<Topic> findAllByNameOrTextContainingIgnoreCase(String name, String text, Pageable pageable);

    List<Topic> findFirst5ByOrderByIdDesc();
}