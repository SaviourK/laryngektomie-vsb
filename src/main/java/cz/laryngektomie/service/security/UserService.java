package cz.laryngektomie.service.security;

import cz.laryngektomie.model.security.User;
import cz.laryngektomie.repository.security.UserRepository;
import cz.laryngektomie.service.ServiceBase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService extends ServiceBase<User> {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder encoder) {
        super(userRepository);
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    public List<User> findByAboutUsTrue() {
        //return userRepository.findByAboutUsTrueOrderByCreateDateTimeAsc();
        return userRepository.findAll();
    }

    public Optional<User> findUserByResetToken(String resetToken) {
        return userRepository.findByResetToken(resetToken);
    }

    public String encode(String password) {
        return encoder.encode(password);
    }

    public Page<User> findAllSearch(int page, int itemsOnPage, String sortBy, boolean asc, String query) {
        Pageable paging;
        if (asc) {
            paging = PageRequest.of(page - 1, itemsOnPage, Sort.by(sortBy).ascending());
        } else {
            paging = PageRequest.of(page - 1, itemsOnPage, Sort.by(sortBy).descending());
        }
        return userRepository.findAllByFirstNameOrLastNameOrUsernameOrEmailContainingIgnoreCase(query, query, query, query, paging);
    }

    public boolean matchPassword(String newPassword, String oldPassword) {
        return encoder.matches(newPassword, oldPassword);
    }
}
