package cz.laryngektomie.service.forum;


import cz.laryngektomie.model.forum.Category;
import cz.laryngektomie.model.forum.Post;
import cz.laryngektomie.model.forum.Topic;
import cz.laryngektomie.model.news.News;
import cz.laryngektomie.repository.forum.PostRepository;
import cz.laryngektomie.service.ServiceBase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;



@Service
public class PostService extends ServiceBase<Post> {


    private PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        super(postRepository);
        this.postRepository = postRepository;
    }

    public Page<Post> findByTopicId(Long topicId, Pageable pageable){
       return postRepository.findByTopicId(topicId, pageable);
    }

    public int getLastPageOfTopic(Long topicId){
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createDateTime").ascending());
        return  findByTopicId(topicId, pageable).getTotalPages();

    }

    public Page<Post> findAllSearch(int page, int itemsOnPage, String sortBy, boolean asc, String query) {
        Pageable paging;
        if(asc) {
            paging = PageRequest.of(page - 1, itemsOnPage, Sort.by(sortBy).ascending());
        }else {
            paging = PageRequest.of(page - 1, itemsOnPage, Sort.by(sortBy).descending());
        }


        return postRepository.findAllByTextContainingIgnoreCase(query, paging);


    }

}
