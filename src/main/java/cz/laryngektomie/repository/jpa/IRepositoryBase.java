package cz.laryngektomie.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface IRepositoryBase<T> extends JpaRepository<T, Long> {

}
