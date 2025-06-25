package com.example.repository.implementation;

import com.example.config.DatabaseConfig;
import com.example.model.Forecast;
import com.example.model.request.ForecastRequest;
import com.example.repository.AbstractRdsRepository;
import com.example.repository.contract.ForecastRepository;
import software.amazon.awssdk.services.rdsdata.RdsDataClient;
import software.amazon.awssdk.services.rdsdata.model.ExecuteStatementRequest;
import software.amazon.awssdk.services.rdsdata.model.ExecuteStatementResponse;
import software.amazon.awssdk.services.rdsdata.model.Field;
import software.amazon.awssdk.services.rdsdata.model.SqlParameter;

import java.time.OffsetDateTime;
import java.util.List;

public class ForecastRepositoryImpl extends AbstractRdsRepository<Forecast> implements ForecastRepository {

    public ForecastRepositoryImpl(RdsDataClient rdsDataClient, DatabaseConfig dbConfig) {
        super(rdsDataClient, dbConfig);
    }

    @Override
    public Forecast save(ForecastRequest forecast) {
        String sql = "INSERT INTO forecasts (user_id, forecast_for_date, forecasted_amount, algorithm_version) " +
                "VALUES (:user_id, :forecast_for_date, :forecasted_amount, :algorithm_version) " +
                "ON CONFLICT (user_id, forecast_for_date) DO UPDATE SET " +
                "forecasted_amount = EXCLUDED.forecasted_amount, " +
                "algorithm_version = EXCLUDED.algorithm_version, " +
                "created_at = NOW() " +
                "RETURNING id, user_id, forecast_for_date, forecasted_amount, algorithm_version, created_at";

        SqlParameter userIdParam = userParam(forecast.getUserId());
        SqlParameter dateParam = dateParam(forecast.getForecastForDate().toString());
        SqlParameter amountParam = forecastedAmountParam(forecast.getForecastedAmount());
        SqlParameter algoParam = algorithmVersionParam(forecast.getAlgorithmVersion());

        ExecuteStatementRequest sqlRequest = createExecuteStatementRequest(sql, userIdParam, dateParam, amountParam, algoParam);
        ExecuteStatementResponse response = rdsDataClient.executeStatement(sqlRequest);

        return mapResponseToList(response).get(0);
    }

    @Override
    protected Forecast mapToEntity(List<Field> record) {
        return new Forecast(
                record.get(0).longValue().intValue(),       // id
                record.get(1).longValue().intValue(),       // user_id
                record.get(2).stringValue(),                // forecast_for_date
                record.get(3).doubleValue(),                // forecasted_amount
                record.get(4).stringValue(),                // algorithm_version
                OffsetDateTime.parse(record.get(5).stringValue().replace(" ", "T") + "Z") // created_at
        );
    }

    private SqlParameter userParam(int userId) {
        return SqlParameter.builder()
                .name("user_id")
                .value(Field.builder().longValue((long) userId).build())
                .build();
    }

    private SqlParameter dateParam(String forecastForDate) {
        return SqlParameter.builder()
                .name("forecast_for_date")
                .value(Field.builder().stringValue(forecastForDate).build())
                .build();
    }

    private SqlParameter algorithmVersionParam(String algorithmVersion) {
        return SqlParameter.builder()
                .name("algorithm_version")
                .value(Field.builder().stringValue(algorithmVersion).build())
                .build();
    }

    private SqlParameter forecastedAmountParam(double forecastedAmount) {
        return SqlParameter.builder()
                .name("algorithm_version")
                .value(Field.builder().doubleValue(forecastedAmount).build())
                .build();
    }
}
