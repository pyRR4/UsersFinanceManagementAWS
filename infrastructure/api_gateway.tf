resource "aws_api_gateway_account" "main" {
  cloudwatch_role_arn = module.api_gateway_logging_role.role_arn

  depends_on = [module.api_gateway_logging_role]
}

resource "aws_api_gateway_authorizer" "cognito_auth" {
  name          = "CognitoAuthorizer"
  type          = "COGNITO_USER_POOLS"
  rest_api_id   = aws_api_gateway_rest_api.main.id
  provider_arns = [module.user_pool.pool_arn]
}

module "api_transactions" {
  source = "./modules/api_gateway_endpoint"

  rest_api_id            = aws_api_gateway_rest_api.main.id
  rest_api_execution_arn = aws_api_gateway_rest_api.main.execution_arn
  parent_id              = aws_api_gateway_rest_api.main.root_resource_id
  path_part              = "transactions"
  authorizer_id          = aws_api_gateway_authorizer.cognito_auth.id

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

  rest_api_id            = aws_api_gateway_rest_api.main.id
  rest_api_execution_arn = aws_api_gateway_rest_api.main.execution_arn
  parent_id              = module.api_transactions.resource_id
  path_part              = "{id}"
  authorizer_id          = aws_api_gateway_authorizer.cognito_auth.id

  lambda_integrations = {
    "GET"    = { invoke_arn = module.lambda_functions["get-transaction-by-id"].invoke_arn, name = module.lambda_functions["get-transaction-by-id"].function_name },
    "PUT"    = { invoke_arn = module.lambda_functions["update-transaction"].invoke_arn, name = module.lambda_functions["update-transaction"].function_name },
    "DELETE" = { invoke_arn = module.lambda_functions["delete-transaction"].invoke_arn, name = module.lambda_functions["delete-transaction"].function_name }
  }
  depends_on = [aws_api_gateway_authorizer.cognito_auth]
}

module "api_categories" {
  source = "./modules/api_gateway_endpoint"

  rest_api_id            = aws_api_gateway_rest_api.main.id
  rest_api_execution_arn = aws_api_gateway_rest_api.main.execution_arn
  parent_id              = aws_api_gateway_rest_api.main.root_resource_id
  path_part              = "categories"
  authorizer_id          = aws_api_gateway_authorizer.cognito_auth.id

  lambda_integrations = {
    "POST" = { invoke_arn = module.lambda_functions["create-category"].invoke_arn, name = module.lambda_functions["create-category"].function_name },
    "GET"  = { invoke_arn = module.lambda_functions["get-categories"].invoke_arn, name = module.lambda_functions["get-categories"].function_name }
  }
  depends_on = [aws_api_gateway_authorizer.cognito_auth]
}

module "api_category_by_id" {
  source = "./modules/api_gateway_endpoint"

  rest_api_id            = aws_api_gateway_rest_api.main.id
  rest_api_execution_arn = aws_api_gateway_rest_api.main.execution_arn
  parent_id              = module.api_categories.resource_id
  path_part              = "{id}"
  authorizer_id          = aws_api_gateway_authorizer.cognito_auth.id

  lambda_integrations = {
    "PUT"    = { invoke_arn = module.lambda_functions["update-category"].invoke_arn, name = module.lambda_functions["update-category"].function_name },
    "DELETE" = { invoke_arn = module.lambda_functions["delete-category"].invoke_arn, name = module.lambda_functions["delete-category"].function_name }
  }
  depends_on = [aws_api_gateway_authorizer.cognito_auth]
}

module "api_saving_goals" {
  source = "./modules/api_gateway_endpoint"

  rest_api_id            = aws_api_gateway_rest_api.main.id
  rest_api_execution_arn = aws_api_gateway_rest_api.main.execution_arn
  parent_id              = aws_api_gateway_rest_api.main.root_resource_id
  path_part              = "saving-goals"
  authorizer_id          = aws_api_gateway_authorizer.cognito_auth.id

  lambda_integrations = {
    "POST" = { invoke_arn = module.lambda_functions["create-saving-goal"].invoke_arn, name = module.lambda_functions["create-saving-goal"].function_name },
    "GET"  = { invoke_arn = module.lambda_functions["get-saving-goals"].invoke_arn, name = module.lambda_functions["get-saving-goals"].function_name }
  }
  depends_on = [aws_api_gateway_authorizer.cognito_auth]
}

module "api_saving_goal_by_id" {
  source = "./modules/api_gateway_endpoint"

  rest_api_id            = aws_api_gateway_rest_api.main.id
  rest_api_execution_arn = aws_api_gateway_rest_api.main.execution_arn
  parent_id              = module.api_saving_goals.resource_id
  path_part              = "{id}"
  authorizer_id          = aws_api_gateway_authorizer.cognito_auth.id

  lambda_integrations = {
    "PUT"    = { invoke_arn = module.lambda_functions["update-saving-goal"].invoke_arn, name = module.lambda_functions["update-saving-goal"].function_name },
    "DELETE" = { invoke_arn = module.lambda_functions["delete-saving-goal"].invoke_arn, name = module.lambda_functions["delete-saving-goal"].function_name }
  }
  depends_on = [aws_api_gateway_authorizer.cognito_auth]
}

module "api_saving_goal_add_funds" {
  source = "./modules/api_gateway_endpoint"

  rest_api_id            = aws_api_gateway_rest_api.main.id
  rest_api_execution_arn = aws_api_gateway_rest_api.main.execution_arn
  parent_id              = module.api_saving_goal_by_id.resource_id
  path_part              = "add-funds"
  authorizer_id          = aws_api_gateway_authorizer.cognito_auth.id

  lambda_integrations = {
    "POST" = { invoke_arn = module.lambda_functions["add-funds-to-goal"].invoke_arn, name = module.lambda_functions["add-funds-to-goal"].function_name }
  }
  depends_on = [aws_api_gateway_authorizer.cognito_auth]
}


resource "aws_api_gateway_deployment" "main" {
  rest_api_id = aws_api_gateway_rest_api.main.id

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

  depends_on = [
    module.api_transactions,
    module.api_transaction_by_id,
    module.api_categories,
    module.api_category_by_id,
    module.api_saving_goals,
    module.api_saving_goal_by_id,
    module.api_saving_goal_add_funds
  ]
}

resource "aws_api_gateway_stage" "main" {
  deployment_id = aws_api_gateway_deployment.main.id
  rest_api_id   = aws_api_gateway_rest_api.main.id
  stage_name    = var.environment
}

resource "aws_api_gateway_method_settings" "all" {
  rest_api_id = aws_api_gateway_rest_api.main.id
  stage_name  = aws_api_gateway_stage.main.stage_name
  method_path = "*/*"

  settings {
    logging_level = "INFO"

    data_trace_enabled = true

    metrics_enabled = true
  }
}