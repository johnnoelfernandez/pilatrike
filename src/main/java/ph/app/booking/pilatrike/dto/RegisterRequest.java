package ph.app.booking.pilatrike.dto;

import lombok.Data;
import ph.app.booking.pilatrike.constant.UserType;

@Data
public class RegisterRequest {
    private String name;
    private String contactNumber;
    private String password;
    private UserType type;
}
