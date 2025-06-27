package com.example.service.implementation;

import com.example.model.User;
import com.example.repository.contract.UserRepository;
import com.example.service.contract.UserService;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.getAllActiveUsers();
    }
}
