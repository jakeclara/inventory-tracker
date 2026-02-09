package com.jakeclara.inventorytracker.util;

import com.jakeclara.inventorytracker.model.User;
import com.jakeclara.inventorytracker.model.UserRole;

public class TestUserFactory {
    public static final String VALID_USERNAME = "test_user";
    public static final String VALID_PASSWORD_HASH = "TestPassword123!";
    public static final UserRole VALID_ROLE = UserRole.USER;

    public static User createDefaultUser() {
        return new User(VALID_USERNAME, VALID_PASSWORD_HASH, VALID_ROLE);
    }
}
