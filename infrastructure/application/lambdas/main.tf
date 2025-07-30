locals {
  common_environment_variables = {
    DB_CLUSTER_ARN       = var.db_cluster_arn
    DB_SECRET_ARN        = var.db_secret_arn
    DB_HOST              = var.db_host
    DB_PORT              = var.db_port
    DB_NAME              = var.db_name
    DB_USERNAME          = var.db_username
    COGNITO_REGION       = var.aws_region
    COGNITO_USERPOOL_ID  = var.cognito_userpool_id
  }

  lambda_definitions = {
    "create-transaction"       = { handler = "com.example.handlers.transaction.CreateTransactionHandler::handleRequest", needs_vpc = true }
    "get-transactions"         = { handler = "com.example.handlers.transaction.GetTransactionsHandler::handleRequest", needs_vpc = true }
    "get-transaction-by-id"    = { handler = "com.example.handlers.transaction.GetTransactionByIdHandler::handleRequest", needs_vpc = true }
    "update-transaction"       = { handler = "com.example.handlers.transaction.UpdateTransactionHandler::handleRequest", needs_vpc = true }
    "delete-transaction"       = { handler = "com.example.handlers.transaction.DeleteTransactionHandler::handleRequest", needs_vpc = true }

    "create-category"          = { handler = "com.example.handlers.category.CreateCategoryHandler::handleRequest", needs_vpc = true }
    "get-categories"           = { handler = "com.example.handlers.category.GetCategoriesHandler::handleRequest", needs_vpc = true }
    "update-category"          = { handler = "com.example.handlers.category.UpdateCategoryHandler::handleRequest", needs_vpc = true }
    "delete-category"          = { handler = "com.example.handlers.category.DeleteCategoryHandler::handleRequest", needs_vpc = true }

    "create-saving-goal"       = { handler = "com.example.handlers.savingGoal.CreateGoalHandler::handleRequest", needs_vpc = true }
    "get-saving-goals"         = { handler = "com.example.handlers.savingGoal.GetGoalsHandler::handleRequest", needs_vpc = true }
    "update-saving-goal"       = { handler = "com.example.handlers.savingGoal.UpdateGoalHandler::handleRequest", needs_vpc = true }
    "delete-saving-goal"       = { handler = "com.example.handlers.savingGoal.DeleteGoalHandler::handleRequest", needs_vpc = true }
    "add-funds-to-goal"        = { handler = "com.example.handlers.savingGoal.AddFundsHandler::handleRequest", needs_vpc = true }

    "start-report-generation" = {
      handler   = "com.example.handlers.raport.StartReportGenerationHandler::handleRequest",
      needs_vpc = true,
      env_vars  = { SQS_QUEUE_URL = var.report_jobs_queue_url }
    }

    "report-generator-worker" = {
      handler   = "com.example.handlers.raport.ReportGeneratorWorkerHandler::handleRequest",
      needs_vpc = true,
      env_vars = {
        S3_BUCKET_NAME = var.reports_bucket_name
        SNS_TOPIC_ARN  = var.report_notifications_topic_arn
      }
    }

    "get-all-users" = {
      handler   = "com.example.handlers.user.GetAllUsersHandler::handleRequest",
      needs_vpc = true,
      tags      = { Feature = "Forecasting" }
    }

    "fetch-user-transactions" = {
      handler   = "com.example.handlers.transaction.FetchUserTransactionsHandler::handleRequest",
      needs_vpc = true,
      tags      = { Feature = "Forecasting" }
    }

    "calculate-forecast" = {
      handler   = "com.example.handlers.forecast.CalculateForecastHandler::handleRequest",
      needs_vpc = false,
      tags      = { Feature = "Forecasting" }
    }

    "save-forecast" = {
      handler   = "com.example.handlers.forecast.SaveForecastHandler::handleRequest",
      needs_vpc = true,
      tags      = { Feature = "Forecasting" }
    }
  }
}

module "lambda_functions" {
  source = "./lambda_function"

  for_each = local.lambda_definitions

  function_name = "${var.project_name}-${var.environment}-${each.key}"
  handler       = each.value.handler
  iam_role_arn  = var.app_lambda_role_arn
  jar_path      = var.app_jar_path
  tags          = merge(var.tags, try(each.value.tags, {}))

  vpc_config = each.value.needs_vpc ? {
    subnet_ids         = var.vpc_private_subnet_ids
    security_group_ids = [var.lambda_security_group_id]
  } : null

  environment_variables = merge(
    local.common_environment_variables,
    try(each.value.env_vars, {})
  )
}