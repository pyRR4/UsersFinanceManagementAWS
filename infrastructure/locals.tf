locals {
  common_environment_variables = {
    DB_CLUSTER_ARN = module.database.db_instance_arn
    DB_SECRET_ARN = module.db_password_secret.secret_arn
    DB_HOST       = module.database.db_instance_endpoint # lub module.database_proxy.proxy_endpoint
    DB_PORT       = module.database.db_connection_data.port
    DB_NAME       = var.db_name
    DB_USERNAME   = var.db_username
  }

  lambda_definitions = {
    "create-transaction"      = { handler = "com.example.handlers.transaction.CreateTransactionHandler::handleRequest", needs_vpc = true }
    "get-transactions"        = { handler = "com.example.handlers.transaction.GetTransactionsHandler::handleRequest", needs_vpc = true }
    "get-transaction-by-id" = { handler = "com.example.handlers.transaction.GetTransactionByIdHandler::handleRequest", needs_vpc = true }
    "update-transaction"      = { handler = "com.example.handlers.transaction.UpdateTransactionHandler::handleRequest", needs_vpc = true }
    "delete-transaction"      = { handler = "com.example.handlers.transaction.DeleteTransactionHandler::handleRequest", needs_vpc = true }

    "create-category" = { handler = "com.example.handlers.category.CreateCategoryHandler::handleRequest", needs_vpc = true }
    "get-categories"  = { handler = "com.example.handlers.category.GetCategoriesHandler::handleRequest", needs_vpc = true }
    "update-category" = { handler = "com.example.handlers.category.UpdateCategoryHandler::handleRequest", needs_vpc = true }
    "delete-category" = { handler = "com.example.handlers.category.DeleteCategoryHandler::handleRequest", needs_vpc = true }

    "create-saving-goal"  = { handler = "com.example.handlers.savingGoal.CreateSavingGoalHandler::handleRequest", needs_vpc = true }
    "get-saving-goals"    = { handler = "com.example.handlers.savingGoal.GetSavingGoalsHandler::handleRequest", needs_vpc = true }
    "update-saving-goal"  = { handler = "com.example.handlers.savingGoal.UpdateSavingGoalHandler::handleRequest", needs_vpc = true }
    "delete-saving-goal"  = { handler = "com.example.handlers.savingGoal.DeleteSavingGoalHandler::handleRequest", needs_vpc = true }
    "add-funds-to-goal"   = { handler = "com.example.handlers.savingGoal.AddFundsToGoalHandler::handleRequest", needs_vpc = true }

    "start-report-generation" = {
      handler   = "com.example.handlers.raport.StartReportGenerationHandler::handleRequest",
      needs_vpc = true,
      env_vars  = { SQS_QUEUE_URL = module.report_jobs_queue.queue_url }
    }
    "report-generator-worker" = {
      handler   = "com.example.handlers.raport.ReportGeneratorWorkerHandler::handleRequest",
      needs_vpc = true,
      env_vars = {
        S3_BUCKET_NAME = module.reports_bucket.bucket_name
        SNS_TOPIC_ARN  = module.report_notifications_topic.topic_arn
      }
    }

    "get-all-users"           = { handler = "com.example.handlers.user.GetAllUsersHandler::handleRequest", needs_vpc = true, tags = { Feature = "Forecasting" } }
    "fetch-user-transactions" = { handler = "com.example.handlers.transaction.FetchUserTransactionsHandler::handleRequest", needs_vpc = true, tags = { Feature = "Forecasting" } }
    "calculate-forecast"      = { handler = "com.example.handlers.forecast.CalculateForecastHandler::handleRequest", needs_vpc = false, tags = { Feature = "Forecasting" } }
    "save-forecast"           = { handler = "com.example.handlers.forecast.SaveForecastHandler::handleRequest", needs_vpc = true, tags = { Feature = "Forecasting" }}
  }
}