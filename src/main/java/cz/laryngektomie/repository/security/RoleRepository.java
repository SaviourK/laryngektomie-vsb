package cz.laryngektomie.repository.security;

import cz.laryngektomie.model.security.Role;
import cz.laryngektomie.repository.IRepositoryBase;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends IRepositoryBase<Role> {

    Role findByName(String name);

    @Override
    void delete(Role role);
}
