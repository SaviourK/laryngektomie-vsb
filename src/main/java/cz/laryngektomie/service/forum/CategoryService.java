package cz.laryngektomie.service.forum;

import cz.laryngektomie.helper.ForumHelper;
import cz.laryngektomie.model.forum.Category;
import cz.laryngektomie.repository.forum.CategoryRepository;
import cz.laryngektomie.service.ServiceBase;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoryService extends ServiceBase<Category> {

    private CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        super(categoryRepository);
        this.categoryRepository = categoryRepository;
    }

    public Optional<Category> findByName(String name){
        return categoryRepository.findByName(name);
    }

    public Optional<Category> findByUrl(String url) {
        return categoryRepository.findByUrl(url);
    }

    @Override
    public void saveOrUpdate(Category category) {
        category.setUrl(ForumHelper.makeFriendlyUrl(category.getName()));
        categoryRepository.save(category);

    }
}
