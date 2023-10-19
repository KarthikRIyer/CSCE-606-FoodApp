package com.foodapp.service;

import com.foodapp.model.User;

import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

public class LoginService {

    private LoginDataAdapter loginDataAdapter;

    public LoginService(LoginDataAdapter loginDataAdapter) {
        this.loginDataAdapter = loginDataAdapter;
    }

    public User loginAndGenerateToken(String username, String password) throws SQLException {
        User user = loginDataAdapter.getUser(username, password);
        if (Objects.isNull(user)) {
            throw new RuntimeException("Incorrect credentials or user not found!");
        }

        user.setToken(UUID.randomUUID().toString());
        boolean saved = loginDataAdapter.updateUserWithPassword(user, password);
        if (!saved) {
            throw new RuntimeException("Unable to login. Try again.");
        }
        return user;
    }

}
