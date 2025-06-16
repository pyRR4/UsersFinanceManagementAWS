package com.example.repository.contract;

import com.example.model.User;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findByCognitoSub(String cognitoSub);
    User create(String cognitoSub, String email);
}