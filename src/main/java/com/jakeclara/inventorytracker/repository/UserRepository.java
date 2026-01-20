package com.jakeclara.inventorytracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jakeclara.inventorytracker.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    
}
