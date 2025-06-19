package com.example.factory;

import com.example.repository.contract.*;
import com.example.repository.implementation.*;
import com.example.service.contract.*;
import com.example.service.implementation.*;
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

        TransactionManager transactionManager = new RdsTransactionManager(rdsDataClient, dbConfig);
        services.put(TransactionManager.class, transactionManager);

        UserRepository userRepository = new UserRepositoryImpl(rdsDataClient, dbConfig);
        AuthService authService = new AuthServiceImpl(userRepository);
        services.put(AuthService.class, authService);

        TransactionRepository transactionRepository = new TransactionRepositoryImpl(rdsDataClient, dbConfig);
        TransactionService transactionService = new TransactionServiceImpl(transactionRepository);
        services.put(TransactionService.class, transactionService);

        CategoryRepository categoryRepository = new CategoryRepositoryImpl(rdsDataClient, dbConfig);
        CategoryService categoryService = new CategoryServiceImpl(categoryRepository);
        services.put(CategoryService.class, categoryService);

        SavingGoalRepository savingGoalRepository = new SavingGoalRepositoryImpl(rdsDataClient, dbConfig);
        SavingGoalService savingGoalService = new SavingGoalServiceImpl(
                savingGoalRepository, userRepository, transactionRepository, transactionManager);
        services.put(SavingGoalService.class, savingGoalService);

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