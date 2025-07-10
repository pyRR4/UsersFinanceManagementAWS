package com.example.repository.contract;

import com.example.model.User;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findByCognitoSub(String cognitoSub);
    User create(String cognitoSub, String email);
    double updateBalance(int userId, double balanceDelta);
    double updateBalance(int userId, double balanceDelta, Connection conn);
    double getBalance(int userId);
    List<User> getAllActiveUsers(); // Zmieniłem nazwę z findAll na bardziej opisową
}