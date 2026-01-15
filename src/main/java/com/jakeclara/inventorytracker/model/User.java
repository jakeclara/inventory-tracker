package com.jakeclara.inventorytracker.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    private boolean enabled;

    protected User() {}

    public User(String username, String passwordHash, Role role) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be blank");
        }
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash cannot be blank");
        }
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }

        this.username = username.trim().toLowerCase();
        this.passwordHash = passwordHash;
        this.role = role;
        this.enabled = true;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public boolean isEnabled() {
        return enabled;
    }

}
