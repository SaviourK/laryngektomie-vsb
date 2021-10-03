package cz.laryngektomie.service.security;

import cz.laryngektomie.model.security.Privilege;
import cz.laryngektomie.repository.security.PrivilegeRepository;
import cz.laryngektomie.service.ServiceBase;
import org.springframework.stereotype.Service;

@Service
public class PrivilegeService extends ServiceBase<Privilege> {

    private final PrivilegeRepository privilegeRepository;

    public PrivilegeService(PrivilegeRepository privilegeRepository) {
        super(privilegeRepository);
        this.privilegeRepository = privilegeRepository;
    }
}
