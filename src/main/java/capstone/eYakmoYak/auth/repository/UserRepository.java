package capstone.eYakmoYak.auth.repository;

import capstone.eYakmoYak.auth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
}
