package com.jakeclara.inventorytracker.model;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import com.jakeclara.inventorytracker.util.TestUserFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {
	@Test
	@DisplayName("Constructor with valid data sets fields correctly")
	void constructor_WithValidData_SetsFields() {
		User user = TestUserFactory.createDefaultUser();
		
		assertThat(user.getId())
		.as("New user should have null id before being saved to DB")
		.isNull();

		assertThat(user.getUsername()).isEqualTo(TestUserFactory.VALID_USERNAME);
		assertThat(user.getPasswordHash()).isEqualTo(TestUserFactory.VALID_PASSWORD_HASH);
		assertThat(user.getRole()).isEqualTo(TestUserFactory.VALID_ROLE);
		
		assertThat(user.isEnabled())
		.as("New user should be active by default")
		.isTrue();
	}

	@Test
	@DisplayName("Constructor should trim and lowercase username")
	void constructor_SanitizeUsername() {
		User user = new User(
			"  ADMIN  ",
			TestUserFactory.VALID_PASSWORD_HASH,
			TestUserFactory.VALID_ROLE
		);
		assertThat(user.getUsername()).isEqualTo("admin");
	}

	@ParameterizedTest
	@NullSource
	@ValueSource(strings = {"", "   "})
	@DisplayName("Constructor should throw exception for null or blank usernames")
	void constructor_WithInvalidUsername_ThrowsException(String invalidUsername) {
		assertThatThrownBy(() -> new User(
			invalidUsername,
			TestUserFactory.VALID_PASSWORD_HASH,
			TestUserFactory.VALID_ROLE
		)).isInstanceOf(IllegalArgumentException.class)
		.hasMessage("Username cannot be blank");
	}

	@Test
	@DisplayName("Constructor should throw exception for too long username")
	void constructor_WithTooLongUsername_ThrowsException() {
		String longUsername = "a".repeat(51); 
		assertThatThrownBy(() -> new User(
			longUsername,
			TestUserFactory.VALID_PASSWORD_HASH,
			TestUserFactory.VALID_ROLE
		)).isInstanceOf(IllegalArgumentException.class)
		.hasMessage("Username cannot exceed 50 characters");
	}

	@ParameterizedTest
	@NullSource
	@ValueSource(strings = {"", "   "})
	@DisplayName("Constructor should throw exception for null or blank password hashes")
	void constructor_WithInvalidPasswordHash_ThrowsException(String invalidPasswordHash) {
		assertThatThrownBy(() -> new User(
			TestUserFactory.VALID_USERNAME,
			invalidPasswordHash,
			TestUserFactory.VALID_ROLE
		)).isInstanceOf(IllegalArgumentException.class)
		.hasMessage("Password hash cannot be blank");
	}

	@Test
	@DisplayName("Constructor with null role throws exception")
	void constructor_WithNullRole_ThrowsException() {
		assertThatThrownBy(() -> new User(
			TestUserFactory.VALID_USERNAME,
			TestUserFactory.VALID_PASSWORD_HASH,
			null
		)).isInstanceOf(IllegalArgumentException.class)
		.hasMessage("Role cannot be null");
	}

}
