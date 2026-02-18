package com.jakeclara.inventorytracker.security;

import com.jakeclara.inventorytracker.model.User;
import com.jakeclara.inventorytracker.repository.UserRepository;
import com.jakeclara.inventorytracker.util.TestUserFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class JpaUserDetailsServiceTest {
	
	@Mock
	private UserRepository userRepository;
	
	@InjectMocks
	private JpaUserDetailsService jpaUserDetailsService;

	@Test
	@DisplayName("loadUserByUsername should return UserDetails when user exists")
	void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
		// Arrange
		User user = TestUserFactory.createDefaultUser();

		when(userRepository.findByUsername(user.getUsername()))
			.thenReturn(Optional.of(user));
		
		// Act
		UserDetails userDetails = jpaUserDetailsService.loadUserByUsername(user.getUsername());

		// Assert
		assertThat(userDetails.getUsername()).isEqualTo(user.getUsername());
		assertThat(userDetails.getPassword()).isEqualTo(user.getPasswordHash());
		assertThat(userDetails.isEnabled()).isTrue();
		assertThat(userDetails.getAuthorities())
			.extracting("authority").containsExactly("ROLE_" +user.getRole().name());
		
		verify(userRepository).findByUsername(user.getUsername());
		
	}

	@Test
	@DisplayName("loadUserByUsername should throw UsernameNotFoundException when user not found")
	void loadUserByUsername_ShouldThrowUsernameNotFoundException_WhenUserNotFound() {

		when(userRepository.findByUsername("nonexistentuser"))
			.thenReturn(Optional.empty());

		assertThatThrownBy(() -> jpaUserDetailsService.loadUserByUsername("nonexistentuser"))
			.isInstanceOf(UsernameNotFoundException.class);
		
		verify(userRepository).findByUsername("nonexistentuser");
	}
}
