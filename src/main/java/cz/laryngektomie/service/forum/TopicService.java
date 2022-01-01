package cz.laryngektomie.service.forum;

import cz.laryngektomie.model.forum.Topic;
import cz.laryngektomie.repository.forum.TopicRepository;
import cz.laryngektomie.service.ServiceBase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TopicService extends ServiceBase<Topic> {

    private final TopicRepository topicRepository;

    public TopicService(TopicRepository topicRepository) {
        super(topicRepository);
        this.topicRepository = topicRepository;
    }

    public Optional<Topic> findByName(String name) {
        return topicRepository.findByName(name);
    }

    public List<Topic> findLatest() {
        return topicRepository.findFirst5ByOrderByIdDesc();
    }

    public Page<Topic> findByCategoryId(Long categoryId, Pageable pageable) {
        return topicRepository.findByCategoryId(categoryId, pageable);
    }

    public Page<Topic> findAllSearch(int page, int itemsOnPage, String sortBy, boolean asc, String query) {
        Pageable paging;
        if (asc) {
            paging = PageRequest.of(page - 1, itemsOnPage, Sort.by(sortBy).ascending());
        } else {
            paging = PageRequest.of(page - 1, itemsOnPage, Sort.by(sortBy).descending());
        }
        return topicRepository.findAllByNameOrTextContainingIgnoreCase(query, query, paging);
    }
}
