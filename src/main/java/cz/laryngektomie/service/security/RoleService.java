package cz.laryngektomie.service.security;

import cz.laryngektomie.model.security.Role;
import cz.laryngektomie.repository.security.RoleRepository;
import cz.laryngektomie.service.ServiceBase;
import org.springframework.stereotype.Service;

@Service
public class RoleService extends ServiceBase<Role> {

    private RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        super(roleRepository);
        this.roleRepository = roleRepository;
    }

    public Role findByName(String name){
        return roleRepository.findByName(name);
    }
}
