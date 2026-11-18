package cat.udl.eps.softarch.demo.config;

import cat.udl.eps.softarch.demo.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class BasicUserDetailsService implements UserDetailsService {

  final UserRepository userRepository;

  public BasicUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  // The login identifier can be the email or the username (US-AUT-02)
  @Override
  public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
    return userRepository.findByLoginIdentifier(identifier)
        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + identifier));
  }
}
