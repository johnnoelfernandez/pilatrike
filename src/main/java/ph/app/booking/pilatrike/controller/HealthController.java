package ph.app.booking.pilatrike.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pilatrike")
public class HealthController {

    @GetMapping("/health")
    public String healthCheck() {
        return "✅ Backend is up and running!";
    }
}