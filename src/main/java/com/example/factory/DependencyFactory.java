package com.example.factory;

import com.example.repository.contract.TransactionRepository;
import com.example.repository.contract.UserRepository;
import com.example.repository.implementation.TransactionRepositoryImpl;
import com.example.repository.implementation.UserRepositoryImpl;
import com.example.service.contract.AuthService;
import com.example.service.contract.TransactionService;
import com.example.service.implementation.AuthServiceImpl;
import com.example.service.implementation.TransactionServiceImpl;
import com.google.gson.Gson;
import com.example.config.DatabaseConfig;
import lombok.Getter;
import software.amazon.awssdk.services.rdsdata.RdsDataClient;

@Getter
public class DependencyFactory {

    @Getter
    private static final DependencyFactory instance = new DependencyFactory();

    private final AuthService authService;
    private final TransactionService transactionService;
    private final Gson gson;

    private DependencyFactory() {
        DatabaseConfig dbConfig = new DatabaseConfig();
        RdsDataClient rdsDataClient = RdsDataClient.builder().build();
        this.gson = new Gson();

        UserRepository userRepository = new UserRepositoryImpl(rdsDataClient, dbConfig);
        this.authService = new AuthServiceImpl(userRepository);

        TransactionRepository transactionRepository = new TransactionRepositoryImpl(rdsDataClient, dbConfig);
        this.transactionService = new TransactionServiceImpl(transactionRepository);
    }
}