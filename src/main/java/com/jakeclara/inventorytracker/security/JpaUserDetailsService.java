package com.jakeclara.inventorytracker.security;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.jakeclara.inventorytracker.model.User;
import com.jakeclara.inventorytracker.repository.UserRepository;

@Service
public class JpaUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    
    public JpaUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads a user from the database by their username.
     *
     * @param username the username to search for
     * @return a UserDetails object representing the user
     * @throws UsernameNotFoundException if no user is found with the given username
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return org.springframework.security.core.userdetails.User
            .withUsername(user.getUsername())
            .password(user.getPasswordHash())
            .roles(user.getRole().name())
            .disabled(!user.isEnabled())
            .build();
    }
}