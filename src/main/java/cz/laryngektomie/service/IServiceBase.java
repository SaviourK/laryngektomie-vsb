package cz.laryngektomie.service;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface IServiceBase<T> {

    Optional<T> findById(long id);

    List<T> findAll();

    Page<T> findAll(int page, int itemsOnPage, String sortBy, boolean asc);

    T saveOrUpdate(T t);

    void delete(T t);

    void deleteById(long id);
}
