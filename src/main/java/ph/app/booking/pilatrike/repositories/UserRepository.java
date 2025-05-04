package ph.app.booking.pilatrike.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ph.app.booking.pilatrike.entities.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByName(String name);
    Optional<User> findByContactNumber(String contactNumber);
    boolean existsByContactNumber(String contactNumber);
}
