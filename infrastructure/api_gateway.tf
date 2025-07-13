module "cloudfront_log_bucket" {
  source = "./modules/s3_bucket"

  bucket_prefix = "${var.project_name}-cf-logs"
  tags          = var.tags
}

resource "aws_s3_bucket_policy" "cloudfront_logs_policy" {
  bucket = module.cloudfront_log_bucket.bucket_name

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect    = "Allow"
        Principal = {
          Service = "cloudfront.amazonaws.com"
        }
        Action    = "s3:PutObject"
        Resource  = "${module.cloudfront_log_bucket.bucket_arn}/*"
        Condition = {
          StringEquals = {
            "AWS:SourceArn" = aws_cloudfront_distribution.main_distribution.arn
          }
        }
      }
    ]
  })
}

resource "aws_cloudfront_distribution" "main_distribution" {
  enabled         = true
  is_ipv6_enabled = true
  comment         = "Main distribution for the finance application API"

  origin {
    domain_name = split("/", replace(aws_api_gateway_stage.main.invoke_url, "https://", ""))[0]
    origin_path = "/${var.environment}"
    origin_id   = "APIGW-${aws_api_gateway_rest_api.main.name}"

    custom_origin_config {
      http_port              = 80
      https_port             = 443
      origin_protocol_policy = "https-only"
      origin_ssl_protocols   = ["TLSv1.2"]
    }
  }

  default_cache_behavior {
    allowed_methods = ["DELETE", "GET", "HEAD", "OPTIONS", "PATCH", "POST", "PUT"]
    cached_methods  = ["GET", "HEAD"]

    target_origin_id = "APIGW-${aws_api_gateway_rest_api.main.name}"

    viewer_protocol_policy = "redirect-to-https"

    forwarded_values {
      query_string = true
      headers      = ["*"]
      cookies {
        forward = "none"
      }
    }
  }

  logging_config {
    include_cookies = false
    bucket          = module.cloudfront_log_bucket.bucket_domain_name
    prefix          = "cdn-access-logs/"
  }

  viewer_certificate {
    cloudfront_default_certificate = true
  }

  restrictions {
    geo_restriction {
      restriction_type = "none"
    }
  }

  web_acl_id = null

  tags = var.tags
}

resource "aws_api_gateway_rest_api" "main" {
  name        = "${var.project_name}-${var.environment}-api"
  description = "API dla aplikacji do zarządzania finansami."
  tags        = var.tags

  endpoint_configuration {
    types = ["REGIONAL"]
  }
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
}

resource "aws_api_gateway_stage" "main" {
  deployment_id = aws_api_gateway_deployment.main.id
  rest_api_id   = aws_api_gateway_rest_api.main.id
  stage_name    = var.environment
}