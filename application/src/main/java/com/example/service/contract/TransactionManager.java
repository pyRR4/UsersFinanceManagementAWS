package com.example.service.contract;

import java.sql.Connection;
import java.util.function.Consumer;

public interface TransactionManager {
    void execute(Consumer<Connection> transactionalLogic);
}
