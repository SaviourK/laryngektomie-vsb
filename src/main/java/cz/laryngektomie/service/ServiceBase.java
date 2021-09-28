package cz.laryngektomie.service;

import cz.laryngektomie.repository.IRepositoryBase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public class ServiceBase<T> implements IServiceBase<T> {

    private IRepositoryBase<T> repository;

    public ServiceBase(IRepositoryBase<T> repository) {
        this.repository = repository;
    }


    @Override
    public Optional<T> findById(long id) {
        return repository.findById(id);

    }

    @Override
    public List<T> findAll() {
        return repository.findAll();
    }

    @Override
    public Page<T> findAll(int page, int itemsOnPage, String sortBy, boolean asc) {
        Pageable paging;
        if(asc) {
            paging = PageRequest.of(page - 1, itemsOnPage, Sort.by(sortBy).ascending());
        }else {
            paging = PageRequest.of(page - 1, itemsOnPage, Sort.by(sortBy).descending());
        }


        return repository.findAll(paging);


    }




    @Override
    public void saveOrUpdate(T t) {
        repository.save(t);
    }



    @Override
    public void delete(T t) {
        repository.delete(t);
    }
}
