package com.example.sms.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Represents one login account for this application.
 *
 * This is NOT a "Student". A Student is someone the school is tracking
 * (grades, enrollments, etc). An AppUser is a person who is allowed to
 * log in and use this API (for example, a school administrator or a
 * staff member).
 *
 * Spring Security uses this class (together with the password encoder
 * configured in SecurityConfig) to check usernames and passwords when
 * someone calls the API with HTTP Basic authentication.
 */
@Entity
@Table(
        name = "app_users",
        // This makes sure two accounts can never share the same username.
        // The database itself will reject a duplicate insert, even if our
        // Java code forgets to check first.
        uniqueConstraints = @UniqueConstraint(name = "uk_username", columnNames = "username")
)
public class AppUser {

    // The database will pick this value automatically (auto-increment).
    // We never set this ourselves.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The name the user types in to log in. Must be unique (see above).
    @Column(nullable = false)
    private String username;

    // IMPORTANT: this is the *encoded* (hashed) password, never the
    // plain-text password. See SecurityConfig.passwordEncoder(), which
    // uses BCrypt to hash passwords before they are ever saved here.
    @Column(nullable = false)
    private String password;

    // ADMIN or STAFF. Stored as text in the database (see Role.java).
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // JPA requires a no-argument constructor so it can create objects
    // when it loads rows back out of the database.
    public AppUser() {
    }

    // This is the constructor we actually use in our own code, for
    // example when seeding demo accounts in DataInitializer.
    public AppUser(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // --- Getters ---
    // We only expose getters here (no setters) because, in this project,
    // user accounts are only ever created once and never edited through
    // the API. If we later add an "update account" feature, setters
    // could be added the same way they are on Student and Course.

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }
}
