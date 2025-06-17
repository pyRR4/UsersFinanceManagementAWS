package com.example.factory;

import com.example.repository.contract.CategoryRepository;
import com.example.repository.contract.TransactionRepository;
import com.example.repository.contract.UserRepository;
import com.example.repository.implementation.CategoryRepositoryImpl;
import com.example.repository.implementation.TransactionRepositoryImpl;
import com.example.repository.implementation.UserRepositoryImpl;
import com.example.service.contract.AuthService;
import com.example.service.contract.CategoryService;
import com.example.service.contract.TransactionService;
import com.example.service.implementation.AuthServiceImpl;
import com.example.service.implementation.CategoryServiceImpl;
import com.example.service.implementation.TransactionServiceImpl;
import com.google.gson.Gson;
import com.example.config.DatabaseConfig;
import lombok.Getter;
import software.amazon.awssdk.services.rdsdata.RdsDataClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DependencyFactory {

    @Getter
    private static final DependencyFactory instance = new DependencyFactory();

    private final Map<Class<?>, Object> services = new ConcurrentHashMap<>();

    private DependencyFactory() {
        DatabaseConfig dbConfig = new DatabaseConfig();
        RdsDataClient rdsDataClient = RdsDataClient.builder().build();

        UserRepository userRepository = new UserRepositoryImpl(rdsDataClient, dbConfig);
        AuthService authService = new AuthServiceImpl(userRepository);
        services.put(AuthService.class, authService);

        TransactionRepository transactionRepository = new TransactionRepositoryImpl(rdsDataClient, dbConfig);
        TransactionService transactionService = new TransactionServiceImpl(transactionRepository);
        services.put(TransactionService.class, transactionService);

        CategoryRepository categoryRepository = new CategoryRepositoryImpl(rdsDataClient, dbConfig);
        CategoryService categoryService = new CategoryServiceImpl(categoryRepository);
        services.put(CategoryService.class, categoryService);

        services.put(Gson.class, new Gson());
    }

    @SuppressWarnings("unchecked")
    public <T> T getService(Class<T> serviceClass) {
        T service = (T) services.get(serviceClass);
        if (service == null) {
            throw new IllegalArgumentException("Service not found for class: " + serviceClass.getName());
        }
        return service;
    }
}