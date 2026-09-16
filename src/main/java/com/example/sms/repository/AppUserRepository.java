package com.example.sms.repository;

import com.example.sms.model.AppUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Handles all direct database access for AppUser rows (login accounts,
 * not students - see the comment at the top of AppUser.java).
 */
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    /**
     * Looks up a login account by its username. Used when checking
     * whether an account already exists (see DataInitializer, which
     * seeds a couple of demo accounts the first time the app starts).
     */
    Optional<AppUser> findByUsername(String username);
}
