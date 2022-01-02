package cz.laryngektomie.repository.jpa.security;

import cz.laryngektomie.model.security.User;
import cz.laryngektomie.repository.jpa.IRepositoryBase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends IRepositoryBase<User> {

    User findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findTop3ByAboutUsTrueOrderByCreateDateTimeAsc();

    Optional<User> findByResetToken(String resetToken);

    Page<User> findAllByLastNameContainingIgnoreCase(String name, Pageable pageable);

    Page<User> findAllByFirstNameOrLastNameOrUsernameOrEmailContainingIgnoreCase(String firstname, String lastname, String username, String email, Pageable pageable);

    @Override
    void delete(User user);
}