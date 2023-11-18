package com.foodapp.service;

import com.foodapp.model.User;
import com.foodapp.model.enums.UserType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class LoginDataAdapter {

    private Connection connection;

    public LoginDataAdapter(Connection connection) {
        this.connection = connection;
    }

    public User getUser(String username, String password) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("select * from Users where username=? and password=?");
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, password);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            User user = new User();
            user.setUsername(resultSet.getString(1));
            user.setUserType(UserType.valueOf(resultSet.getString(3)));
            user.setUserId(resultSet.getInt(4));
            user.setName(resultSet.getString(5));
            user.setAddress(resultSet.getString(6));
            user.setPhoneNumber(resultSet.getString(7));
            user.setCardNumber(resultSet.getString(8));
            user.setToken(resultSet.getString(9));
            return user;
        }
        return null;
    }

    public User getUserWithToken(int userId, String token) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("select * from USERS where user_id=? and token=?");
        preparedStatement.setInt(1, userId);
        preparedStatement.setString(2, token);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            User user = new User();
            user.setUsername(resultSet.getString(1));
            user.setUserType(UserType.valueOf(resultSet.getString(3)));
            user.setUserId(resultSet.getInt(4));
            user.setName(resultSet.getString(5));
            user.setAddress(resultSet.getString(6));
            user.setPhoneNumber(resultSet.getString(7));
            user.setCardNumber(resultSet.getString(8));
            user.setToken(resultSet.getString(9));
            return user;
        }
        return null;
    }

//    public boolean saveUser(User user, String password) throws SQLException {
//        PreparedStatement saveStatement = connection.prepareStatement("insert into USERS (username, password, type, name, address, phone_no, card_no, token) values (?,?,?,?,?,?,?,?)");
//        saveStatement.setString(1, user.getUsername());
//        saveStatement.setString(2, password);
//        saveStatement.setString(3, user.getUserType().toString());
//        saveStatement.setString(4, user.getName());
//        saveStatement.setString(5, user.getAddress());
//        saveStatement.setString(6, user.getPhoneNumber());
//        saveStatement.setString(7, user.getCardNumber());
//        saveStatement.setString(8, user.getToken());
//    }

    public boolean updateUserWithPassword(User user, String password) throws SQLException {
        User existingUser = getUser(user.getUsername(), password);
        if (!Objects.isNull(existingUser)) { // user exists, so update
            existingUser.setToken(user.getToken() != null && !user.getToken().trim().isEmpty() ? user.getToken() : existingUser.getToken());
            existingUser.setName(user.getName() != null && !user.getName().trim().isEmpty() ? user.getName() : existingUser.getName());
            existingUser.setAddress(user.getAddress() != null && !user.getAddress().trim().isEmpty() ? user.getAddress() : existingUser.getAddress());

            PreparedStatement updateStatement = connection.prepareStatement("update USERS set token=?, name=?, address=? where username=? and password=?");
            updateStatement.setString(1, existingUser.getToken());
            updateStatement.setString(2, existingUser.getName());
            updateStatement.setString(3, existingUser.getAddress());
            updateStatement.setString(4, existingUser.getUsername());
            updateStatement.setString(5, password);

            return updateStatement.executeUpdate() > 0;
        } else {
            throw new RuntimeException("User not found!");
        }
    }
}
