    package ph.app.booking.pilatrike.controller;

    import lombok.RequiredArgsConstructor;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
    import org.springframework.security.core.AuthenticationException;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.web.bind.annotation.*;
    import ph.app.booking.pilatrike.dto.AuthRequest;
    import ph.app.booking.pilatrike.dto.AuthResponse;
    import ph.app.booking.pilatrike.dto.RegisterRequest;
    import ph.app.booking.pilatrike.entities.User;
    import ph.app.booking.pilatrike.repositories.UserRepository;
    import ph.app.booking.pilatrike.security.JwtService;

    @RestController
    @RequestMapping("/api/auth")
    @RequiredArgsConstructor
    public class AuthController {

        private final AuthenticationManager authenticationManager;
        private final UserRepository userRepository;
        private final JwtService jwtService;
        private final PasswordEncoder passwordEncoder;

        @PostMapping("/register")
        public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
            if (userRepository.existsByContactNumber(request.getContactNumber())) {
                return ResponseEntity.badRequest().body("Contact number already in use.");
            }

            User user = User.builder()
                    .name(request.getName())
                    .contactNumber(request.getContactNumber())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .type(request.getType())
                    .build();

            userRepository.save(user);

            String token = jwtService.generateToken(user);
            return ResponseEntity.ok(new AuthResponse(token));
        }

        @PostMapping("/login")
        public ResponseEntity<?> login(@RequestBody AuthRequest request) {
            try {
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(request.getContactNumber(), request.getPassword())
                );
            } catch (AuthenticationException e) {
                return ResponseEntity.badRequest().body("Invalid credentials.");
            }

            User user = userRepository.findByContactNumber(request.getContactNumber())
                    .orElseThrow(() -> new RuntimeException("User not found."));

            String token = jwtService.generateToken(user);
            return ResponseEntity.ok(new AuthResponse(token));
        }
    }
