resource "aws_api_gateway_account" "main" {
  cloudwatch_role_arn = var.cloudwatch_role_arn
}

resource "aws_api_gateway_authorizer" "cognito_auth" {
  name          = "CognitoAuthorizer"
  type          = "COGNITO_USER_POOLS"
  rest_api_id   = aws_api_gateway_rest_api.main.id
  provider_arns = [var.user_pool_arn]
}

resource "aws_api_gateway_rest_api" "main" {
  name        = "${var.project_name}-${var.environment}-api"
  description = "Main API for ${var.project_name}"
}

module "api_transactions" {
  source = "./api_gateway_endpoint"

  rest_api_id            = aws_api_gateway_rest_api.main.id
  rest_api_execution_arn = aws_api_gateway_rest_api.main.execution_arn
  parent_id              = aws_api_gateway_rest_api.main.root_resource_id
  path_part              = "transactions"
  authorizer_id          = aws_api_gateway_authorizer.cognito_auth.id

  lambda_integrations = {
    "POST" = var.lambda_integrations["create-transaction"]
    "GET"  = var.lambda_integrations["get-transactions"]
  }
}

module "api_transaction_by_id" {
  source = "./api_gateway_endpoint"

  rest_api_id            = aws_api_gateway_rest_api.main.id
  rest_api_execution_arn = aws_api_gateway_rest_api.main.execution_arn
  parent_id              = module.api_transactions.resource_id
  path_part              = "{id}"
  authorizer_id          = aws_api_gateway_authorizer.cognito_auth.id

  lambda_integrations = {
    "GET"    = var.lambda_integrations["get-transaction-by-id"]
    "PUT"    = var.lambda_integrations["update-transaction"]
    "DELETE" = var.lambda_integrations["delete-transaction"]
  }
}

module "api_categories" {
  source = "./api_gateway_endpoint"

  rest_api_id            = aws_api_gateway_rest_api.main.id
  rest_api_execution_arn = aws_api_gateway_rest_api.main.execution_arn
  parent_id              = aws_api_gateway_rest_api.main.root_resource_id
  path_part              = "categories"
  authorizer_id          = aws_api_gateway_authorizer.cognito_auth.id

  lambda_integrations = {
    "POST" = var.lambda_integrations["create-category"]
    "GET"  = var.lambda_integrations["get-categories"]
  }
}

module "api_category_by_id" {
  source = "./api_gateway_endpoint"

  rest_api_id            = aws_api_gateway_rest_api.main.id
  rest_api_execution_arn = aws_api_gateway_rest_api.main.execution_arn
  parent_id              = module.api_categories.resource_id
  path_part              = "{id}"
  authorizer_id          = aws_api_gateway_authorizer.cognito_auth.id

  lambda_integrations = {
    "PUT"    = var.lambda_integrations["update-category"]
    "DELETE" = var.lambda_integrations["delete-category"]
  }
}

module "api_saving_goals" {
  source = "./api_gateway_endpoint"

  rest_api_id            = aws_api_gateway_rest_api.main.id
  rest_api_execution_arn = aws_api_gateway_rest_api.main.execution_arn
  parent_id              = aws_api_gateway_rest_api.main.root_resource_id
  path_part              = "saving-goals"
  authorizer_id          = aws_api_gateway_authorizer.cognito_auth.id

  lambda_integrations = {
    "POST" = var.lambda_integrations["create-saving-goal"]
    "GET"  = var.lambda_integrations["get-saving-goals"]
  }
}

module "api_saving_goal_by_id" {
  source = "./api_gateway_endpoint"

  rest_api_id            = aws_api_gateway_rest_api.main.id
  rest_api_execution_arn = aws_api_gateway_rest_api.main.execution_arn
  parent_id              = module.api_saving_goals.resource_id
  path_part              = "{id}"
  authorizer_id          = aws_api_gateway_authorizer.cognito_auth.id

  lambda_integrations = {
    "PUT"    = var.lambda_integrations["update-saving-goal"]
    "DELETE" = var.lambda_integrations["delete-saving-goal"]
  }
}

module "api_saving_goal_add_funds" {
  source = "./api_gateway_endpoint"

  rest_api_id            = aws_api_gateway_rest_api.main.id
  rest_api_execution_arn = aws_api_gateway_rest_api.main.execution_arn
  parent_id              = module.api_saving_goal_by_id.resource_id
  path_part              = "add-funds"
  authorizer_id          = aws_api_gateway_authorizer.cognito_auth.id

  lambda_integrations = {
    "POST" = var.lambda_integrations["add-funds-to-goal"]
  }
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
    logging_level      = "INFO"
    data_trace_enabled = true
    metrics_enabled    = true
  }
}
