resource "aws_lambda_function" "this" {
  function_name = var.function_name
  description   = var.description
  role          = var.iam_role_arn
  handler       = var.handler
  runtime       = var.runtime
  memory_size   = var.memory_size
  timeout       = var.timeout
  tags          = var.tags

  filename         = var.jar_path
  source_code_hash = filebase64sha256(var.jar_path)

  dynamic "vpc_config" {
    for_each = var.vpc_config != null ? [var.vpc_config] : []
    content {
      subnet_ids         = vpc_config.value.subnet_ids
      security_group_ids = vpc_config.value.security_group_ids
    }
  }

  environment {
    variables = var.environment_variables
  }
}