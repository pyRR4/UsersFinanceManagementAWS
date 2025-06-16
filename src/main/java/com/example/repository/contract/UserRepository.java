package com.example.repository.contract;

import java.util.Optional;

public interface UserRepository {
    Optional<Integer> findUserIdByCognitoSub(String cognitoSub);
    int createUser(String cognitoSub, String email);
}
