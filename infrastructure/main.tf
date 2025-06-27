data "aws_iam_role" "lab_execution_role" {
  name = "LabRole"
}

module "user_pool" {
  source = "./modules/cognito_user_pool"

  pool_name       = "${var.project_name}-${var.environment}-user-pool"
  app_client_name = "${var.project_name}-${var.environment}-app-client"
  tags = {
    Environment = var.environment
    Project     = var.project_name
  }
}

module "report_notifications_topic" {
  source = "./modules/sns_topic"

  topic_name = "${var.project_name}-${var.environment}-report-notifications"

  tags = {
    Environment = var.environment
    Project     = var.project_name
  }
}

module "report_jobs_queue" {
  source = "./modules/sqs_queue"

  queue_name = "${var.project_name}-${var.environment}-report-jobs"
  is_fifo    = true

  tags = {
    Environment = var.environment
    Project     = var.project_name
  }
}

module "reports_bucket" {
  source = "./modules/s3_bucket"

  bucket_prefix = "${var.project_name}-${var.environment}-reports"

  tags = {
    Environment = var.environment
    Project     = var.project_name
  }
}

resource "aws_security_group" "lambda_sg" {
  name        = "${var.project_name}-${var.environment}-lambda-sg"
  description = "Allows egress traffic for Lambda functions"
  vpc_id      = module.vpc.vpc_id

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
  tags = var.tags
}

module "vpc" {
  source = "./modules/vpc"

  project_name = var.project_name
  aws_region   = var.aws_region
  tags         = var.tags
}

module "database" {
  source = "./modules/rds_postgres"

  project_name            = var.project_name
  environment             = var.environment
  tags                    = var.tags
  vpc_id                  = module.vpc.vpc_id
  private_subnet_ids      = module.vpc.public_subnet_ids
  allowed_security_group_id = aws_security_group.lambda_sg.id

  db_username             = var.db_username
  db_password             = var.db_password
}

module "db_credentials_secret" {
  source = "./modules/secrets_manager"

  secret_name        = "${var.project_name}/${var.environment}/db-credentials/v2"
  secret_description = "Przechowuje dane dostępowe do bazy danych RDS."

  secret_payload = {
    username = var.db_username
    password = var.db_password
    dbname   = var.db_name
    host     = module.database.db_connection_data.host
    port     = module.database.db_connection_data.port
  }

  tags = {
    Environment = var.environment
    Project     = var.project_name
  }
}

locals {
  common_api_environment_variables = {
    DB_CLUSTER_ARN = module.database.db_instance_arn
    DB_SECRET_ARN  = module.db_credentials_secret.secret_arn
    DB_NAME        = var.db_name
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
      handler = "com.example.handlers.raport.StartReportGenerationHandler::handleRequest",
      needs_vpc = true,
      env_vars = {
        SQS_QUEUE_URL = module.report_jobs_queue.queue_url
      }
    }
    "report-generator-worker" = {
      handler = "com.example.handlers.raport.ReportGeneratorWorkerHandler::handleRequest",
      needs_vpc = true,
      env_vars = {
        S3_BUCKET_NAME = module.reports_bucket.bucket_name
        SNS_TOPIC_ARN  = module.report_notifications_topic.topic_arn
      }
    }
    "get-all-users" = {
      handler   = "com.mojafirma.handlers.user.GetAllUsersHandler::handleRequest",
      needs_vpc = true
    }
    "fetch-user-transactions" = {
      handler   = "com.mojafirma.handlers.transaction.FetchUserTransactionsHandler::handleRequest",
      needs_vpc = true
    }
    "calculate-forecast" = {
      handler   = "com.mojafirma.handlers.forecast.CalculateForecastHandler::handleRequest",
      needs_vpc = false
    }
    "save-forecast" = {
      handler   = "com.mojafirma.handlers.forecast.SaveForecastHandler::handleRequest",
      needs_vpc = true
    }
  }
}

module "lambda_functions" {
  source = "./modules/lambda_function"

  for_each = local.lambda_definitions

  function_name = "${var.project_name}-${var.environment}-${each.key}"
  handler       = each.value.handler
  iam_role_arn  = data.aws_iam_role.lab_execution_role.arn
  jar_path      = var.app_jar_path
  tags          = var.tags

  vpc_config = each.value.needs_vpc ? {
    subnet_ids         = module.vpc.private_subnet_ids
    security_group_ids = [aws_security_group.lambda_sg.id]
  } : null

  environment_variables = merge(
    local.common_api_environment_variables,
    try(each.value.env_vars, {})
  )
}

# Ten plik definiuje główną bramę API i komponuje wszystkie endpointy,
# wywołując reużywalny moduł 'api_gateway_endpoint'.

# --- 1. Główne zasoby API (tworzone tylko raz) ---

resource "aws_api_gateway_rest_api" "main" {
  name        = "${var.project_name}-${var.environment}-api"
  description = "API dla aplikacji do zarządzania finansami."

  tags = var.tags
}

resource "aws_api_gateway_authorizer" "cognito_auth" {
  name          = "CognitoAuthorizer"
  type          = "COGNITO_USER_POOLS"
  rest_api_id   = aws_api_gateway_rest_api.main.id
  provider_arns = [module.user_pool.pool_arn]
  identity_source = "method.request.header.Authorization"
}

# --- 2. Kompozycja Endpointów za pomocą Modułów ---

# --- Ścieżki dla /transactions ---

module "api_transactions" {
  source = "./modules/api_gateway_endpoint"

  rest_api_id = aws_api_gateway_rest_api.main.id
  rest_api_execution_arn = aws_api_gateway_rest_api.main.execution_arn
  parent_id   = aws_api_gateway_rest_api.main.root_resource_id
  path_part   = "transactions"
  authorizer_id = aws_api_gateway_authorizer.cognito_auth.id

  lambda_integrations = {
    "POST" = {
      invoke_arn = module.lambda_functions["create-transaction"].invoke_arn
      name       = module.lambda_functions["create-transaction"].function_name
    },
    "GET" = {
      invoke_arn = module.lambda_functions["get-transactions"].invoke_arn
      name       = module.lambda_functions["get-transactions"].function_name
    }
  }
  depends_on = [aws_api_gateway_authorizer.cognito_auth]
}

module "api_transaction_by_id" {
  source = "./modules/api_gateway_endpoint"

  rest_api_id = aws_api_gateway_rest_api.main.id
  rest_api_execution_arn = aws_api_gateway_rest_api.main.execution_arn
  parent_id   = module.api_transactions.resource_id # Łączymy z /transactions
  path_part   = "{id}"
  authorizer_id = aws_api_gateway_authorizer.cognito_auth.id

  lambda_integrations = {
    "GET" = {
      invoke_arn = module.lambda_functions["get-transaction-by-id"].invoke_arn
      name       = module.lambda_functions["get-transaction-by-id"].function_name
    },
    "PUT" = {
      invoke_arn = module.lambda_functions["update-transaction"].invoke_arn
      name       = module.lambda_functions["update-transaction"].function_name
    },
    "DELETE" = {
      invoke_arn = module.lambda_functions["delete-transaction"].invoke_arn
      name       = module.lambda_functions["delete-transaction"].function_name
    }
  }
  depends_on = [aws_api_gateway_authorizer.cognito_auth]
}

# --- Ścieżki dla /categories ---

module "api_categories" {
  source = "./modules/api_gateway_endpoint"

  rest_api_id   = aws_api_gateway_rest_api.main.id
  rest_api_execution_arn = aws_api_gateway_rest_api.main.execution_arn
  parent_id     = aws_api_gateway_rest_api.main.root_resource_id
  path_part     = "categories"
  authorizer_id = aws_api_gateway_authorizer.cognito_auth.id

  lambda_integrations = {
    "POST" = {
      invoke_arn = module.lambda_functions["create-category"].invoke_arn
      name       = module.lambda_functions["create-category"].function_name
    },
    "GET" = {
      invoke_arn = module.lambda_functions["get-categories"].invoke_arn
      name       = module.lambda_functions["get-categories"].function_name
    }
  }
  depends_on = [aws_api_gateway_authorizer.cognito_auth]
}

module "api_category_by_id" {
  source = "./modules/api_gateway_endpoint"

  rest_api_id = aws_api_gateway_rest_api.main.id
  rest_api_execution_arn = aws_api_gateway_rest_api.main.execution_arn
  parent_id   = module.api_categories.resource_id
  path_part   = "{id}"
  authorizer_id = aws_api_gateway_authorizer.cognito_auth.id

  lambda_integrations = {
    "PUT" = {
      invoke_arn = module.lambda_functions["update-category"].invoke_arn
      name       = module.lambda_functions["update-category"].function_name
    },
    "DELETE" = {
      invoke_arn = module.lambda_functions["delete-category"].invoke_arn
      name       = module.lambda_functions["delete-category"].function_name
    }
  }
  depends_on = [aws_api_gateway_authorizer.cognito_auth]
}

# --- Ścieżki dla /saving-goals ---
module "api_saving_goals" {
  source = "./modules/api_gateway_endpoint"

  rest_api_id   = aws_api_gateway_rest_api.main.id
  rest_api_execution_arn = aws_api_gateway_rest_api.main.execution_arn
  parent_id     = aws_api_gateway_rest_api.main.root_resource_id
  path_part     = "saving-goals"
  authorizer_id = aws_api_gateway_authorizer.cognito_auth.id

  lambda_integrations = {
    "POST" = {
      invoke_arn = module.lambda_functions["create-saving-goal"].invoke_arn
      name       = module.lambda_functions["create-saving-goal"].function_name
    },
    "GET" = {
      invoke_arn = module.lambda_functions["get-saving-goals"].invoke_arn
      name       = module.lambda_functions["get-saving-goals"].function_name
    }
  }
  depends_on = [aws_api_gateway_authorizer.cognito_auth]
}

module "api_saving_goal_by_id" {
  source = "./modules/api_gateway_endpoint"

  rest_api_id = aws_api_gateway_rest_api.main.id
  rest_api_execution_arn = aws_api_gateway_rest_api.main.execution_arn
  parent_id   = module.api_saving_goals.resource_id
  path_part   = "{id}"
  authorizer_id = aws_api_gateway_authorizer.cognito_auth.id

  lambda_integrations = {
    "PUT" = {
      invoke_arn = module.lambda_functions["update-saving-goal"].invoke_arn
      name       = module.lambda_functions["update-saving-goal"].function_name
    },
    "DELETE" = {
      invoke_arn = module.lambda_functions["delete-saving-goal"].invoke_arn
      name       = module.lambda_functions["delete-saving-goal"].function_name
    }
  }
  depends_on = [aws_api_gateway_authorizer.cognito_auth]
}

module "api_saving_goal_add_funds" {
  source = "./modules/api_gateway_endpoint"

  rest_api_id   = aws_api_gateway_rest_api.main.id
  rest_api_execution_arn = aws_api_gateway_rest_api.main.execution_arn
  parent_id     = module.api_saving_goal_by_id.resource_id
  path_part     = "add-funds"
  authorizer_id = aws_api_gateway_authorizer.cognito_auth.id

  lambda_integrations = {
    "POST" = {
      invoke_arn = module.lambda_functions["add-funds-to-goal"].invoke_arn
      name       = module.lambda_functions["add-funds-to-goal"].function_name
    }
  }
  depends_on = [aws_api_gateway_authorizer.cognito_auth]
}

# --- 3. Wdrożenie API (Deployment) ---
# Ten zasób "publikuje" wszystkie powyższe zmiany.

resource "aws_api_gateway_deployment" "main" {
  rest_api_id = aws_api_gateway_rest_api.main.id

  # Ten trigger powoduje, że API jest wdrażane ponownie za każdym razem,
  # gdy zmieniamy cokolwiek w jego strukturze.
  triggers = {
    redeployment = sha1(jsonencode([
      module.api_transactions.resource_id,
      module.api_transaction_by_id.resource_id,
      module.api_categories.resource_id,
      module.api_category_by_id.resource_id,
      module.api_saving_goals.resource_id,
      module.api_saving_goal_by_id.resource_id,
      module.api_saving_goal_add_funds.resource_id
    ]))
  }

  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_api_gateway_stage" "main" {
  deployment_id = aws_api_gateway_deployment.main.id
  rest_api_id   = aws_api_gateway_rest_api.main.id
  stage_name    = var.environment
}

# --- 4. Uprawnienia dla API Gateway ---
# Musimy jawnie pozwolić API Gateway na uruchamianie wszystkich naszych funkcji.
# Robimy to w module 'api_gateway_endpoint' za pomocą pętli for_each, więc nie musimy już tego tutaj powtarzać!