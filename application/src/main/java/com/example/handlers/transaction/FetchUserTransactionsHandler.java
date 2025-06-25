package com.example.handlers.transaction;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.example.factory.DependencyFactory;
import com.example.model.Transaction;
import com.example.model.User;
import com.example.model.request.ForecastCalculationRequest;
import com.example.service.contract.TransactionService;

import java.time.LocalDate;
import java.util.List;

public class FetchUserTransactionsHandler implements RequestHandler<User, ForecastCalculationRequest> {

    private final TransactionService transactionService;

    public FetchUserTransactionsHandler() {
        this.transactionService = DependencyFactory.getInstance().getService(TransactionService.class);
    }

    @Override
    public ForecastCalculationRequest handleRequest(User user, Context context) {
        int userId = user.getId();
        context.getLogger().log("Fetching transaction history for user: " + userId);

        LocalDate endDate = LocalDate.now().withDayOfMonth(1);
        LocalDate startDate = endDate.minusMonths(3);

        List<Transaction> transactions = transactionService.findAllForUserInDateRange(
                userId,
                startDate.toString(),
                endDate.toString()
        );
        context.getLogger().log("Found " + transactions.size() + " transactions for user " + userId);

        return new ForecastCalculationRequest(userId, transactions);
    }
}
