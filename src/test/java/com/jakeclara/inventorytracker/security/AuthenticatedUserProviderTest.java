package com.jakeclara.inventorytracker.security;

import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.jakeclara.inventorytracker.model.User;
import com.jakeclara.inventorytracker.repository.UserRepository;
import com.jakeclara.inventorytracker.util.TestUserFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;



@ExtendWith(MockitoExtension.class)
class AuthenticatedUserProviderTest {
	
	@Mock
	UserRepository userRepository;

	@InjectMocks
	AuthenticatedUserProvider authenticatedUserProvider;

	@AfterEach
	void clearSecurityContext() {
		SecurityContextHolder.clearContext();
	}

	@Test
	@DisplayName("getAuthenticatedUser should return user when authentication exists")
	void getAuthenticatedUser_ShouldReturnUser_WhenAuthenticated() {
		User user = TestUserFactory.createDefaultUser();

		Authentication authentication = new UsernamePasswordAuthenticationToken(
			user.getUsername(),
			null,
			List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
		);

		SecurityContextHolder.getContext().setAuthentication(authentication);

		when(userRepository.findByUsername(user.getUsername()))
			.thenReturn(Optional.of(user));

		User authenticatedUser = authenticatedUserProvider.getAuthenticatedUser();

		assertThat(authenticatedUser).isEqualTo(user);
	}

	@Test
	@DisplayName("getAuthenticatedUser should throw when no authentication present")
	void getAuthenticatedUser_ShouldThrow_WhenNoAuthentication() {

		SecurityContextHolder.clearContext();

		assertThatThrownBy(() -> authenticatedUserProvider.getAuthenticatedUser())
			.isInstanceOf(IllegalStateException.class)
			.hasMessage("No authenticated user found");
	}

	@Test
	@DisplayName("getAuthenticatedUser should throw when user not found in database")
	void getAuthenticatedUser_ShouldThrow_WhenUserNotFound() {

		Authentication authentication = new UsernamePasswordAuthenticationToken(
			"nonexistentUser",
			null,
			List.of()
		);

		SecurityContextHolder.getContext().setAuthentication(authentication);

		when(userRepository.findByUsername("nonexistentUser"))
			.thenReturn(Optional.empty());

		assertThatThrownBy(() -> authenticatedUserProvider.getAuthenticatedUser())
			.isInstanceOf(IllegalStateException.class)
			.hasMessage("Authenticated user not found");
	}
}
