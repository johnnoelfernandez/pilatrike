package ph.app.booking.pilatrike.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String contactNumber;
    private String password;
}
