package cz.laryngektomie.repository.security;

import cz.laryngektomie.model.security.Privilege;
import cz.laryngektomie.repository.IRepositoryBase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrivilegeRepository extends IRepositoryBase<Privilege> {

    Privilege findByName(String name);

    @Override
    void delete(Privilege privilege);
}
