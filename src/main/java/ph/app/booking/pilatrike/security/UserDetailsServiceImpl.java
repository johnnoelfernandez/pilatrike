package ph.app.booking.pilatrike.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ph.app.booking.pilatrike.constant.UserType;
import ph.app.booking.pilatrike.entities.User;
import ph.app.booking.pilatrike.repositories.UserRepository;

import java.util.Collections;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String contactNumber) throws UsernameNotFoundException {
        User user = userRepository.findByContactNumber(contactNumber)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with contact number: " + contactNumber));

        return new org.springframework.security.core.userdetails.User(
                user.getContactNumber(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getType().name()))
        );
    }
}
