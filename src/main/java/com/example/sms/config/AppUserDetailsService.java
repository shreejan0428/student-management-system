package com.example.sms.config;

import com.example.sms.model.AppUser;
import com.example.sms.repository.AppUserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * This is the missing link between our AppUser database table and
 * Spring Security's login process.
 *
 * Without a class like this, Spring Security has no idea our
 * app_users table even exists. Spring Boot's auto-configuration falls
 * back to generating one random, single-use account at startup (you
 * would see a line like "Using generated security password: ..." in
 * the console) - which explains why "admin" / "admin123" was being
 * rejected with 401 Unauthorized, even though those exact values are
 * sitting right there in the database (seeded by DataInitializer).
 *
 * By implementing Spring Security's UserDetailsService interface and
 * registering this class as a @Service (which makes it an
 * auto-detected Spring bean), Spring Security automatically finds it
 * and uses it - together with the PasswordEncoder bean already defined
 * in SecurityConfig - to check submitted usernames and passwords
 * against real rows in app_users. No extra wiring is needed anywhere
 * else.
 */
@Service
public class AppUserDetailsService implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    public AppUserDetailsService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    /**
     * Spring Security calls this method automatically every time
     * someone tries to log in (for example, with HTTP Basic auth on
     * every API request). We just need to look the username up in our
     * own table and hand back the information Spring Security needs to
     * finish checking the password itself.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<AppUser> appUserOrEmpty = appUserRepository.findByUsername(username);

        if (appUserOrEmpty.isEmpty()) {
            throw new UsernameNotFoundException("No account found for username: " + username);
        }

        AppUser appUser = appUserOrEmpty.get();

        // Spring Security expects each role/authority name to be
        // prefixed with "ROLE_" by convention, e.g. "ROLE_ADMIN".
        String authorityName = "ROLE_" + appUser.getRole().name();

        return org.springframework.security.core.userdetails.User.builder()
                .username(appUser.getUsername())
                // This is already the BCrypt-hashed password from the
                // database (see DataInitializer and AppUser.java) -
                // Spring Security compares the submitted plain-text
                // password against this hash using the PasswordEncoder
                // bean from SecurityConfig, so we never see or compare
                // plain-text passwords ourselves.
                .password(appUser.getPassword())
                .authorities(List.of(new SimpleGrantedAuthority(authorityName)))
                .build();
    }
}
