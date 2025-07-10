CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       cognito_sub VARCHAR(255) UNIQUE NOT NULL,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       balance NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
                       created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE categories (
                            id SERIAL PRIMARY KEY,
                            name VARCHAR(255) NOT NULL,
                            user_id INTEGER NOT NULL,

                            CONSTRAINT uq_user_category_name UNIQUE (user_id, name),

                            CONSTRAINT fk_category_user
                                FOREIGN KEY(user_id)
                                    REFERENCES users(id)
                                    ON DELETE CASCADE
);

CREATE TABLE transactions (
                              id SERIAL PRIMARY KEY,
                              user_id INTEGER NOT NULL,
                              amount NUMERIC(10, 2) NOT NULL,
                              date TIMESTAMP WITH TIME ZONE NOT NULL,
                              description TEXT,
                              category_id INTEGER,

                              CONSTRAINT fk_transaction_user
                                  FOREIGN KEY(user_id)
                                      REFERENCES users(id)
                                      ON DELETE CASCADE,

                              CONSTRAINT fk_transaction_category
                                  FOREIGN KEY(category_id)
                                      REFERENCES categories(id)
                                      ON DELETE SET NULL
);

CREATE TABLE saving_goals (
                              id SERIAL PRIMARY KEY,
                              title VARCHAR(255) NOT NULL,
                              target_amount NUMERIC(12, 2) NOT NULL,
                              current_amount NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
                              user_id INTEGER NOT NULL,

                              CONSTRAINT fk_goal_user
                                  FOREIGN KEY(user_id)
                                      REFERENCES users(id)
                                      ON DELETE CASCADE
);

CREATE TABLE forecasts (
                           id SERIAL PRIMARY KEY,
                           user_id INTEGER NOT NULL,
                           forecast_for_date DATE NOT NULL,
                           forecasted_amount NUMERIC(12, 2) NOT NULL,
                           algorithm_version VARCHAR(50) NOT NULL,
                           created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),

                           CONSTRAINT uq_user_forecast_date UNIQUE (user_id, forecast_for_date),

                           CONSTRAINT fk_forecast_user
                               FOREIGN KEY(user_id)
                                   REFERENCES users(id)
                                   ON DELETE CASCADE
);