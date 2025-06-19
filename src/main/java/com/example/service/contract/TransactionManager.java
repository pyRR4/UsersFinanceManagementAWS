package com.example.service.contract;

import java.util.function.Consumer;

public interface TransactionManager {
    void execute(Consumer<String> transactionalLogic);
}
