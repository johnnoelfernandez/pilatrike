package ph.app.booking.pilatrike.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ph.app.booking.pilatrike.constant.BookingStatus;
import ph.app.booking.pilatrike.constant.UserType;
import ph.app.booking.pilatrike.dto.BookingDto;
import ph.app.booking.pilatrike.entities.Booking;
import ph.app.booking.pilatrike.entities.User;
import ph.app.booking.pilatrike.exception.BookingException;
import ph.app.booking.pilatrike.repositories.BookingRepository;
import ph.app.booking.pilatrike.repositories.UserRepository;
import ph.app.booking.pilatrike.service.BookingService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestBody BookingDto booking) {
        Booking savedBooking = bookingService.createBooking(booking);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBooking);
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<Booking> acceptBooking(@PathVariable Long id, @RequestParam Long driverId) {
        Booking acceptedBooking = bookingService.acceptBooking(id, driverId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(acceptedBooking);
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<Booking> completeBooking(@PathVariable Long id) {
        Booking completeBooking = bookingService.completeBooking(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(completeBooking);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Booking> cancelBooking(@PathVariable Long id, @RequestParam Long userId) {
        Booking canceledBooking = bookingService.cancelBooking(id, userId);
        return ResponseEntity.ok(canceledBooking);
    }

    @GetMapping("/history")
    public ResponseEntity<List<Booking>> getBookingHistory(@RequestParam Long userId) {
        List<Booking> history = bookingService.getBookingHistory(userId);
        return ResponseEntity.ok(history);
    }
}
