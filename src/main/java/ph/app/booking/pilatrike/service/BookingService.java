package ph.app.booking.pilatrike.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ph.app.booking.pilatrike.constant.BookingStatus;
import ph.app.booking.pilatrike.constant.UserType;
import ph.app.booking.pilatrike.dto.BookingDto;
import ph.app.booking.pilatrike.entities.Booking;
import ph.app.booking.pilatrike.entities.User;
import ph.app.booking.pilatrike.exception.BookingException;
import ph.app.booking.pilatrike.repositories.BookingRepository;
import ph.app.booking.pilatrike.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;

    public Booking createBooking(BookingDto booking) {
        User passenger = userRepository
                .findById(booking.getPassengerId())
                .orElseThrow(() -> new BookingException("Passenger not found."));

        if(passenger.getType() == UserType.DRIVER)
            throw new BookingException("Driver cannot book a ride.");

        boolean hasActiveBooking = bookingRepository.existsByPassengerAndStatus(passenger, BookingStatus.PENDING);
        if(hasActiveBooking)
            throw new BookingException("Passenger already has a pending booking.");
        Booking newBooking = Booking.builder()
                .passenger(passenger)
                .status(BookingStatus.PENDING)
                .pickupLocation(booking.getPickupLocation())
                .dropoffLocation(booking.getDropoffLocation())
                .createdAt(LocalDateTime.now())
                .build();

        return bookingRepository.save(newBooking);
    }

    public Booking acceptBooking(Long id, Long driverId) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingException("No booking found with id: " + id));

        if (booking.getStatus() != BookingStatus.PENDING)
            throw new BookingException("Booking is not pending.");

        User driver = userRepository.findById(driverId)
                .orElseThrow(() -> new BookingException("Driver not found with id: " + driverId));

        if (driver.getType() != UserType.DRIVER)
            throw new BookingException("Only drivers can accept a booking.");

        boolean hasActiveBooking = bookingRepository.existsByDriverAndStatus(driver, BookingStatus.ACCEPTED);
        if (hasActiveBooking)
            throw new BookingException("Driver already has an accepted booking.");


        booking.setDriver(driver);
        booking.setStatus(BookingStatus.ACCEPTED);
        booking.setAcceptedAt(LocalDateTime.now());
        return bookingRepository.save(booking);
    }

    public Booking completeBooking(Long id) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new BookingException("No Booking found."));

        if(booking.getStatus() != BookingStatus.ACCEPTED)
            throw new BookingException("Booking is not accepted.");

        booking.setStatus(BookingStatus.COMPLETED);
        booking.setCompletedAt(LocalDateTime.now());
        return bookingRepository.save(booking);
    }

    public Booking cancelBooking(Long bookingId, Long userId) {
        // Fetch the booking
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingException("No Booking found."));

        // Check if the user is the passenger or driver
        if (booking.getPassenger().getId().equals(userId)) {
            // Passenger can cancel any time before the booking is completed
            if (booking.getStatus() == BookingStatus.COMPLETED) {
                throw new BookingException("Completed booking cannot be canceled.");
            }
        } else if (booking.getDriver() != null && booking.getDriver().getId().equals(userId)) {
            // Driver can cancel only if the booking is still pending or accepted
            if (booking.getStatus() == BookingStatus.COMPLETED) {
                throw new BookingException("Completed booking cannot be canceled.");
            }
        } else {
            throw new BookingException("You are not authorized to cancel this booking.");
        }

        // Update the status to CANCELED
        booking.setStatus(BookingStatus.CANCELED);
        booking.setCanceledAt(LocalDateTime.now());
        return bookingRepository.save(booking);
    }

    public List<Booking> getBookingHistory(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BookingException("User not found."));

        if (user.getType() == UserType.PASSENGER)
            return bookingRepository.findByPassengerAndStatusIsNot(user, BookingStatus.CANCELED);
         else
            return bookingRepository.findByDriverAndStatusIsNot(user, BookingStatus.CANCELED);
    }
}
