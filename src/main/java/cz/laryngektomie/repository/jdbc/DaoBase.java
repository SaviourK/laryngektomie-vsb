package cz.laryngektomie.repository.jdbc;

import java.util.List;

public interface DaoBase<T> {

    void create(T entity);

    List<T> findAll();

    T findById(long id);

    void update(T entity);

    void delete(long id);
}
