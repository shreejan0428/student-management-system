package com.example.sms.model;

/**
 * The set of roles that a logged-in user of this API can have.
 *
 * We only need two roles for this project:
 *   ADMIN - full access (could create/update/delete everything)
 *   STAFF - a regular staff member who uses the system day to day
 *
 * This is stored in the database as a String (see the @Enumerated
 * annotation on AppUser.role) so that the database rows are easy to
 * read directly (e.g. "ADMIN" instead of a number like "0").
 */
public enum Role {
    ADMIN,
    STAFF
}
