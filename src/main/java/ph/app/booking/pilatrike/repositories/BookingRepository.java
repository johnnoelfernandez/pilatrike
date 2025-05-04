package ph.app.booking.pilatrike.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ph.app.booking.pilatrike.constant.BookingStatus;
import ph.app.booking.pilatrike.entities.Booking;
import ph.app.booking.pilatrike.entities.User;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    boolean existsByPassengerAndStatus(User passenger, BookingStatus status);
    boolean existsByDriverAndStatus(User driver, BookingStatus status);

    // --- history methods: exclude a status (e.g. CANCELED) ---
    List<Booking> findByPassengerAndStatusIsNot(User passenger, BookingStatus statusToExclude);
    List<Booking> findByDriverAndStatusIsNot(User driver, BookingStatus statusToExclude);

    // --- paginated history methods ---
    Page<Booking> findByPassengerAndStatusIsNot(User passenger, BookingStatus statusToExclude, Pageable pageable);
    Page<Booking> findByDriverAndStatusIsNot(User driver, BookingStatus statusToExclude, Pageable pageable);
}
