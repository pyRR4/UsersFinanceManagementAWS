package com.example.factory;

import com.example.model.DatabaseCredentials;
import com.example.strategy.SimpleAverageStrategy;
import com.google.gson.Gson;
import com.example.config.DatabaseConfig;
import com.example.repository.contract.*;
import com.example.repository.implementation.*;
import com.example.service.contract.*;
import com.example.service.contract.report.ReportDistributionService;
import com.example.service.contract.report.ReportFormattingService;
import com.example.service.contract.report.ReportingJobService;
import com.example.service.implementation.*;
import com.example.service.implementation.report.ReportDistributionServiceImpl;
import com.example.service.implementation.report.ReportingJobServiceImpl;
import com.example.service.implementation.report.TextReportFormattingService;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import software.amazon.awssdk.services.rdsdata.RdsDataClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sqs.SqsClient;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DependencyFactory {

    @Getter
    private static final DependencyFactory instance = new DependencyFactory();

    private final Map<Class<?>, Object> services = new ConcurrentHashMap<>();

    /**
     * Główny konstruktor, który teraz tylko koordynuje proces tworzenia zależności,
     * delegując zadania do wyspecjalizowanych, prywatnych metod.
     */
    private DependencyFactory() {
        // Kolejność ma znaczenie - najpierw tworzymy podstawowe komponenty, potem te, które z nich korzystają.
        registerCoreComponents();
        registerRepositories();
        registerBusinessServices();
        registerReportingServices();
    }

    /**
     * Rejestruje podstawowe, współdzielone komponenty i klientów AWS.
     */
    private void registerCoreComponents() {
        DatabaseConfig dbConfig = new DatabaseConfig();
        register(DatabaseConfig.class, dbConfig);

        String secretJson = new SecretManagerServiceImpl().getSecret(dbConfig.getDbSecretArn());
        DatabaseCredentials credentials = new Gson().fromJson(secretJson, DatabaseCredentials.class);

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(String.format("jdbc:postgresql://%s:%d/%s", credentials.getHost(), credentials.getPort(), credentials.getDbname()));
        hikariConfig.setUsername(credentials.getUsername());
        hikariConfig.setPassword(credentials.getPassword());
        hikariConfig.setMaximumPoolSize(5);

        DataSource dataSource = new HikariDataSource(hikariConfig);
        register(DataSource.class, dataSource);

        register(RdsDataClient.class, RdsDataClient.builder().build());
        register(S3Client.class, S3Client.builder().build());
        register(SnsClient.class, SnsClient.builder().build());
        register(SqsClient.class, SqsClient.builder().build());
        register(Gson.class, new Gson());
    }

    /**
     * Rejestruje wszystkie implementacje repozytoriów.
     */
    private void registerRepositories() {
        register(UserRepository.class, new UserRepositoryImpl(getService(DataSource.class)));
        register(TransactionRepository.class, new TransactionRepositoryImpl(getService(DataSource.class)));
        register(CategoryRepository.class, new CategoryRepositoryImpl(getService(DataSource.class)));
        register(SavingGoalRepository.class, new SavingGoalRepositoryImpl(getService(DataSource.class)));
        register(ForecastRepository.class, new ForecastRepositoryImpl(getService(DataSource.class)));
    }

    /**
     * Rejestruje główne serwisy logiki biznesowej.
     */
    private void registerBusinessServices() {
        register(AuthService.class, new AuthServiceImpl(getService(UserRepository.class), getService(Gson.class)));
        register(UserService.class, new UserServiceImpl(getService(UserRepository.class)));
        register(TransactionService.class, new TransactionServiceImpl(getService(TransactionRepository.class)));
        register(CategoryService.class, new CategoryServiceImpl(getService(CategoryRepository.class)));
        register(TransactionManager.class, new RdsTransactionManager(getService(RdsDataClient.class), getService(DatabaseConfig.class)));

        register(SavingGoalService.class, new SavingGoalServiceImpl(
                getService(SavingGoalRepository.class),
                getService(UserRepository.class),
                getService(TransactionRepository.class),
                getService(TransactionManager.class)
        ));
        register(ForecastService.class, new ForecastServiceImpl(getService(ForecastRepository.class)));
        register(SimpleAverageStrategy.class, new SimpleAverageStrategy());
    }

    /**
     * Rejestruje wszystkie serwisy związane z generowaniem i dystrybucją raportów.
     */
    private void registerReportingServices() {
        String bucketName = System.getenv("S3_BUCKET_NAME");
        String snsTopicArn = System.getenv("SNS_TOPIC_ARN");
        String queueUrl = System.getenv("SQS_QUEUE_URL");

        register(StorageService.class, new S3StorageService(getService(S3Client.class), bucketName));
        register(NotificationService.class, new SnsNotificationService(getService(SnsClient.class), snsTopicArn));

        register(ReportDistributionService.class, new ReportDistributionServiceImpl(
                getService(StorageService.class),
                getService(NotificationService.class)
        ));
        register(ReportFormattingService.class, new TextReportFormattingService());

        register(ReportingJobService.class, new ReportingJobServiceImpl(
                getService(UserService.class),
                getService(SqsClient.class),
                getService(Gson.class),
                queueUrl
        ));
    }

    /**
     * Prywatna metoda pomocnicza do rejestrowania serwisu w mapie.
     */
    private <T> void register(Class<T> type, T instance) {
        services.put(type, instance);
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